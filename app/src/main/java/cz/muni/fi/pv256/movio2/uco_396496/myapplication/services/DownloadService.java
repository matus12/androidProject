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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.muni.fi.pv256.movio2.uco_396496.myapplication.Data;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.MainActivity;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.MoviesList;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class DownloadService extends IntentService {
    private Context mContext;
    private List<RequestCreator> mRequestCreators;
    private String fromYear, fromMonth, fromDay, toYear, toMonth, toDay;
    private Calendar calendar;

    public DownloadService() {
        super("DownloadService");
        mContext = this;
        mRequestCreators = new ArrayList<>();
        Date currentTime = Calendar.getInstance().getTime();
        calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        fromYear = Integer.toString(calendar.get(Calendar.YEAR));
        fromMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        fromDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        toYear = Integer.toString(calendar.get(Calendar.YEAR));
        toMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        toDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
    }

    private MoviesList list;

    interface Download {
        @GET
        Call<MoviesList> getMovies(@Url String url);
        @GET
        Call<MoviesList> getInCinemas(@Url String url);
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

        String url = getUrl(
                fromYear,
                fromMonth,
                fromDay,
                toYear,
                toMonth,
                toDay
        );

        Call<MoviesList> call = service.getMovies(url);
        Callback<MoviesList> callback = new Callback<MoviesList>() {
            @Override
            public void onResponse(@NonNull Call<MoviesList> call, @NonNull Response<MoviesList> response) {
                list = response.body();
                for(int i = 0; i < list.getResults().size(); i++){
                    mRequestCreators.add(Picasso.with(mContext)
                            .load("https://image.tmdb.org/t/p/w500/"
                                    + list.getResults().get(i).getPoster_path()));
                }

                Data.getInstance().setData(mRequestCreators);
                Data.getInstance().setDefaultCreator(Picasso.with(mContext)
                        .load("http://www.christophergrantharvey.com/uploads/4/3/2/3/4323645/" +
                                "movie-poster-coming-soon_2_orig.png"));

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra("movies", list);
                notificationManager.cancel(0);
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesList> call, @NonNull Throwable t) {
                Log.d("APP", "FAIL");
            }
        };
        call.enqueue(callback);
    }

    @NonNull
    private String getUrl(String fromYear, String fromMonth, String fromDay, String toYear, String toMonth, String toDay) {
        return "https://api.themoviedb.org/3/discover/movie?primary_release_date.gte="
                    + fromYear + "-" + fromMonth + "-" + fromDay
                    + "&primary_release_date.lte="
                    + toYear + "-" + toMonth + "-" + toDay +
                    "&sort_by=popularity.desc&with_original_language=en&" +
                    "api_key=4d1917c52de723c48c649b3eb9955c8f";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_NOT_STICKY;
    }

}
