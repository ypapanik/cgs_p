/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.auth.csd.intelligence.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 *
 * @author g
 */
public class Timer {

    private long user;
    private long system;
    private long cpu;
    private long wall;

    public Timer() {
        user = Utils.getUserTime();
        system = Utils.getSystemTime();
        cpu = Utils.getCpuTime();
        wall = System.currentTimeMillis();
    }

    public String duration() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Wall time (hours): %.2f\n", (System.currentTimeMillis()-wall) / (1000.0 * 60 * 60)));
        sb.append(String.format("User time (hours): %.2f\n", (getUserTime()-user) / (1000.0 * 1000 * 1000 * 60 * 60)));
        sb.append(String.format("System time (hours): %.2f\n", (getSystemTime()-system) / (1000.0 * 1000 * 1000 * 60 * 60)));
        sb.append(String.format("CPU time (hours): %.2f\n", (getCPUTime()-cpu) / (1000.0 * 1000 * 1000 * 60 * 60)));
        return sb.toString();
    }

        public String durationSeconds() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Wall time (seconds): %.2f\n", (System.currentTimeMillis()-wall) / (1000.0)));
        sb.append(String.format("User time (seconds): %.2f\n", (getUserTime()-user) / (1000.0 * 1000 * 1000)));
        sb.append(String.format("System time (seconds): %.2f\n", (getSystemTime()-system) / (1000.0 * 1000 * 1000)));
        sb.append(String.format("CPU time (seconds): %.2f\n", (getCPUTime()-cpu) / (1000.0 * 1000 * 1000)));
        return sb.toString();
    }
    
    
    /**
     * Get CPU time in nanoseconds.
     */
    public static long getCPUTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadCpuTime() : 0L;
    }

    /**
     * Get user time in nanoseconds.
     */
    public static long getUserTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadUserTime() : 0L;
    }

    /**
     * Get system time in nanoseconds.
     */
    public static long getSystemTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L;
    }
}
