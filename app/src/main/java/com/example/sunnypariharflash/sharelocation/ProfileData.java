package com.example.sunnypariharflash.sharelocation;

/**
 * Created by Sunny Parihar on 08-03-2018.
 */

public class ProfileData {
    String name;
    String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProfileData(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public ProfileData(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
