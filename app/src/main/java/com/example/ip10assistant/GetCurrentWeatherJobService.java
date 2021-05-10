package com.example.ip10assistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.ip10assistant.client.ApiClient;
import com.example.ip10assistant.client.ApiInterface;
import com.example.ip10assistant.model.IpData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetCurrentWeatherJobService extends JobService {

    public static final String TAG = GetCurrentWeatherJobService.class.getSimpleName();

    public ApiInterface mApiInterface;

    String ipResult;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob() Executed");
        getCurrentIP(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob() Executed");
        return true;
    }

    private void getCurrentIP(final JobParameters job) {
        Log.d(TAG, "Running");
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<IpData> ip = mApiInterface.getIp();
        ip.enqueue(new Callback<IpData>() {
            @Override
            public void onResponse(Call<IpData> call, Response<IpData> response) {
                Log.d("Cek IP via terminal", "onResponse: " + response.body().getIp());
                ipResult = response.body().getIp();
                //ipResult = "10.252.68.1";
                MainActivity.tvIp.setText(ipResult);
                if (ipResult.startsWith("10.")) {
                    int notifId = 101;
                    String title = "Peringatan IP 10";
                    String message = "IP anda saat ini adalah " + response.body().getIp() + ". Segera restart Access Point anda!";
                    showNotification(getApplicationContext(), title, message, notifId);
                }
            }

            @Override
            public void onFailure(Call<IpData> call, Throwable t) {
                jobFinished(job, true);
            }
        });
    }

    private void showNotification(Context context, String title, String message, int notifId) {
        String CHANNEL_ID = "Channel_1";
        String CHANNEL_NAME = "Job scheduler channel";

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_baseline_network_wifi_24)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();
        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, notification);
        }
    }
}
