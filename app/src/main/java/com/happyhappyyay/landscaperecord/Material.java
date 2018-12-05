package com.happyhappyyay.landscaperecord;

import android.os.Parcel;
import android.os.Parcelable;

public class Material implements Parcelable {

    private String materialName;
    private double materialPrice;
    private String materialType;
    private boolean addMaterial;
    private double materialQuantity;
    private String materialMeasurement;

    public Material(String materialName, String materialType, boolean addMaterial) {
        this.materialName = materialName;
        this.materialType = materialType;
        this.addMaterial = addMaterial;
    }

    public static final Parcelable.Creator<Material> CREATOR = new Parcelable.Creator<Material>() {
        public Material createFromParcel(Parcel in) {
            return new Material(in);
        }

        public Material[] newArray(int size) {
            return new Material[size];
        }
    };

    public Material(Parcel in) {
        materialName = in.readString();
        materialPrice = in.readDouble();
        materialType = in.readString();
        addMaterial = in.readByte() != 0;
        materialQuantity = in.readDouble();
        materialMeasurement = in.readString();
    }

    public String getMaterialName() {
        return materialName;
    }

    public double getMaterialPrice() {
        return materialPrice;
    }

    public void setMaterialPrice(double materialPrice) {
        this.materialPrice = materialPrice;
    }

    public String getMaterialType() {
        return materialType;
    }

    public double getMaterialQuantity() {
        return materialQuantity;
    }

    public void setMaterialQuantity(double materialQuantity) {
        this.materialQuantity = materialQuantity;
    }

    public String getMaterialMeasurement() {
        return materialMeasurement;
    }

    public void setMaterialMeasurement(String materialMeasurement) {
        this.materialMeasurement = materialMeasurement;
    }

    public boolean isAddMaterial() {
        return addMaterial;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if(addMaterial) {
            stringBuilder.append("Add ");
        }
        else {
            stringBuilder.append("Remove ");
        }

        if(materialName != null) {
            stringBuilder.append(materialName);
            stringBuilder.append(" ");
        }

        if(!materialType.toLowerCase().equals("other") & !materialType.isEmpty()) {
            stringBuilder.append(materialType);
            stringBuilder.append(" ");
        }

        if(materialQuantity > 0) {
            stringBuilder.append(materialQuantity);
            stringBuilder.append(" ");
        }
        else {
            stringBuilder.append("1 ");
        }

        stringBuilder.append(materialMeasurement);
        stringBuilder.append(" ");

        if(materialPrice > 0) {
            stringBuilder.append(materialPrice);
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
        parcel.writeString(materialName);
        parcel.writeDouble(materialPrice);
        parcel.writeString(materialType);
        parcel.writeByte((byte) (addMaterial? 1 : 0));
        parcel.writeDouble(materialQuantity);
        parcel.writeString(materialMeasurement);
    }
}
