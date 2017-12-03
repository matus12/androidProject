package cz.muni.fi.pv256.movio2.uco_396496.myapplication.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import cz.muni.fi.pv256.movio2.uco_396496.myapplication.MainActivity;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.MoviesList;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    private MoviesList list;

    interface Download {
        @GET("3/discover/movie?primary_release_date.gte=2017-11-12&" +
                "primary_release_date.lte=2017-11-13&sort_by=popularity.desc&" +
                "with_original_language=en&api_key=4d1917c52de723c48c649b3eb9955c8f")
        Call<MoviesList> getMovies();
    }

    Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    Download service = mRetrofit.create(Download.class);

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent inten = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, inten, 0);

        String notificationContentText;

        if (isNetworkAvailable()) {
            notificationContentText = "Downloading content...";
        } else {
            notificationContentText = "No internet connection";
        }
        Notification n = new Notification.Builder(this)
                .setContentTitle("MovieDB app")
                .setContentText(notificationContentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(false).build();

        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);

        Call<MoviesList> call = service.getMovies();
        Callback<MoviesList> callback = new Callback<MoviesList>() {
            @Override
            public void onResponse(@NonNull Call<MoviesList> call, @NonNull Response<MoviesList> response) {
                list = response.body();
                notificationManager.cancel(0);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);

                if (list == null) {
                    Log.d("APP", "SERVICE: NULL");
                } else {
                    Log.d("APP", "SERVICE: NOT NULL");
                    Log.d("APP", list.getResults().get(0).getOriginal_title());
                }

                broadcastIntent.putExtra("movies", list);
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesList> call, @NonNull Throwable t) {
                Log.d("APP", "FAIL");
            }
        };
        call.enqueue(callback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_NOT_STICKY;
    }

}
