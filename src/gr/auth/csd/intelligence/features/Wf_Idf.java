/* 
 * Copyright (C) 2017
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