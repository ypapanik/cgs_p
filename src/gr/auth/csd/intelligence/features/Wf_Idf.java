package gr.auth.csd.intelligence.features;

import gr.auth.csd.intelligence.preprocessing.Dictionary;
import gr.auth.csd.intelligence.preprocessing.Document;
import gr.auth.csd.intelligence.preprocessing.Labels;
import gr.auth.csd.intelligence.preprocessing.NGram;

/**
 * @author Ioannis Papanikolaou
 */
public class Wf_Idf extends FeatureScoring {

    public Wf_Idf(Dictionary dict, boolean zoning) {
        super(dict, zoning);
        
    }
    
    public double fsMethod(NGram feature, Document doc, int wf, Labels labels) {
        return wf_Idf(doc, feature, wf, labels);
    }
    
    private double wf_Idf(Document doc, NGram feature, int wf, Labels labels) {
        double wf_temp = (zoning)?(zoning(feature, doc, wf,labels)):wf;
        double idf = Math.log((double) corpusSize / (double) dictionary.getDocumentFrequency().get(feature));
        //System.out.println(feature+" "+wf+" "+wf_temp+" "+idf+" "+corpusSize+" "+ dictionary.getDocumentFrequency().get(feature));
        return (1 + Math.log(wf_temp))*idf;
    }

    @Override
    public void read(int label, int pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}