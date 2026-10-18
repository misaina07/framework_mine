package model;

import mg.itu.myframework.model.MapFILE;

public class Employe {
    private String nom;
    private int age;
    private String email;
    private MapFILE rectoCIN;

    public Employe() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MapFILE getRectoCIN() {
        return rectoCIN;
    }

    public void setRectoCIN(MapFILE rectoCIN) {
        this.rectoCIN = rectoCIN;
    }
}