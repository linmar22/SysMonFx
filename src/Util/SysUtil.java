/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import org.hyperic.sigar.*;

/**
 *
 * @author Linas Martusevicius
 * 
 * A utility class for getting system information, mostly using the SIGAR library.
 */
public class SysUtil {

    Integer maxTraffic = 100;
    Sigar sigar = new Sigar();

    /**
     * @return a Double representation of the load percentage of all CPU cores combined.
     */
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

    /**
     * @return a Double representation of the RAM utilization percentage. 
     */
    public Double getMemoryUsage() {
        try {
            Double used = sigar.getMem().getUsedPercent();
            return used;
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return null;
        }
    }

    /**
     * @return a Double representation of a magical number that somehow shows
     * how much of the inbound and outbound traffic of this machine,
     * is this application.
     */
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

    /**
     * @return a Double representation of the root file system used in percent.
     */
    public Double getUsedSpacePercentLinux() {
        try {
            FileSystemUsage fsu = sigar.getFileSystemUsage("/");
            return fsu.getUsePercent();
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return null;
        }
    }

    /**
     * @return a long representation of the root file system's used space in kilobytes.
     */
    public long getUsedSpaceKbLinux() {
        try {
            FileSystemUsage fsu = sigar.getFileSystemUsage("/");
            return fsu.getUsed();
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return -1;
        }
    }

    /**
     * @return a long representation of the root file system's free space in kilobytes.
     */
    public long getFreeSpaceKbLinux() {
        try {
            FileSystemUsage fsu = sigar.getFileSystemUsage("/");
            return fsu.getTotal() - fsu.getUsed();
        } catch (SigarException sigex) {
            sigex.printStackTrace();
            return -1;
        }
    }

    /**
     * @return a long representation of the root file system's total space in kilobytes.
     */
    public long getTotalSpaceKbLinux() {
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

    public int getNumberOfCores() {
        CpuInfo cpui = new CpuInfo();
        return cpui.getTotalSockets();
    }

}
