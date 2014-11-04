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
 * @author root
 */
public class Pinger implements Callable {

    Target target;

    public Pinger(Target t) {
        this.target = t;
    }

    @Override
    public String call() throws Exception {
        if (target.domainProperty().get()!= null) {
            if (getOS().contains("LINUX")) {
                return pingLinux(target.domainProperty().get());
            } else {

            }
        }else{
            if (getOS().contains("LINUX")) {
                return pingLinux(target.addressProperty().get());
            } else {

            }
        }
        return null;
    }

    public String pingLinux(String host) {
        try {
            String strCommand = "ping -W 2 -c 1 " + host;
            
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
                if (output.contains("rtt")){
                    output = output.split("/")[5];
                }
                if (output.contains(" 0 received")){
                    output = "TIME_OUT";
                }
            }
            while((errLine = errbr.readLine())!=null){
                error += errLine;
                if(error.contains("unknown")){
                    output = "UNKNOWN_HOST";
                }
                if(error.contains("unreachable")){
                    output = "UNREACHABLE";
                }
            }
            return output;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getOS() {
        return System.getProperty("os.name").toUpperCase();
    }

}
