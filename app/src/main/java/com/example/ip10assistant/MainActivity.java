package com.example.ip10assistant;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ip10assistant.client.ApiClient;
import com.example.ip10assistant.client.ApiInterface;

public class MainActivity extends AppCompatActivity {
    private static final int JOB_ID = 10;
    public static TextView tvIp;
    public ApiInterface mApiInterface;
    Button btnStart, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvIp = findViewById(R.id.idTvIp);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnStart.setOnClickListener(view -> startJob());
        btnCancel.setOnClickListener(view -> cancelJob());

    }

    private void startJob() {
        if (isJobRunning(this)) {
            Toast.makeText(this, "Job Service is already scheduled", Toast.LENGTH_SHORT).show();
            return;
        }
        ComponentName mServiceComponent = new ComponentName(this, GetCurrentWeatherJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, mServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        // 1000 ms = 1 detik
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(900000); //15 menit
            Log.d("MAIN", "startJob: 15");
        } else {
            builder.setPeriodic(180000); //3 menit
            Log.d("MAIN", "startJob: 3");
        }
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
        Toast.makeText(this, "Job Service started", Toast.LENGTH_SHORT).show();
    }

    private void cancelJob() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(JOB_ID);
        Toast.makeText(this, "Job Service canceled", Toast.LENGTH_SHORT).show();
    }

    private boolean isJobRunning(Context context) {
        boolean isScheduled = false;
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                if (jobInfo.getId() == JOB_ID) {
                    isScheduled = true;
                    break;
                }
            }
        }
        return isScheduled;
    }
}