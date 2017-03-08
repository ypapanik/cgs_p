package gr.auth.csd.intelligence.utils;

/**
 *
 * @author Yannis Papanikolaou
 * Performs 2-sample z-test to compare sample proportions
 */
public class Ztest {
    public static void main (String args[]) {
        double a = 0.17118;//Double.parseDouble(args[0]);
        double b = 0.18102;//Double.parseDouble(args[1]);
        double n = 17570;//Double.parseDouble(args[2]);
        double d = Math.abs(a-b);
        double p = (a+b)/2;
        //double p = (a*n1+b*n2)/(n1+n2);
        double se = Math.sqrt(p*(1-p)*2/n);
        //double se = Math.sqrt(p*(1-p)*(1/n1+1/n2));
        double z = d/se;
        org.apache.commons.math3.distribution.NormalDistribution nd = new org.apache.commons.math3.distribution.NormalDistribution();
        System.out.println(1 - nd.cumulativeProbability(z));
        if(1 - nd.cumulativeProbability(z)<0.05) System.out.println("Null hypothesis rejected.");
        else System.out.println("Null hypothesis not rejected.");
    }
}
