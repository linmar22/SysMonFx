/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.io.File;
import org.hyperic.sigar.*;

/**
 *
 * @author root
 */
public class SysUtil {

    Integer maxTraffic = 100;
    Sigar sigar = new Sigar();

    public Double getCPULoad() {
        try {
            Double load = sigar.getCpuPerc().getCombined();
            Double rounded = (double) Math.round(load * 10000) / 100;
            return rounded;
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return null;
        }
    }

    public Double getMemoryUsage() {
        try {
            Double used = sigar.getMem().getUsedPercent();
            return used;
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return null;
        }
    }

    public Double getNetworkUsage() {
        Double usage;
        try {
            Integer in = sigar.getNetStat().getAllInboundTotal();
            Integer out = sigar.getNetStat().getAllOutboundTotal();
            Integer traffic = in + out;
            usage = Double.valueOf(maxTraffic / 100 * traffic);
            Double rounded = (double) Math.round(usage);
            return rounded;
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return null;
        }
    }

    public Double getUsedSpacePercentLinux() {
        try {
            FileSystemUsage fsu = sigar.getFileSystemUsage("/");
            return fsu.getUsePercent();
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return null;
        }
    }
    
    public long getUsedSpaceKbLinux(){
        try {
            FileSystemUsage fsu = sigar.getFileSystemUsage("/");
            return fsu.getUsed();
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return -1;
        }
    }
    
    public long getFreeSpaceKbLinux(){
        try {
            FileSystemUsage fsu = sigar.getFileSystemUsage("/");
            return fsu.getTotal()-fsu.getUsed();
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return -1;
        }
    }
    
    public long getTotalSpaceKbLinux(){
        try {
            FileSystemUsage fsu = sigar.getFileSystemUsage("/");
            return fsu.getTotal();
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return -1;
        }
    }

    public void setMaxTraffic(Integer maxTraffic) {
        this.maxTraffic = maxTraffic;
    }

    public Integer getMaxTraffic() {
        return maxTraffic;
    }

}
