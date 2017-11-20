package com.example.pyae.communication_services;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    LocationTracker locationTrackerService;
    Intent serviceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById(R.id.btnStartLocationTracking);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnButtonClick(view);
            }
        });

        Button stopButton=(Button)findViewById(R.id.btnStopLocationTracking);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnButtonClick(view);
            }
        });

         serviceIntent = new Intent(this, LocationTracker.class);
    }

    private void OnButtonClick(View view) {
        switch (view.getId()){
            case R.id.btnStartLocationTracking:
                locationTrackerService.startTracking();
                break;
            case R.id.btnStopLocationTracking:
                locationTrackerService.stopTracking();
                break;
            default:break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(serviceIntent);
        bindService(serviceIntent,locationServiceConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!locationTrackerService.isTracking()){
            stopService(serviceIntent);
        }
        unbindService(locationServiceConnection);
    }

    private ServiceConnection locationServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            locationTrackerService=  ((LocationTracker.TrackerBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationTrackerService=null;
        }
    };
}
