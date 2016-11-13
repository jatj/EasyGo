package com.example.abraham.proyectotema.com.example.abraham.proyectotema.model;

import android.graphics.drawable.Drawable;

/**
 * Created by abraham on 7/28/2016.
 */
public class appElement {
    String name;
    String packageName;
    Drawable img;

    public appElement(String name, String packageName, Drawable img) {
        this.name = name;
        this.packageName = packageName;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getImg() {
        return img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }
}
