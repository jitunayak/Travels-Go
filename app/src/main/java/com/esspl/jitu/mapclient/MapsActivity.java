package com.esspl.jitu.mapclient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        LocationListener

        {

    EditText searchText;
    Button searchButton;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleAPIClient;
    ArrayList<LatLng>  marker;
    ArrayList<LatLng>  drivermarker_list;
    Marker driverMarker;
    Marker dragableMarker;
    LocationManager locationManager;
    public LatLng destination;
    public LatLng source;
    boolean cab_clicked_or_not =false;
    String global_source_locality;
    ArrayList<LatLng> latLngs;
    ArrayList<String> names;
    TextView driver_name_textview;
    TextView cab_name_textview;

    String driver_name;
    String cab_name;
    String calling_phone_number;

    Double pass_lat =0.0;
    Double pass_long = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        marker = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latLngs = new ArrayList<LatLng>();
        names = new ArrayList<String>();

        driver_name_textview = findViewById(R.id.driver_name);
        cab_name_textview = findViewById(R.id.cab_number);

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
        mMap.setOnMyLocationButtonClickListener(this);
        Toast.makeText(this,"Choose your pick up location",Toast.LENGTH_LONG).show();


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                LatLng newlocation = marker.getPosition();
                destination = newlocation;
                drawtheline();

            }
        });

        //on map click marker creater
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (marker.size() == 0) {
                    MarkerOptions options = new MarkerOptions()
                            .title("pick up point")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                            .snippet("cab will pick up you from here")
                            .draggable(true)
                            .position(latLng);
                   dragableMarker= mMap.addMarker(options);
                    marker.add(latLng);
                    source = new LatLng(latLng.latitude, latLng.longitude);
                    Log.d("source", "marker is added ");
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
//                else if(marker.size() == 1)
//                { MarkerOptions options = new MarkerOptions()
//                        .title("Destination ")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                        .snippet("Cab will drop you heree")
//                        .position(latLng);
//                    mMap.addMarker(options);
//                    marker.add(latLng);
//                    source = new LatLng(latLng.latitude, latLng.longitude);
//                    Log.d("source", "marker is added ");
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    Toast.makeText(MapsActivity.this,"Destination",Toast.LENGTH_LONG).show();
//                    destination = latLng;;
//
//                }
                else if (marker.size() == 2)
                {
                    marker.clear();
                    mMap.clear();
                }
            }
        });

    }


