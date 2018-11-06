package com.happyhappyyay.landscaperecord;

public enum MaterialType {
    SALT,
    TREE,
    BUSH,
    FLOWER,
    DIRT,
    MULCH,
    STONE,
    GAS,
    CHEMICAL,
    SEED,
    SOD,
    EDGING,
    DRAIN_TILE {
        @Override
        public String toString() {
            return "Drain Tile";
        }
    },
    OTHER,
}
