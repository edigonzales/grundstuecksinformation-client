package ch.so.agi.grundstuecksinformation.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Egrid implements IsSerializable {
    private String egrid;
    private String number;
    private String identDN;
    private String type;
    private String limit;
    private boolean planned;
    
    public String getEgrid() {
        return egrid;
    }
    public void setEgrid(String egrid) {
        this.egrid = egrid;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getIdentDN() {
        return identDN;
    }
    public void setIdentDN(String identDN) {
        this.identDN = identDN;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLimit() {
        return limit;
    }
    public void setLimit(String limit) {
        this.limit = limit;
    }
    public boolean isPlanned() {
        return planned;
    }
    public void setPlanned(boolean planned) {
        this.planned = planned;
    }
}
