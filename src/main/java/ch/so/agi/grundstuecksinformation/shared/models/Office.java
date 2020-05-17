package ch.so.agi.grundstuecksinformation.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Office implements IsSerializable {
    String name;
    
    String officeAtWeb;
    
    String street;
    
    String number;
    
    String postalCode;
    
    String city;
    
    String email;
    
    String lastName;
    
    String firstName;
    
    String line1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficeAtWeb() {
        return officeAtWeb;
    }

    public void setOfficeAtWeb(String officeAtWeb) {
        this.officeAtWeb = officeAtWeb;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }
}
