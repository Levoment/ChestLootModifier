package com.github.levoment.chestlootmodifier;

public class RarityObject {
    private int MinRolls;
    private int MaxRolls;

    public RarityObject(int minRolls, int maxRolls) {
        MinRolls = minRolls;
        MaxRolls = maxRolls;
    }

    public int getMinRolls() {
        return MinRolls;
    }

    public void setMinRolls(int minRolls) {
        MinRolls = minRolls;
    }

    public int getMaxRolls() {
        return MaxRolls;
    }

    public void setMaxRolls(int maxRolls) {
        MaxRolls = maxRolls;
    }
}
