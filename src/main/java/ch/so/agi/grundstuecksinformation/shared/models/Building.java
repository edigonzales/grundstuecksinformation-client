package ch.so.agi.grundstuecksinformation.shared.models;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Building implements IsSerializable {
    private int egid;
    
    private boolean planned;
    
    private boolean undergroundStructure;
    
    private double area;
    
    private ArrayList<BuildingEntry> buildingEntries;

    public int getEgid() {
        return egid;
    }

    public void setEgid(int egid) {
        this.egid = egid;
    }

    public boolean isPlanned() {
        return planned;
    }

    public void setPlanned(boolean planned) {
        this.planned = planned;
    }

    public boolean isUndergroundStructure() {
        return undergroundStructure;
    }

    public void setUndergroundStructure(boolean undergroundStructure) {
        this.undergroundStructure = undergroundStructure;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public ArrayList<BuildingEntry> getBuildingEntries() {
        return buildingEntries;
    }

    public void setBuildingEntries(ArrayList<BuildingEntry> buildingEntries) {
        this.buildingEntries = buildingEntries;
    }
}
