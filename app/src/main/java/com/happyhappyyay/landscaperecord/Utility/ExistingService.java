package com.happyhappyyay.landscaperecord.Utility;

import com.happyhappyyay.landscaperecord.POJO.Material;

import java.util.List;

public class ExistingService {
    private static ExistingService instance;
    private String services;
    private List<Material> materials;

    public static ExistingService getExistingService() {
        if (instance == null) {
            instance = new ExistingService();
        }
        return instance;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public void clearServices() {
        services = null;
    }

    public void clearMaterials() {
        materials = null;
    }
}
