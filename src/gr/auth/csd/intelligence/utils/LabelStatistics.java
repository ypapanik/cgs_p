package gr.auth.csd.intelligence.utils;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Labels;
import java.util.ArrayList;
//import nl.peterbloem.powerlaws.Continuous;

/**
 *
 * @author Yannis Papanikolaou
 */
public class LabelStatistics {
    public static void main(String args[]) {
        String dataset = args[0];
        CorpusJSON c = new CorpusJSON(dataset);
        Labels labels = new Labels(c);
        
        TIntIntHashMap stats = new TIntIntHashMap();
        TObjectIntHashMap<String> p = labels.getPositiveInstances();
        ArrayList<Double> values = new ArrayList<>(p.size());
        TObjectIntIterator<String> it = p.iterator();
        int thousand=0, hundred=0, tenthousand=0;
        double avg = 0;
        double count=0;
        while(it.hasNext()) {
            it.advance();
            values.add(((Double.valueOf(it.value() ) ) ) );
            stats.adjustOrPutValue(it.value(), 1, 1);
            avg+=it.value();
            count++;
            if (it.value()>500) thousand++;
            if (it.value()>50) hundred++;
            if(it.value()>5000) tenthousand++;
        }
        TIntIntIterator it2 = stats.iterator();
        while(it2.hasNext()) {
            it2.advance();
            System.out.println(it2.key()+" "+it2.value());
        }
        System.out.println("above 50:"+hundred);
        System.out.println("above 500:"+thousand);
        System.out.println("above 5000:"+tenthousand);
        System.out.println(avg/count);
        //Continuous model = Continuous.fit(values).fit();
        // * Calculate the significance based on 100 trials
        //double significance = model.significance(values, 1000);
        // * Calculate the significance to an accuracy of 0.01
        //double significance = model.significance(values, 0.01);
        //System.out.println("sig:"+significance);
    }
}
