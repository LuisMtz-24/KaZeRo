package com.example.kazero.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String address;
    private String postalCode;

    public User() {
    }

    public User(String username, String password, String email, String address, String postalCode) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
    }

    public User(int id, String username, String email, String address, String postalCode) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}