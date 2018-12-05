package com.happyhappyyay.landscaperecord;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class LandscapingMaterialsViewModel extends ViewModel {
    private List<Material> materials;
    public List<Material> getMaterials() {
        if (materials == null) {
            materials = new ArrayList<>();
        }
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}
