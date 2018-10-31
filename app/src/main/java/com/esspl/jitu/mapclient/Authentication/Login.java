package com.esspl.jitu.mapclient.Authentication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esspl.jitu.mapclient.MapsActivity;
import com.esspl.jitu.mapclient.R;
import com.esspl.jitu.mapclient.cab_bus_choose;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText phone;
    private EditText pass;
    private Button login;
    private TextView forget_password;
    private String password;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = findViewById(R.id.phone_number);
        pass = findViewById(R.id.password);
        login  =findViewById(R.id.UserLogin);
        progressDialog = new ProgressDialog(Login.this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), cab_bus_choose.class);
                startActivity(intent);

                boolean field_checked = check_textboxes();
                if(field_checked) {
                    Log.d("field Verification","All Fields are filled up correctly");
                    String u_phone = phone.getText().toString();
                    String u_pass = pass.getText().toString();

                   firebase_user_verification(u_phone, u_pass);

                }
                else
                {
                    Log.d("field Verification","every field is mandatory");
                }

            }
        });
    }

    private void moveTOmap() {
        Intent intent = new Intent(this, cab_bus_choose.class);
        startActivity(intent);
        finish();
    }

    private boolean check_textboxes() {
        boolean checked = true;


        // Reset errors.
        phone.setError(null);
        pass.setError(null);

        String u_phone = phone.getText().toString();
        String u_pass = pass.getText().toString();

        if (!TextUtils.isEmpty(u_pass) && !isPasswordValid(u_pass)) {
            pass.setError("password must be of 8 characters");
            checked=false;

        } if (TextUtils.isEmpty(u_phone)) {
            phone.setError("can't be empty");
            checked=false;

        } if (!isPhoneValid(u_phone)) {
            phone.setError("Must be of 10 digit");
            checked=false;
        }

        return checked;

    }
        private boolean isPhoneValid(String u_phone) {
            return phone.length()>9;
        }

        private boolean isPasswordValid(String password) {
            return password.length() > 3;
        }


    private boolean firebase_user_verification(String u_phone, String u_pass) {
        progressDialog.setMessage("Signing in");
        progressDialog.show();
        boolean conn = false;
            password = u_pass;
            Log.d("firebase","inside verification method");
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference("userslist").child(u_phone);
           myRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                 if(password.equals(dataSnapshot.child("password").getValue().toString()))
                 {
                     Toast.makeText(getApplicationContext(),"User exists",Toast.LENGTH_SHORT).show();
                     moveTOmap();
                     progressDialog.dismiss();

                 }
                 else
                     login_failed();

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });

        return conn;
    }
    public void login_failed(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Html.fromHtml("<font color='#e41a04'>ACCOUNT DOES NOT EXIST</font>"));
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(Login.this,HomeActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
    }
}
