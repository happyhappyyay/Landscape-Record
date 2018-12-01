package com.happyhappyyay.landscaperecord;

public class Material {

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
}
