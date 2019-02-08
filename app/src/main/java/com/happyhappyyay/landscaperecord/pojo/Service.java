package com.happyhappyyay.landscaperecord.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;

public class Service implements Parcelable {

    public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator<Service>() {
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        public Service[] newArray(int size) {
            return new Service[size];
        }
    };
    private int serviceID;
    private String services, username, customerName;
    private double materialCost;
    private double manHours;
    private double mileage;
    private List<Material> materials;
    //    TODO: get pause time, then pass time difference of pause and restart to accumulated time subtract from man hours
    private long startTime;
    private long endTime;
    private static int idCount = 0;
    private boolean priced;
//    private int accumulatedTime;
    private boolean pause;
    private boolean paid;

    public Service() {
        materials = new ArrayList<>();
        pause = true;
        priced = false;
        serviceID = idCount++;
    }

    public Service(Parcel in) {
        idCount = in.readInt();
        serviceID = in.readInt();
        services = in.readString();
        username = in.readString();
        customerName = in.readString();
        materialCost = in.readDouble();
        manHours = in.readDouble();
        mileage = in.readDouble();
        materials = new ArrayList<>();
        in.readTypedList(materials, Material.CREATOR);
        startTime = in.readLong();
        endTime = in.readLong();
        pause = in.readByte() != 0;
        priced = in.readByte() != 0;
        paid = in.readByte() != 0;
    }

    public void addMaterial(Material material) {
        materials.add(material);
    }

    public void removeMaterial(Material material) {
        materials.remove(material);
    }

    public double calculateMaterialCost() {
        materialCost = 0;
        if (materials != null) {
            for (Material material : materials) {
                materialCost += material.getMaterialPrice();
            }
        }
        return materialCost;
    }

    public String convertStartTimeToDateString() {
        String dateMessage = "";
        if (startTime >0) {
            dateMessage = Util.convertLongToStringDate(startTime);
        }
        return dateMessage;
    }

    public String convertEndTimeToDateString() {
        String dateMessage = "";
        if (endTime >0) {
            dateMessage = Util.convertLongToStringDate(endTime);
        }
        return dateMessage;
    }

    public String retrieveStartToEndString(){
        String dateRange = Util.convertLongToStringDate(startTime) + " - ";
        if(endTime > 0) {
            dateRange += Util.convertLongToStringDate(endTime);
        }
        return dateRange;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public double getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(double materialCost) {
        this.materialCost = materialCost;
    }

    public double getManHours() {
        return manHours;
    }

    public void setManHours(double manHours) {
        this.manHours = manHours;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public boolean isValid() {
        return startTime > 0;
    }

    public boolean isPriced() {
        return priced;
    }

    public void setPriced(boolean priced) {
        this.priced = priced;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idCount);
        parcel.writeInt(serviceID);
        parcel.writeString(services);
        parcel.writeString(username);
        parcel.writeString(customerName);
        parcel.writeDouble(materialCost);
        parcel.writeDouble(manHours);
        parcel.writeDouble(mileage);
        parcel.writeTypedList(materials);
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
        parcel.writeByte((byte) (pause? 1:0));
        parcel.writeByte((byte) (priced? 1:0));
        parcel.writeByte((byte) (paid? 1:0));
    }
}
