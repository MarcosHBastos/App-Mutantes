package com.example.wsmutantes.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class Mutante implements Serializable {

    private int id;
    private List<String> skills;
    private String nome;

    private String imageFileName;
    private String image;

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

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String image) {
        this.imageFileName = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
