package com.esspl.jitu.mapclient.Authentication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esspl.jitu.mapclient.MapsActivity;
import com.esspl.jitu.mapclient.R;

public class HomeActivity extends AppCompatActivity implements LocationListener{

    Button login ;
    Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activitty);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }

        }
    }



    public void login(View v)
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void signup(View v)
    {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }

    public void sos(View view) {
        Toast.makeText(this,"SOS is sent for help",Toast.LENGTH_SHORT).show();
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,5, this);

    }
    catch (SecurityException e)
    {
    e.printStackTrace();
    }
    }

    @Override
    public void onLocationChanged(Location location) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+918658087043",null,
                "This is an SOS help message \n my location is:\n\n http://maps.google.com/?q="+location.getLatitude()+","+location.getLongitude(),
                null,null);
      //  Snackbar snackbar = new Snackbar("")
        Toast.makeText(this,"msg sent to Hospital",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
