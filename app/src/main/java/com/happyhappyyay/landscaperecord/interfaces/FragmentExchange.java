package com.happyhappyyay.landscaperecord.interfaces;

import com.happyhappyyay.landscaperecord.pojo.Material;

import java.util.List;

public interface FragmentExchange {
    String getServices();
    void setServices(String services);
    void appendServices(String services);

    List<Material> getMaterials();

    void setMaterials(List<Material> materials);
}
