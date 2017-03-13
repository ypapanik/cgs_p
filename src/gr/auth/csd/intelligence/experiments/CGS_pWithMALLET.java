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
package gr.auth.csd.intelligence.experiments;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import gr.auth.csd.intelligence.preprocessing.JSONtoMALLET;
import gr.auth.csd.intelligence.utils.Utils;
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
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class CGS_pWithMALLET extends CGS_pWithWarpLDA {

    public CGS_pWithMALLET(String malletFile) {
        Utils.gunzipIt(malletFile, "data/mallet/output.txt");
        List<String> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get("data/mallet/output.txt"))) {
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
        ArrayList<ArrayList<Integer>> documents = new ArrayList<>();
        ArrayList<ArrayList<Integer>> zeta = new ArrayList<>();
        for (int i = 3; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] split = line.split(" ");
            int d = Integer.parseInt(split[0]);
            if (d > documents.size()-1 ) {
                documents.add(d, new ArrayList<>());
            }
            int pos = Integer.parseInt(split[2]);
            int wordType = Integer.parseInt(split[3]);
            if (V < wordType) {
                V = wordType;
            }
            documents.get(d).add(pos, wordType);
            int topic = Integer.parseInt(split[5]);
            if (K < topic) {
                K = topic;
            }
            if (zeta.size() -1 < d) {
                zeta.add(d, new ArrayList<>());
            }
            zeta.get(d).add(pos, topic);
        }
        D = documents.size();
        V++;
        K++;
        nw = new double[K][V];
        nd = new double[D][K];
        nwsum = new double[K];
        z = new int[D][];
        docs = new int[D][];
        for (int d = 0; d < D; d++) {
            z[d] = new int[zeta.get(d).size()];
            docs[d] = new int[documents.get(d).size()];
        }

        for (int i = 3; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] split = line.split(" ");
            int doc = Integer.parseInt(split[0]);
            int pos = Integer.parseInt(split[2]);
            int wordType = Integer.parseInt(split[3]);
            int topic = Integer.parseInt(split[5]);
            nw[topic][wordType]++;
            nd[doc][topic]++;
            nwsum[topic]++;
            z[doc][pos] = topic;
            docs[doc][pos] = wordType;
        }
    }

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

}
