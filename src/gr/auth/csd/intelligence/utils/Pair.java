package gr.auth.csd.intelligence.utils;

public class Pair implements Comparable<Pair> {

    public static int indexOf(Pair[] arr, int topic) {
        int i;
        for (i = 0; i < arr.length; i++) {
            if (topic == (Integer) arr[i].first) {
                break;
            }
        }
        return i;
    }
    public Object first;
    public Comparable second;
    private double value;
    public static boolean naturalOrder = false;

    public Pair(Object k, Comparable v){
        first = k;
        value = (double) v;
        second = v;		
    }

    public Pair(Object k, Comparable v, boolean naturalOrder){
        first = k;
        second = v;
        value = (double) v;
        Pair.naturalOrder = naturalOrder; 
    }

    @Override
    public int compareTo(Pair p){
        if (naturalOrder)
            return this.second.compareTo(p.second);
        else return -this.second.compareTo(p.second);
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void decrease() {
        value--;
        this.second = value;
    }
    public void increase() {
        value++;
        this.second = value;
    }
    
    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }
    
    
}
