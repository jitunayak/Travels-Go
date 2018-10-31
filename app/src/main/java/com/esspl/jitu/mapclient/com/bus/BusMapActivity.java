package com.esspl.jitu.mapclient.com.bus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esspl.jitu.mapclient.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BusMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    FirebaseDatabase database;
    Double lat ;
    Double lng;
    GoogleMap mMap;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_map);
        Intent intent = getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new ProgressDialog(BusMapActivity.this);
        final String bus_name = intent.getStringExtra("BusName");

        DatabaseReference BusRef = FirebaseDatabase.getInstance().getReference().child("Providers");
        Query query = BusRef.orderByChild("type").equalTo("bus");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    if(bus_name.equals(dataSnapshot1.child("pid").getValue().toString()))
                    {
                        Toast.makeText(getApplicationContext(),dataSnapshot1.child("lat").getValue().toString(),Toast.LENGTH_SHORT).show();
                        lat = Double.parseDouble(dataSnapshot1.child("lat").getValue().toString());
                        lng =  Double.parseDouble(dataSnapshot1.child("long").getValue().toString());
                        show_bus(lat,lng);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void show_bus(Double lat, Double lng) {

        LatLng source = new LatLng(lat, lng);
        MarkerOptions options = new MarkerOptions()
                .title("BUS CURRENT POSITION")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                .snippet("bus is on the way")
                .draggable(true)
                .position(source);

        mMap.addMarker(options);

        Log.d("source", "marker is added ");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source,12));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)&&(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION))){

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }
        mMap.setMyLocationEnabled(true);

    }
}
