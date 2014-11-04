/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author root
 */
public class Target{
    
    SimpleStringProperty status;
    SimpleStringProperty name;
    SimpleStringProperty domain;
    SimpleStringProperty address;
    SimpleStringProperty lastrtt;
    SimpleIntegerProperty timeouts;
    
    public Target(String status, String name, String domain, String address, String lastrtt, int timeouts){
        this.status = new SimpleStringProperty(status);
        this.name = new SimpleStringProperty(name);
        this.domain = new SimpleStringProperty(domain);
        this.address = new SimpleStringProperty(address);
        this.lastrtt = new SimpleStringProperty(lastrtt);
        this.timeouts = new SimpleIntegerProperty(timeouts);
    }
    
    public SimpleStringProperty statusProperty(){
        return status;
    }
    
    public void setStatus(String status){
        this.status.set(status);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty domainProperty() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain.set(domain);
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public SimpleStringProperty lastrttProperty() {
        return lastrtt;
    }

    public void setLastrtt(String lastrtt) {
        this.lastrtt.set(lastrtt);
    }

    public SimpleIntegerProperty timeoutsProperty() {
        return timeouts;
    }

    public void setTimeouts(int timeouts) {
        this.timeouts.set(timeouts);
    }
    
}
