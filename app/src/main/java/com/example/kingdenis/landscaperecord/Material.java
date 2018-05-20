package com.example.kingdenis.landscaperecord;

public class Material {

    private String materialName;
    private double materialPrice;
    private MaterialType materialType;
    private boolean addMaterial;
    private double quantity;
    private String measurement;

    public Material(String materialName, MaterialType materialType, boolean addMaterial) {
        this.materialName = materialName;
        this.materialType = materialType;
        this.addMaterial = addMaterial;
    }

    public String getMaterialName() {
        return materialName;
    }

    public double getMaterialPrice() {
        return materialPrice;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public boolean isAddMaterial() {
        return addMaterial;
    }

    public void setMaterialPrice(double materialPrice) {
        this.materialPrice = materialPrice;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }
}
