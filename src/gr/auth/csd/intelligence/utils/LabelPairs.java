package gr.auth.csd.intelligence.utils;

import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Document;
import gr.auth.csd.intelligence.preprocessing.Labels;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Yannis Papanikolaou
 */
public class LabelPairs {
    static Labels labels;
    
    public static void main(String[] args) {
        CorpusJSON c= new CorpusJSON(args[0]);
        labels  = new Labels(c);
        int[][] labelpairs = new int[labels.getSize()][labels.getSize()];
        Document doc;
        c.reset();
        while((doc=c.nextDocument())!=null) {
            Set<String> docLabels = doc.getLabels();
            String[] dl= new String[docLabels.size()];
            int i=0;
            for(String l:docLabels) dl[i++]= l;
            
            for(i=0;i<dl.length;i++) {
                int index1 = labels.getIndex(dl[i]);
                for(int j=i+1;j<dl.length;j++) {
                    int index2 = labels.getIndex(dl[j]);
                    labelpairs[index1][index2]++;
                    labelpairs[index2][index1]++;
                }
            }
        }
        
        for(int i=0;i<labelpairs.length;i++) {
            for(int j=0;j<labelpairs[j].length;j++) {
                
            }
        }
    }
}
