/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bwaldvogel.liblinear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import static de.bwaldvogel.liblinear.Linear.atof;
import static de.bwaldvogel.liblinear.Linear.atoi;

/**
 *
 * @author anithagenilos
 */
public class TrainGr extends Train {
    private static ProblemGr constructProblem(List<Double> vy, List<Feature[]> vx, int max_index, double bias) {
        ProblemGr prob = new ProblemGr();
        prob.bias = bias;
        prob.l = vy.size();
        prob.n = max_index;
        if (bias >= 0) {
            prob.n++;
        }
        prob.x = new Feature[prob.l][];
        for (int i = 0; i < prob.l; i++) {
            prob.x[i] = vx.get(i);

            if (bias >= 0) {
                assert prob.x[i][prob.x[i].length - 1] == null;
                prob.x[i][prob.x[i].length - 1] = new FeatureNode(max_index + 1, bias);
            }
        }

        prob.y = new double[prob.l];
        for (int i = 0; i < prob.l; i++)
            prob.y[i] = vy.get(i).doubleValue();

        return prob;
    }
        /**
     * reads a problem from LibSVM format
     * @param file the SVM file
     * @throws IOException obviously in case of any I/O exception ;)
     * @throws InvalidInputDataException if the input file is not correctly formatted
     */
    public static ProblemGr readProblem(File file, double bias, int max_index) throws IOException, InvalidInputDataException {
        BufferedReader fp = new BufferedReader(new FileReader(file));
        List<Double> vy = new ArrayList<>();
        List<Feature[]> vx = new ArrayList<>();

        int lineNr = 0;

        try {
            while (true) {
                String line = fp.readLine();
                if (line == null) break;
                lineNr++;

                StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
                String token;
                try {
                    token = st.nextToken();
                } catch (NoSuchElementException e) {
                    throw new InvalidInputDataException("empty line", file, lineNr, e);
                }

                try {
                    vy.add(atof(token));
                } catch (NumberFormatException e) {
                    throw new InvalidInputDataException("invalid label: " + token, file, lineNr, e);
                }

                int m = st.countTokens() / 2;
                Feature[] x;
                if (bias >= 0) {
                    x = new Feature[m + 1];
                } else {
                    x = new Feature[m];
                }
                int indexBefore = 0;
                for (int j = 0; j < m; j++) {

                    token = st.nextToken();
                    int index;
                    try {
                        index = atoi(token);
                    } catch (NumberFormatException e) {
                        throw new InvalidInputDataException("invalid index: " + token, file, lineNr, e);
                    }

                    // assert that indices are valid and sorted
                    if (index < 0) throw new InvalidInputDataException("invalid index: " + index, file, lineNr);
                    if (index <= indexBefore) throw new InvalidInputDataException("indices must be sorted in ascending order", file, lineNr);
                    indexBefore = index;

                    token = st.nextToken();
                    try {
                        double value = atof(token);
                        x[j] = new FeatureNode(index, value);
                    } catch (NumberFormatException e) {
                        throw new InvalidInputDataException("invalid value: " + token, file, lineNr);
                    }
                }
                vx.add(x);
            }

            return constructProblem(vy, vx, max_index, bias);
        }
        finally {
            fp.close();
        }
    }
    
}
