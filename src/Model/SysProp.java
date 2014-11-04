/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author root
 */
public class SysProp {
    
    Double cpuusage;
    String type;
    
    public SysProp(String type, Double cpuusage){
        this.type = type;
        this.cpuusage = cpuusage;
    }

    public Double getCpuusage() {
        return cpuusage;
    }

    public void setCpuusage(Double cpuusage) {
        this.cpuusage = cpuusage;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
