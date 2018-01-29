package cz.muni.fi.pv256.movio2.uco_396496.myapplication.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import cz.muni.fi.pv256.movio2.uco_396496.myapplication.MainActivity;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.MovieInfo;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.R;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.Movie;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.MovieDbManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

import static android.content.Context.NOTIFICATION_SERVICE;

public class UpdaterSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the server, in seconds.
    public static final int SYNC_INTERVAL = 60 * 60 * 24; //day
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public UpdaterSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(Bundle.EMPTY) //enter non null Bundle, otherwise on some phones it crashes sync
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one if the
     * fake account doesn't exist yet.  If we make a new account, we call the onAccountCreated
     * method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        UpdaterSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    interface Download {
        @GET
        Call<MovieInfo> getMovie(@Url String url);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Download service = mRetrofit.create(Download.class);
        final MovieDbManager movieDbManager = new MovieDbManager(getContext());

        List<Movie> movies = movieDbManager.getMovies();
        for (int i = 0; i < movies.size(); i++) {
            Call<MovieInfo> call = service.getMovie("https://api.themoviedb.org/3/movie/" + movies.get(i).getMovie_id() + "?api_key=4d1917c52de723c48c649b3eb9955c8f");
            final Movie savedMovie = movies.get(i);
            Callback<MovieInfo> callback = new Callback<MovieInfo>() {
                @Override
                public void onResponse(@NonNull Call<MovieInfo> call, @NonNull Response<MovieInfo> response) {
                    MovieInfo downloadedMovie = response.body();
                    if ((!savedMovie.getRelease_date().equals(downloadedMovie.getRelease_date())) ||
                            (!savedMovie.getOverview().equals(downloadedMovie.getOverview())) ||
                            (!savedMovie.getTitle().equals(downloadedMovie.getOriginal_title())) ||
                            (!savedMovie.getRating().equals(downloadedMovie.getVote_average()))){
                        final Movie movie = new Movie();
                        movie.setTitle(downloadedMovie.getOriginal_title());
                        movie.setOverview(downloadedMovie.getOverview());
                        movie.setRelease_date(downloadedMovie.getRelease_date());
                        movie.setRating(downloadedMovie.getVote_average());
                        movie.setPoster_path(downloadedMovie.getPoster_path());
                        movie.setMovie_id(downloadedMovie.getMovie_id());

                        movieDbManager.deleteMovie(savedMovie.getTitle());
                        movieDbManager.createMovie(movie);

                        Intent inten = new Intent(getContext(), MainActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(getContext(), 0, inten, 0);

                        Notification n = new Notification.Builder(getContext())
                                .setContentTitle("Movie update")
                                .setContentText("Movie " + movie.getTitle() + " updated!")
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentIntent(pIntent)
                                .setAutoCancel(false).build();

                        NotificationManager mNotificationManager =
                                (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
                        mNotificationManager.notify(0, n);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieInfo> call, @NonNull Throwable t) {
                    Log.d("APP", "FAIL");
                }
            };
            call.enqueue(callback);
        }
    }
}
