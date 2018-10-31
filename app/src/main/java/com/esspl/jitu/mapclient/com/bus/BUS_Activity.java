package com.esspl.jitu.mapclient.com.bus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esspl.jitu.mapclient.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BUS_Activity extends AppCompatActivity {

    TextView source_textview;
    TextView destination_textview;
    Button search_button;
    ListView listview;

    String source;
    String destination;
    FirebaseDatabase database;

    ArrayList<String> sources;
    ArrayList<String> dests;

    String[] AvailableBuses= new String[15];
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_);
        source_textview = findViewById(R.id.source);
        destination_textview = findViewById(R.id.destination);
        search_button = findViewById(R.id.search_bus);
        listview = findViewById(R.id.BusList);
        progressDialog = new ProgressDialog(BUS_Activity.this);
        database = FirebaseDatabase.getInstance();

        sources = new ArrayList<>();
        dests = new ArrayList<>();


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listview.setAdapter(null);
                sources.clear();
                dests.clear();

                if (TextUtils.isDigitsOnly(source_textview.getText()) && TextUtils.isEmpty(destination_textview.getText())) {
                    Toast.makeText(getApplicationContext(), "Please fill up all the feilds", Toast.LENGTH_LONG).show();
                } else {
                    source = source_textview.getText().toString();
                    destination = destination_textview.getText().toString();
                    checkDatabase(source, destination);
                    Toast.makeText(getApplicationContext(),source+destination,Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void checkDatabase(final String source, final String destination) {
        progressDialog.setMessage("Fetching data");
        progressDialog.show();
        DatabaseReference databaseReference = database.getReference().child("Providers");
        Query query = databaseReference.orderByChild("type").equalTo("bus");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    int count = 0;
                    int c = 0;
                    for (int i = 0; i < dataSnapshot1.child("waypoints").getChildrenCount(); i++) {
                        // Toast.makeText(getApplicationContext(),"INSIDE FOR LOOPPPP",Toast.LENGTH_SHORT).show();
                        if (source.toLowerCase().equals(dataSnapshot1.child("waypoints").child(Integer.toString(i)).getValue().toString().toLowerCase())) {
                            count++;
                            //  Toast.makeText(getApplicationContext(),"INSIDE IF CLAUSE",Toast.LENGTH_SHORT).show();
                            Log.e("Bus SOURCE", dataSnapshot1.child("name").getValue().toString());
                            sources.add(dataSnapshot1.child("pid").getValue().toString());
                        }
                        if (destination.toLowerCase().equals(dataSnapshot1.child("waypoints").child(Integer.toString(i)).getValue().toString().toLowerCase())) {
                            count++;
                            Log.e("Bus DESTINATION" +
                                    "" +
                                    "", dataSnapshot1.child("pid").getValue().toString());
                            dests.add(dataSnapshot1.child("pid").getValue().toString());
                        }
                        c=0;
                        for (int p = 0; p < sources.size(); p++) {
                            for (int j = 0; j < dests.size(); j++) {
                                if (sources.get(p).equals(dests.get(j))) {
                                    Log.d("MATCHING ", "Found a match for the bus :" + sources.get(p));
                                    AvailableBuses[c] = sources.get(p);
                                    c++;
                                }
                            }
                        }
                        if (c == 0)
                        {
                            AvailableBuses[0] = "    <No Buses Available> ";
                            AvailableBuses[1] = "    ";

                        }
                    }
                }
                progressDialog.dismiss();
                show_in_listview();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void show_in_listview() {

        // progressBar.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, AvailableBuses);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getBaseContext(),"clciked "+parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
                if( parent.getItemAtPosition(position)!=null) {
                    // progressBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(getApplicationContext(), BusMapActivity.class);
                    intent.putExtra("BusName", parent.getItemAtPosition(position).toString());
                    startActivity(intent);
                }
            }
        });

    }


}