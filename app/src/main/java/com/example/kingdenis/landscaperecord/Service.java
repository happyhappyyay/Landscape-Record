package com.example.kingdenis.landscaperecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Service {

    private int serviceID;
    private ServiceType serviceType;
    private String services;
    private double materialCost;
    private double manHours;
    private List<Material> materials;
    private Date startDateTime;
    //    TODO: get pause time, then pass time difference of pause and restart to accumulated time subtract from man hours
    private Date pausedDateTime;
    private Date endDateTime;
    private long startTime;
    private long endTime;
    private int accumulatedTime;
    private boolean pause;

    public Service() {
        materials = new ArrayList<>();
        pause = true;
    }

    public void addMaterial(Material material) {
        materials.add(material);
    }

    public void removeMaterial(Material material) {
        if (materials.contains(material)) {
            materials.remove(material);
        }
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

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
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

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
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

    public boolean isValid() {
        return startTime > 0;
    }
}
