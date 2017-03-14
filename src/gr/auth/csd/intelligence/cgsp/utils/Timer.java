/* 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.auth.csd.intelligence.cgsp.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class Timer {

    private long user;
    private long system;
    private long cpu;
    private long wall;

    /**
     *
     */
    public Timer() {
        user = Utils.getUserTime();
        system = Utils.getSystemTime();
        cpu = Utils.getCpuTime();
        wall = System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    public String duration() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Wall time (hours): %.2f\n", (System.currentTimeMillis()-wall) / (1000.0 * 60 * 60)));
        sb.append(String.format("User time (hours): %.2f\n", (getUserTime()-user) / (1000.0 * 1000 * 1000 * 60 * 60)));
        sb.append(String.format("System time (hours): %.2f\n", (getSystemTime()-system) / (1000.0 * 1000 * 1000 * 60 * 60)));
        sb.append(String.format("CPU time (hours): %.2f\n", (getCPUTime()-cpu) / (1000.0 * 1000 * 1000 * 60 * 60)));
        return sb.toString();
    }

    /**
     *
     * @return
     */
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
     * @return 
     */
    public static long getCPUTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadCpuTime() : 0L;
    }

    /**
     * Get user time in nanoseconds.
     * @return 
     */
    public static long getUserTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadUserTime() : 0L;
    }

    /**
     * Get system time in nanoseconds.
     * @return 
     */
    public static long getSystemTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L;
    }
}
