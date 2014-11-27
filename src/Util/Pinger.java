/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Model.Target;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 *
 * @author Linas Martusevicius
 * 
 * The heart of this application, pings a target address using
 * the local operating system's tools. 
 * "ping -c 1 + host" for Linux and Mac. 
 * "ping -n 1 + host" for Windows.
 * 
 */
public class Pinger implements Callable {

    Target target;

    public Pinger(Target t) {
        this.target = t;
    }

    @Override
    public String call() throws Exception {
        if (target.domainProperty().get() != null) {
            if (getOS().contains("WIN")) {
                return pingWindows(target.domainProperty().get());
            } else {
                return pingLinux(target.domainProperty().get());
            }
        } else {
            if (getOS().contains("WIN")) {
                return pingWindows(target.addressProperty().get());
            } else {
                return pingLinux(target.addressProperty().get());
            }
        }
    }

    /**
     * Pings a target host using a Linux/Mac "/bin/sh -c ping -c 1 [host]" command,
     * reads the result from input and error streams.
     * @param host the host to be pinged
     * @return the ping in ms, unless an error occurs. Otherwise - the error.
     */
    public String pingLinux(String host) {
        try {
            String strCommand = "ping -c 1 " + host;

            ProcessBuilder b = new ProcessBuilder("/bin/sh", "-c", strCommand);
            Process p = b.start();
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errbr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = "";
            String output = "";

            String errLine = "";
            String error = "";

            while ((line = br.readLine()) != null) {
                output += line;
                if (output.contains("avg/max")) {
                    output = output.split("/")[4];
                    //System.out.println("OUTSTRMLINUX = PING = "+output);
                }
                if (output.contains("100% packet loss")|output.contains("100.0% packet loss")) {
                    //System.out.println("OUTSTRMLINUX = TIME_OUT..."+'\n'+output);
                    output = "TIME_OUT";
                }
                if (output.toUpperCase().contains("UNKNOWN")) {
                    //System.out.println("OUTSTRMLINUX = UNKNOWN..."+'\n'+output);
                    output = "UNKNOWN_HOST";
                }
            }
            while ((errLine = errbr.readLine()) != null) {
                error += errLine;
                if (error.toUpperCase().contains("UNKNOWN")) {
                    //System.out.println("ERRSTRMLINUX = UNKNOWN..."+'\n'+error);
                    output = "UNKNOWN_HOST";
                }
                if (error.contains("unreachable")) {
                    //System.out.println("ERRSTRMLINUX = UNREACHABLE..."+'\n'+error);
                    output = "UNREACHABLE";
                }
            }
            return output;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pings a target host using a Windows "ping -n 1 [host]" command,
     * reads the result from input and error streams.
     * @param host the host to be pinged
     * @return the ping in ms, unless an error occurs. Otherwise - the error.
     */
    public String pingWindows(String host) {
        try {

            ProcessBuilder b = new ProcessBuilder("ping", "-n", "1", host);
            Process p = b.start();
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            String output = "";

            while ((line = br.readLine()) != null) {
                output = line + '\n';
                if (output.contains("Average")) {
                    output = output.split(",")[2].split(" = ")[1].split("ms")[0];
                }
                if (output.contains("100% loss")) {
                    output = "TIME_OUT";
                }
                if (output.contains("could not find host")) {
                    output = "UNKNOWN_HOST";
                }
                if (output.contains("unreachable")) {
                    output = "UNREACHABLE";
                }
            }
            return output;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * @return an uppercase String representation of the local Operating System
     */
    public String getOS() {
        return System.getProperty("os.name").toUpperCase();
    }

}
