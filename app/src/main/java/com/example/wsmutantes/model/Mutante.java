package com.example.wsmutantes.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Mutante implements Serializable {

    private int id;
    private List<String> skills;
    private String nome;
    private String image;
    private Bitmap imgbm;

    public Mutante() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> s) {
        this.skills = s;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getImgbm() {
        return imgbm;
    }

    public void setImgbm(Bitmap imgbm) {
        this.imgbm = imgbm;
    }
}