//TODO:Destination marker
    public void destinationMarker(double lat ,double lang) {

        Toast.makeText(this, "choose your cab", Toast.LENGTH_LONG).show();
        if (marker.size() == 1) {

        } else if (marker.size() == 2) {
            marker.clear();
        }
        MarkerOptions options = new MarkerOptions()
                .title("Destination ")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet("Cab will drop you here")
                .draggable(true)
                .position(new LatLng(lat, lang));

        dragableMarker = mMap.addMarker(options);
        marker.add(new LatLng(lat, lang));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lang), 12));
        destination = new LatLng(lat, lang);
        Log.d("Destination", "destiantion is set  : " + destination.toString());
        drawtheline();
    }
    public void drawtheline()
    {

        String url = getUrl(source, destination);
        Log.d("cab booking","source"+source+" destination "+destination);
        Log.d("onMapClick", url.toString());
        MapsActivity.FetchUrl FetchUrl = new MapsActivity.FetchUrl();

        FetchUrl.execute(url);
    }


            //TODO:uber driver marker
    public void driverMarker(double lat, double lang)
    {
        Log.d("uber wala with values: "," "+lat+" "+lang+" " );
        LatLng driver_location = new LatLng(lat,lang);
        MarkerOptions options = new MarkerOptions()
                .title("Cab driver ")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab))
                .snippet("Looking for a cab")
                .position(driver_location);

       driverMarker = mMap.addMarker(options);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("Marker clicked",marker.getTag().toString());
                calling_phone_number = marker.getTag().toString();
                update_driver_details();
                Toast.makeText(getApplicationContext(),calling_phone_number,Toast.LENGTH_LONG).show();
                return true;
            }
        });
        // drivermarker_list.add(driver_location);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lang),15));

    }

    public void update_driver_details()
    {

        final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Providers");
        Query query = mFirebaseDatabaseReference.orderByChild("phone").equalTo(calling_phone_number);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("datasnapshot",dataSnapshot.child("lat").getValue().toString());
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Log.e("Datasnapshot 1 ",dataSnapshot1.getKey());

                    calling_phone_number = dataSnapshot1.child("phone").getValue().toString();
                    driver_name  = dataSnapshot1.child("name").getValue().toString();
                    cab_name  = dataSnapshot1.child("pid").getValue().toString();
                    Log.d("cab marker",driverMarker.getId());
                    driver_name_textview.setText(driver_name);
                    cab_name_textview.setText(cab_name);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //TODO:Search button clicked



    public void Search_button_clicked(View view)
    {
        double lat=0,lang=0;
        searchText = findViewById(R.id.searchText);
        String destination_text = searchText.getText().toString();
        Geocoder gc = new Geocoder(this);
        try {
            List<Address> list = gc.getFromLocationName(destination_text,3);
            Address address = list.get(0);
            lat = address.getLatitude();
            lang = address.getLongitude();
            destinationMarker(lat,lang);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    //TODO:when cab button is selected
    public void select_cab(View view) {

        double log;
        show_cabs();
       // driverMarker(20.2189431,85.733845);

    Toast.makeText(MapsActivity.this," locality "+global_source_locality,Toast.LENGTH_SHORT).show();



        final DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Providers");
        Query query = mFirebaseDatabaseReference.orderByChild("type").equalTo("cab");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // Log.e("datasnapshot",dataSnapshot.child("lat").getValue().toString());
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Log.e("Datasnapshot 1 ",dataSnapshot1.getKey());

                   pass_lat=Double.parseDouble(dataSnapshot1.child("lat").getValue().toString());
                   pass_long=Double.parseDouble(dataSnapshot1.child("long").getValue().toString());
                   calling_phone_number = dataSnapshot1.child("phone").getValue().toString();
                   driver_name  = dataSnapshot1.child("name").getValue().toString();
                    driverMarker(pass_lat,pass_long);
                    Log.d("cab marker",driverMarker.getId());
                   // mFirebaseDatabaseReference.child(dataSnapshot1.getKey()).child("markerID").setValue(driverMarker.getId());
                    driverMarker.setTag(calling_phone_number);



                        }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }


            public void show_cabs()
    {

            Log.d("Locality : ", "cab button is pressed");
            try {
                Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(source.latitude,source.longitude, 1);
                if (addresses.isEmpty()) {
                    Toast.makeText(MapsActivity.this, "unable toidentify Locality", Toast.LENGTH_SHORT).show();
                } else {
                    if (addresses.size() > 0) {

                        Log.d("", "Locality : " + addresses.get(0).getLocality());
                        global_source_locality=addresses.get(0).getLocality().toString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

        }

//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference driverLocationref = database.getReference("DriverLocation");
//
//
//
//        MarkerOptions options = new MarkerOptions()
//                .title("Destination ")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                .snippet("Cab will drop you heree")
//                .draggable(true)
//                .position(new LatLng(lat, lang));
//
//        driverMarker = mMap.addMarker(options);


    }

    public void cab_booking(View view) {
        if (marker == null) {
            Toast.makeText(this, "select a cab first", Toast.LENGTH_SHORT).show();
        } else {
            if(calling_phone_number==null){
                Toast.makeText(this,"Please select a driver",Toast.LENGTH_LONG).show();
            }
            else {

              //  Toast.makeText(this,"NUMBEr IS NOT NULL",Toast.LENGTH_LONG).show();
                String phone = calling_phone_number;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);

            }

        }
    }




            @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this,"Turn o your GPS",Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this,"GPS connection is failed",Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this,"going to my location",Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(this,"My location is "+location,Toast.LENGTH_SHORT).show();
        source =  new LatLng(location.getLatitude(),location.getLongitude());


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


    @Override
    public void onMapClick(LatLng latLng) {



    }
    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



            // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

}
