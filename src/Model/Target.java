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
 * @author Linas Martusevicius
 *
 * The Target object model.
 */
public class Target {

    SimpleStringProperty status;
    SimpleStringProperty name;
    SimpleStringProperty domain;
    SimpleStringProperty address;
    SimpleStringProperty lastrtt;
    SimpleIntegerProperty timeouts;
    SimpleStringProperty flags;
    boolean isBeingPinged;

    public Target(String status, String name, String domain, String address, String lastrtt, int timeouts, String flags, boolean isBeingPinged) {
        this.status = new SimpleStringProperty(status);
        this.name = new SimpleStringProperty(name);
        this.domain = new SimpleStringProperty(domain);
        this.address = new SimpleStringProperty(address);
        this.lastrtt = new SimpleStringProperty(lastrtt);
        this.timeouts = new SimpleIntegerProperty(timeouts);
        this.flags = new SimpleStringProperty(flags);
        this.isBeingPinged = isBeingPinged;
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
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

    public SimpleStringProperty flagsProperty() {
        if (flags != null) {
            return flags;
        } else {
            flags.set("");
            return flags;
        }
    }

    /**
     * Overwrites the Target's flags property value.
     *
     * @param flags
     */
    public void setFlags(String flags) {
        this.flags.set(flags);
    }

    /**
     * Adds a flag to the Target's flags property value.
     *
     * @param flag
     */
    public void addFlag(String flag) {
        String current = this.flags.get();

        if (!current.contains(flag)) {
            this.flags.set(flag + current);
        }
    }

    /**
     * Removes a specific flag from the Target's flags property.
     *
     * @param flag the flag to be removed.
     */
    public void removeFlag(String flag) {
        String current = this.flags.get();

        if (current.contains(flag)) {
            String removed = current.replace(flag, "");
            this.flags.set(removed);
        }
    }

    /**
     * Indicates whether the Target should be pinged.
     *
     * @return true if the Target contains the "A" (Active) flag. False if not.
     */
    public boolean isActive() {
        if (this.flags.get().contains("A")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Indicates whether the Target is currently being pinged. Used as a lock
     * mechanism for for the pingers for bypassing Targets with a large round
     * trip time. The pinger should check if the Target is currently being
     * pinged and move on to the next one in the list if this method returns
     * true.
     *
     * @return true if one of the threads is currently pinging this target.
     * False otherwise.
     */
    public boolean isIsBeingPinged() {
        return isBeingPinged;
    }

    /**
     * Part of the locking mechanism for bypassing Targets that are currently
     * being pinged. Should be set to true while the Target is being pinged and
     * false at the end of the ping cycle.
     *
     * @param isBeingPinged indicates whether the Target is currently being
     * pinged.
     */
    public void setIsBeingPinged(boolean isBeingPinged) {
        this.isBeingPinged = isBeingPinged;
    }

    @Override
    public String toString() {
        return ("T=[" + nameProperty().get() + ", " + domainProperty().get() + ", " + addressProperty().get() + "]" + '\n' + getInfo(this));
    }

    /**
     * A less verbose toString.
     *
     * @param t the Target.
     * @return a String representation of the Target's info.
     */
    public String getInfo(Target t) {
        return ("Status= " + statusProperty().get() + ", LastRTT= " + lastrttProperty().get() + ", Timeouts= " + timeoutsProperty().get() + ", Flags= " + flagsProperty().get() + ", Pinging= " + isIsBeingPinged());
    }

}
