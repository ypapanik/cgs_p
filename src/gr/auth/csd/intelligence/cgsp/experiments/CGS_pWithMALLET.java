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

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gr.auth.csd.intelligence.cgsp.preprocessing.JSONtoMALLET;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Example to show how a trained MALLET LDA model can be used to compute
 * \theta_p and \phi_p.
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class CGS_pWithMALLET extends CGS_pWithWarpLDA {

    private final TIntObjectHashMap<ArrayList<Integer>> documents2;
    TIntObjectHashMap<ArrayList<Integer>> zeta;

    /**
     *
     * @param malletFile
     */
    public CGS_pWithMALLET(String malletFile) {
        Utils.gunzipIt(malletFile, "output.txt");
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get("output.txt"))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException ex) {
            Logger.getLogger(CGS_pWithMALLET.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] alphas = lines.get(1).split(":");
        String[] a = alphas[1].split(" ");
        this.alpha = Double.parseDouble(a[1]);
        String[] betas = lines.get(2).split(":");
        this.beta = Double.parseDouble(betas[1]);

        K = 0;
        V = 0;
        documents2 = new TIntObjectHashMap<>();
        zeta = new TIntObjectHashMap<>();
        for (int i = 3; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] split = line.split(" ");
            int d = Integer.parseInt(split[0]);
            if (!documents2.containsKey(d)) {
                documents2.put(d, new ArrayList<>());
            }
            int pos = Integer.parseInt(split[2]);
            int wordType = Integer.parseInt(split[3]);
            if (V < wordType) {
                V = wordType;
            }
            documents2.get(d).add(pos, wordType);
            int topic = Integer.parseInt(split[5]);
            if (K < topic) {
                K = topic;
            }
            if (!zeta.containsKey(d)) {
                zeta.put(d, new ArrayList<>());
            }
            zeta.get(d).add(pos, topic);
        }
        D = documents2.size();
        V++;
        K++;
        nw = new double[K][V];
        nd = new double[D][K];
        nwsum = new double[K];
        TIntObjectIterator<ArrayList<Integer>> it = documents2.iterator();
        while (it.hasNext()) {
            it.advance();
            int doc = it.key();
            for (int w=0;w<it.value().size();w++) {
                int wordType = it.value().get(w);
                int topic = zeta.get(doc).get(w);
                nw[topic][wordType]++;
                nd[doc][topic]++;
                nwsum[topic]++;
            }
        }
    }

    /**
     *
     * @param inFile
     * @param outputState
     * @throws IOException
     */
    public static void runMallet(String inFile, String outputState) throws IOException {
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<>();
        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false));
        pipeList.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));
        Reader fileReader = new InputStreamReader(new FileInputStream(new File(inFile)), "UTF-8");
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 2, 1)); // data, label, name fields
        int numTopics = 100;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 10.0, 0.01);

        model.addInstances(instances);
        model.setNumThreads(4);
        model.setNumIterations(100);
        model.estimate();
        model.printState(new File(outputState));
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        JSONtoMALLET json2mallet = new JSONtoMALLET(args[0], "train.txt");
        try {
            runMallet("train.txt", "output.txt.gz");
        } catch (IOException ex) {
            Logger.getLogger(CGS_pWithMALLET.class.getName()).log(Level.SEVERE, null, ex);
        }
        //cgs_p
        CGS_pWithMALLET mallet = new CGS_pWithMALLET("output.txt.gz");
        double[][] theta = mallet.computeTheta();
        double[][] theta_p = mallet.computeTheta_p();
        double[][] phi = mallet.computePhi();
        double[][] phi_p = mallet.computePhi_p();

        System.out.println("phi + theta (Griffiths Steyvers estimator): " + mallet.logLikelihood(phi, theta));
        System.out.println("phi_p + theta: " + mallet.logLikelihood(phi_p, theta));
        System.out.println("phi + theta_p: " + mallet.logLikelihood(phi, theta_p));
        System.out.println("phi_p + theta_p: " + mallet.logLikelihood(phi_p, theta_p));
    }

    /**
     *
     * @return
     */
    @Override
    protected double[][] computeTheta_p() {
        double[][] theta_p = new double[D][K];
        for (int d = 0; d < D; d++) {
            double[] p = new double[K];
            for (int word : documents2.get(d)) {
                //System.out.println(word);
                for (int k = 0; k < K; k++) {
                    p[k] = (alpha + nd[d][k]) * (nw[k][word] + beta) / (nwsum[k] + V * beta);
                }
                //average sampling probabilities
                p = Utils.normalize(p, 1);

                for (int k = 0; k < K; k++) {
                    theta_p[d][k] += p[k];
                }
            }

            for (int k = 0; k < K; k++) {
                theta_p[d][k] += alpha;
            }
            // normalize
            theta_p[d] = Utils.normalize(theta_p[d], 1);

        }
        return theta_p;
    }

    /**
     *
     * @return
     */
    @Override
    protected double[][] computePhi_p() {
        double[][] phi_p = new double[K][V];
        for (int d = 0; d < D; d++) {
            double[] p = new double[K];
            for (int word : documents2.get(d)) {
                for (int k = 0; k < K; k++) {
                    p[k] = (nw[k][word] + beta) * (nd[d][k] + alpha) / (nwsum[k] + V * beta);
                }
                //average sampling probabilities
                p = Utils.normalize(p, 1);
                for (int k = 0; k < K; k++) {
                    phi_p[k][word] += p[k];
                }
            }
        }
        for (int k = 0; k < K; k++) {
            //add beta hyperparameter
            for (int w = 0; w < V; w++) {
                phi_p[k][w] = phi_p[k][w] + beta;
            }
            //normalize
            phi_p[k] = Utils.normalize(phi_p[k], 1.0);
        }
        return phi_p;
    }

        /**
     *
     * @param phi
     * @param theta
     * @return
     */
    @Override
    public double logLikelihood(double phi[][], double[][] theta) {
        double ll = 0;
        TIntObjectIterator<ArrayList<Integer>> it = documents2.iterator();
        int d = 0;
        while(it.hasNext()) {
            it.advance();
            for(int word:it.value())
            {
                double l = 0;
                for (int k = 0; k < K; k++) {
                    if (phi[k][word] != 0 && theta[d][k] != 0) {
                        l += phi[k][word] * theta[d][k];
                    }

                }
                ll += Math.log(l);
                //if(Double.isNaN(ll)) System.out.println(d+" "+word+" "+l+" "+ Math.log(l));
            }
            d++;
        }
        return ll;
    }

    
}
