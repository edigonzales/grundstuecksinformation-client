package ch.so.agi.grundstuecksinformation.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BuildingEntry implements IsSerializable {
    private int edid;
    
    private PostalAddress postalAddress;
    
    public int getEdid() {
        return edid;
    }

    public void setEdid(int edid) {
        this.edid = edid;
    }

    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }
}
