package com.happyhappyyay.landscaperecord.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Material implements Parcelable {

    private String name;
    private double price;
    private String type;
    private boolean add;
    private double quantity;
    private String measurement;

    public static final Parcelable.Creator<Material> CREATOR = new Parcelable.Creator<Material>() {
        public Material createFromParcel(Parcel in) {
            return new Material(in);
        }

        public Material[] newArray(int size) {
            return new Material[size];
        }
    };

    public Material(String materialName, String materialType, boolean addMaterial) {
        this.name = materialName;
        this.type = materialType;
        this.add = addMaterial;
    }

    public Material(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        type = in.readString();
        add = in.readByte() != 0;
        quantity = in.readDouble();
        measurement = in.readString();
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public boolean isAdd() {
        return add;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if(add) {
            stringBuilder.append("Add ");
        }
        else {
            stringBuilder.append("Remove ");
        }

        if(name != null) {
            stringBuilder.append(name);
            stringBuilder.append(" ");
        }

        if(!type.toLowerCase().equals("other") & !type.isEmpty()) {
            stringBuilder.append(type);
            stringBuilder.append(" ");
        }

        if(quantity > 0) {
            stringBuilder.append(quantity);
            stringBuilder.append(" ");
        }
        else {
            stringBuilder.append("1 ");
        }

        stringBuilder.append(measurement);
        stringBuilder.append(" ");

        if(price > 0) {
            stringBuilder.append(price);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeString(type);
        parcel.writeByte((byte) (add ? 1 : 0));
        parcel.writeDouble(quantity);
        parcel.writeString(measurement);
    }
}
