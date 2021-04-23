package com.example.ip10assistant;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ip10assistant.client.ApiClient;
import com.example.ip10assistant.client.ApiInterface;
import com.example.ip10assistant.model.IpData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView tvIp;
    ApiInterface mApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvIp = findViewById(R.id.idTvIp);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        getIP();
    }

    public void getIP() {
        Call<IpData> ip = mApiInterface.getIp();
        ip.enqueue(new Callback<IpData>() {
            @Override
            public void onResponse(Call<IpData> call, Response<IpData> response) {
                Log.d("Cek IP via terminal", "onResponse: " + response.body().getIp());
                tvIp.setText("IP Anda : \n" + response.body().getIp());
            }

            @Override
            public void onFailure(Call<IpData> call, Throwable t) {

            }
        });
    }
}