package com.esspl.jitu.mapclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.esspl.jitu.mapclient.com.bus.BUS_Activity;

public class cab_bus_choose extends AppCompatActivity {

    Button cab_button;
    Button  bus_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_bus_choose);
cab_button = findViewById(R.id.choose_cab);
bus_button = findViewById(R.id.choose_bus);

cab_button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(cab_bus_choose.this,MapsActivity.class);
        startActivity(intent);
    }
});

bus_button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent2 = new Intent(cab_bus_choose.this,BUS_Activity.class);
        startActivity(intent2);
    }
});
    }
}
