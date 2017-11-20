package com.example.pyae.communication_services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Pyae on 6/12/2017.
 */

public class LocationTracker extends Service implements LocationListener {

    Log log;
    private static final String LocationTrackerService = "LocationTrackerService";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60 * 1;
    private LocationManager manager;
    private boolean isTracking = false;

    @Override
    public void onCreate() {
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        log.i(LocationTrackerService, "Loc Tracking Service Running....");
    }

    public void startTracking() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Pls Enable GPS!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Starting Loc Tracer", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            log.d("GPS", "GPS Location");
            DispalyGPSorNetworkLocation(location);
        } else {
            DisplayNetWorkLocation();
        }
        isTracking = true;
    }

    public void stopTracking() {
        Toast.makeText(this, "Stopping Location Tracker", Toast.LENGTH_SHORT).show();
        manager.removeUpdates(this);
        isTracking = false;
    }

    public boolean isTracking() {
        return isTracking;
    }

    @Override
    public void onDestroy() {
        manager.removeUpdates(this);
        log.i(LocationTrackerService, "Location Tracking Service Stopped....");
    }

    public class TrackerBinder extends Binder {
        LocationTracker getService() {
            return LocationTracker.this;
        }
    }

    private final IBinder binder = new TrackerBinder();



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void DisplayNetWorkLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        log.d("NetWork","NetWork Location");
        if(manager != null){
            Location location= manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null){
                DispalyGPSorNetworkLocation(location);
            }
        }
    }

    private void DispalyGPSorNetworkLocation(Location location) {
        String latitude = String.valueOf(location.getLatitude());
        String longitude= String.valueOf(location.getLongitude());
        String sLocation=String.format("lat: %s, Long: %s",latitude,longitude);
        Toast.makeText(this,sLocation,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {
        log.i("TrackerService","location changed");
        if(location!=null)
            DispalyGPSorNetworkLocation(location);
        else{
            DisplayNetWorkLocation();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
