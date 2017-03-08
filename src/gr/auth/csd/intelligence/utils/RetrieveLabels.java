package gr.auth.csd.intelligence.utils;

import gr.auth.csd.intelligence.preprocessing.Labels;

public class RetrieveLabels {
    private static Labels labels;
    static int[] a = {3922};

    public static void main(String args[]) {
        labels = Labels.readLabels(args[0]);
        for(int i = 0;i<a.length;i++) {
            System.out.println(labels.getLabel(a[i]));
        }
    }
}
