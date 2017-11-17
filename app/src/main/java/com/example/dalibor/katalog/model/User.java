package com.example.dalibor.katalog.model;

/**
 * Created by Employee on 14.11.2017..
 */

public class User {

    public User(String email, String password, String confirmpassword){
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
    }

    private String email;
    private String password;
    private String confirmpassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmpassword='" + confirmpassword + '\'' +
                '}';
    }
}
