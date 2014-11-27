/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Util.Looper;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Linas Martusevicius
 * 
 * Holds all of the Target objects. Singleton.
 */
public class TargetList {

    private static TargetList instance = null;

    ObservableList<Target> targetList = FXCollections.observableArrayList();
    ArrayList<Looper> looperList = new ArrayList<>();
    public static ExecutorService executor;
    public boolean isPaused;

    protected TargetList() {
        createTargets();
    }

    /**
     * @return an instance of the TargetList class.
     */
    public static TargetList getInstance() {
        if (instance == null) {
            instance = new TargetList();
        }
        return instance;
    }

    /**
     * @return an instance of the TargetList class with the attached ExecutorService.
     */
    public static TargetList getInstance(ExecutorService exec) {
        if (instance == null) {
            instance = new TargetList();
            setExecutor(exec);
        }
        return instance;
    }

    /**
     * 
     * @return an ObservableList of TargetObjects
     */
    public ObservableList<Target> getTargetList() {
        return targetList;
    }

    public void createTargets() {
        Target t1 = new Target("PENDING", "Serveriai.lt", null, "79.98.29.20", null, 0, "A", false);
        Target t2 = new Target("PENDING", "Delfi", "www.delfi.lt", null, null, 0, "A", false);
        Target t3 = new Target("PENDING", "Google.org", "www.google.org", null, null, 0, "A", false);
        Target t4 = new Target("PENDING", "Local peer", null, "192.168.1.140", null, 0, "A", false);
        Target t5 = new Target("PENDING", "Amazon.co.uk", "www.amazon.co.uk", null, null, 0, "A", false);
        Target t6 = new Target("PENDING", "NASA", "www.nasa.gov", null, null, 0, "A", false);
        Target t7 = new Target("PENDING", "BBC", "www.bbc.co.uk", null, null, 0, "A", false);
        Target t8 = new Target("PENDING", "FakeWebsite.com", "www.longfakewebsitename.com", "123.456.789.245", null, 0, "A", false);
        Target t9 = new Target("PENDING", "Linux Mint", "www.linuxmint.com", null, null, 0, "A", false);
        Target t0 = new Target("PENDING", "Facebook", "www.facebook.com", null, null, 0, "A", false);
        Target t10 = new Target("PENDING", "Youtube", "www.youtube.com", null, null, 0, "A", false);
        Target t11 = new Target("PENDING", "W3Schools", "www.w3schools.com", null, null, 0, "A", false);
        Target t12 = new Target("PENDING", "Docs.Oracle", "docs.oracle.com", null, null, 0, "A", false);
        Target t13 = new Target("PENDING", "StackOverflow", "stackoverflow.com", null, null, 0, "A", false);
        Target t14 = new Target("PENDING", "ClassicRock.fm", null, "5.20.233.18", null, 0, "A", false);
        Target t15 = new Target("PENDING", "GMail", "mail.google.com", null, null, 0, "A", false);
        Target t16 = new Target("PENDING", "Mail.com", "www.mail.com", null, null, 0, "A", false);
        Target t17 = new Target("PENDING", "Tutorialspoint", "www.tutorialspoint.com", null, null, 0, "A", false);
        Target t18 = new Target("PENDING", "Swedbank.lt", "www.swedbank.lt", null, null, 0, "A", false);
        Target t19 = new Target("PENDING", "localhost", null, "127.0.0.1", null, 0, "AM", false);
        Target t20 = new Target("PENDING", "Simulated UNREACHABLE", null, "192.168.2.123", null, 0, "A", false);
        Target t21 = new Target("PENDING", "Australia DNS", "ns1.telstra.net", "139.130.4.5", null, 0, "A", false);
        Target t22 = new Target("PENDING", "Google DNS 1", "google-public-dns-a.google.com.", "8.8.8.8", null, 0, "A", false);
        Target t23 = new Target("PENDING", "Google DNS 2", "google-public-dns-b.google.com.", "8.8.4.4", null, 0, "A", false);
        Target t24 = new Target("PENDING", "LjreMC", "www.lejremc.dk", null, null, 0, "A", false);

        targetList.add(t1);
        targetList.add(t2);
        targetList.add(t3);
        targetList.add(t4);
        targetList.add(t5);
        targetList.add(t6);
        targetList.add(t7);
        targetList.add(t8);
        targetList.add(t9);
        targetList.add(t0);
        targetList.add(t10);
        targetList.add(t11);
        targetList.add(t12);
        targetList.add(t13);
        targetList.add(t14);
        targetList.add(t15);
        targetList.add(t16);
        targetList.add(t17);
        targetList.add(t18);
        targetList.add(t19);
        targetList.add(t20);
        targetList.add(t21);
        targetList.add(t22);
        targetList.add(t23);
        targetList.add(t24);
    }

    public static void setExecutor(ExecutorService executor) {
        TargetList.executor = executor;
    }

    public void printTargetNames() {
        for (Target t : targetList) {
            System.out.println(t.nameProperty().get());
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public ArrayList<Looper> getLooperList() {
        return looperList;
    }

}
