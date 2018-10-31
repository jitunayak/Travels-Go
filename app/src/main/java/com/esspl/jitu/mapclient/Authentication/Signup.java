package com.esspl.jitu.mapclient.Authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esspl.jitu.mapclient.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    EditText email;
    EditText phone;
    EditText password;
    EditText cpassword;
    FirebaseDatabase fd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);
        password = (EditText)findViewById(R.id.password1);
        cpassword = (EditText)findViewById(R.id.password2);

    }
    public void signup(View view){
        boolean field_checked = check_textboxes();
        if(field_checked) {
            fd = FirebaseDatabase.getInstance();
            DatabaseReference myRef = fd.getReference("userslist").child(phone.getText().toString());

            String f_phone = phone.getText().toString();
            String f_pass = password.getText().toString();
            String f_email = email.getText().toString();

            UserInfo user = new UserInfo(f_phone,f_pass,f_email);
            myRef.setValue(user);

            success_dialoge(view);


        }

    }


    public void success_dialoge(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("press ok to login to your account")
        .setTitle("SUCCESFULLY CREATED");

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(Signup.this,HomeActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
    }

    private boolean check_textboxes() {
        boolean checked = true;


        // Reset errors.
        email.setError(null);
        password.setError(null);

        String u_email = email.getText().toString();
        String u_pass1 = password.getText().toString();
        String u_pass2 = cpassword.getText().toString();
        String u_phone = phone.getText().toString();

        if (TextUtils.isEmpty(u_email) && !isEmailValid(u_email)) {
            email.setError("email address is not valid");
            checked=false;

        }
        if (!TextUtils.isEmpty(u_pass1) && !isPasswordValid(u_pass1)) {
            password.setError("password must be of 8 charcaters");
            checked=false;

        }
        if (TextUtils.isEmpty(u_phone) && (u_phone.length()==10)) {
            phone.setError("phone no. is invalid");
            checked=false;

        }
         if (!u_pass1.equals(u_pass2)) {
            cpassword.setError("Passwords do no match");
            checked=false;

        }
        if (!isEmailValid(u_email)) {
            email.setError("This email id Not valid");
            checked=false;
        }

        return checked;

    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }
}
