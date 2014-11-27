/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Model.Target;
import Model.TargetList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Callable;
import javafx.collections.ObservableList;

/**
 *
 * @author Linas Martusevicius
 *
 * Continuously loops through an ObservableList of Target objects. Fail fast!
 */
public class Looper implements Runnable {

    int id;
    ObservableList<Target> targets;
    TargetList tl;
    LogUtil logUtil;
    boolean suspended;

    public Looper(int id, ObservableList<Target> targets, TargetList tl, LogUtil logUtil) {
        this.id = id;
        this.targets = targets;
        this.tl = tl;
        this.logUtil = logUtil;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Target t : targets) {
                    String ping = null;
                    if (t.isActive() && !t.isIsBeingPinged()) {
                        t.setIsBeingPinged(true);
                        t.setStatus("PINGING");
                        try {
                            Callable c = new Pinger(t);
                            ping = c.call().toString();
                            switch (ping) {
                                case "TIME_OUT":
                                    t.setStatus("TIME OUT");
                                    t.setLastrtt("TIME_OUT");
                                    t.setTimeouts(t.timeoutsProperty().get() + 1);
                                    logUtil.log(LogUtil.INFO, t.nameProperty().get() + " - timed out!");
                                    t.setIsBeingPinged(false);
                                    break;
                                case "UNKNOWN_HOST":
                                    t.setStatus("ERROR");
                                    t.setLastrtt("UNKNOWN HOST");
                                    logUtil.log(LogUtil.WARNING, t.nameProperty().get() + " - unknown host!");
                                    t.setIsBeingPinged(false);
                                    break;
                                case "UNREACHABLE":
                                    t.setStatus("ERROR");
                                    t.setLastrtt("UNREACHABLE HOST");
                                    logUtil.log(LogUtil.WARNING, t.nameProperty().get() + " - is unreachable!");
                                    t.setIsBeingPinged(false);
                                    break;
                                default:
                                    t.setLastrtt(ping);
                                    t.setStatus("ACTIVE");
                                    t.setIsBeingPinged(false);
                                    break;
                            }
                        } catch (Exception e) {
                            logUtil.log(LogUtil.CRITICAL, e.getMessage() + ", " + e.getCause());
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    synchronized (this) {
                        while (suspended) {
                            wait();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (ConcurrentModificationException ccm) {
                ccm.printStackTrace();
                System.out.println("ConcurrentModificationException in looper " + this.id);
            }
        }
    }

    /**
     * Sets the looper's suspended boolean value to ture.
     */
    public void suspend() {
        suspended = true;
    }

    /**
     * Sets the looper's suspended boolean value to false and notifies the
     * thread.
     */
    public synchronized void resume() {
        suspended = false;
        notify();
    }

    /**
     *
     * @return the looper's suspended status.
     */
    public boolean isSuspended() {
        return suspended;
    }

    @Override
    public String toString() {
        return "Looper{" + "id=" + id + ", suspended=" + suspended + '}';
    }

}
