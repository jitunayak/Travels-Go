package com.esspl.jitu.mapclient.Authentication;

/**
 * Created by jitu on 3/16/18.
 */

public class UserInfo {


    String phone_number;
    String password;
    String email_id;

    public UserInfo(String phone_number, String password, String email_id) {
        this.phone_number = phone_number;
        this.password = password;
        this.email_id = email_id;
    }


    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

}