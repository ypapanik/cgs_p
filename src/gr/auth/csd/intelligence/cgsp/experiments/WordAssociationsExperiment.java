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
package gr.auth.csd.intelligence.cgsp.experiments;

import gr.auth.csd.intelligence.cgsp.lda.LDA;
import gr.auth.csd.intelligence.cgsp.lda.models.Model;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import gr.auth.csd.intelligence.cgsp.preprocessing.NGram;
import gr.auth.csd.intelligence.cgsp.utils.LDACmdOption;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class WordAssociationsExperiment {

    public static void main(String args[]) throws IOException {

        //train an LDA model, calculate phi and phi_p estimators
        LDACmdOption option = new LDACmdOption(args);
        option.trainingFile = "tasa";
        option.K = 500;
        option.niters = 250;
        option.alpha = 0.1;
        option.modelName = option.K + "";
        option.method = "cgsp";
        //LDA lda = new LDA(option);
        //lda.estimation();

        double[][] phip = Model.readPhi(option.modelName + ".phi_p");
        double[][] phi = Model.readPhi(option.modelName + ".phi");
        Dictionary dictionary = Dictionary.readDictionary(option.dictionary);

        //read the Nelson norms file
        List<String> lines = Files.readAllLines(new File("data/NelsonNorms").toPath(),
                Charset.defaultCharset());
        HashMap<NGram, HashSet<String>> norms = new HashMap<>();
        for (String line : lines) {
            String[] words = line.split(", ");
            String cue = words[0];
            String associatedWord = words[1];
            List<String> ngramList = new ArrayList<>();
            ngramList.add(cue);
            NGram cueNGram = new NGram(ngramList);
            if (dictionary.getId().containsKey(cueNGram)) {
                if (!norms.containsKey(cueNGram)) {
                    norms.put(cueNGram, new HashSet<>());
                }
                if (norms.get(cueNGram).size() < 5) {
                    norms.get(cueNGram).add(associatedWord);
                }
            }
        }
        System.out.println(norms.keySet().size());

        Iterator<Map.Entry<NGram, HashSet<String>>> it = norms.entrySet().iterator();
        int[] totalDiff = new int[5];
        while (it.hasNext()) {
            Map.Entry<NGram, HashSet<String>> norm = it.next();
            NGram cueNGram = norm.getKey();
            int[] rankPhi = getRank(phi, dictionary, cueNGram, norm);
            int[] rankPhip = getRank(phip, dictionary, cueNGram, norm);
            int[] diff = new int[5];
            for(int i=0;i<5;i++) {
                diff[i] = rankPhi[i] -rankPhip[i];
                totalDiff[i]+=diff[i];
            }
            System.out.println(cueNGram+" "+dictionary.getDocumentFrequency().get(cueNGram)
                    +" "+Arrays.toString(diff));
        }
        System.out.println(Arrays.toString(totalDiff));
        
        
        
        
        
    }

    private static int[] getRank(double[][] phi, Dictionary dictionary, NGram cueNGram, 
            Map.Entry<NGram, HashSet<String>> norm) {
        double[] cueVectorPhi = Utils.getColumn(phi, dictionary.getId().get(cueNGram));
        cueVectorPhi = Utils.normalizeVector(cueVectorPhi);
        double[] phiAssociatedWords = Utils.matrixMultiplication(cueVectorPhi, phi);
        HashMap<Integer, Integer> sortedIndices = Utils.getSortedIndices(phiAssociatedWords);
        int[] rank = new int[5];
        int i = 0;
        //for(int ind=0;ind<5;ind++) System.out.println(cueNGram+" "+dictionary.getNgram(sortedIndices.get(ind)));
        for (String associatedWord : norm.getValue()) {
            
            List<String> ngramList = new ArrayList<>();
            ngramList.add(associatedWord);
            NGram wordNGram = new NGram(ngramList);
            System.out.println(wordNGram+" ");//+dictionary.getId().get(wordNGram));
            if (dictionary.getId().containsKey(wordNGram)) {
                rank[i] = sortedIndices.get(dictionary.getId().get(wordNGram));
                i++;
                //System.out.println(wordNGram+" "+rank[i-1]);
            }
        }
        return rank;
    }
}
