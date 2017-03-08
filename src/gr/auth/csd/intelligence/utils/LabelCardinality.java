package gr.auth.csd.intelligence.utils;

import gr.auth.csd.intelligence.preprocessing.Corpus;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Document;

/**
 *
 * @author Yannis Papanikolaou
 */
public class LabelCardinality {
    public static void main (String args[]) {
        String pathTrain = args[0];
        Corpus corpus = new CorpusJSON(pathTrain);        
        corpus.reset();
        Document document;
        double sum =0;
        double i=0;
        while ((document = corpus.nextDocument()) != null) {
            sum+=document.getLabels().size();
            i++;
        }
        System.out.println(sum/i);        
    }
}
