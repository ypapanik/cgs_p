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
package gr.auth.csd.intelligence.cgsp.mlclassification.models;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.util.Random;
import gr.auth.csd.intelligence.cgsp.mlclassification.LDADataset;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class CVB0Model extends Model {

    double[][][] gamma;
    //constructor used for estimation

    /**
     *
     * @param data
     * @param thread
     * @param beta
     * @param inf
     * @param trainedModelName
     * @param threads
     * @param iters
     * @param burnin
     */
    public CVB0Model(LDADataset data, int thread, double beta, boolean inf, 
            String trainedModelName, int threads, int iters, int burnin) {
        super(data, thread, beta, inf, trainedModelName, threads, iters, burnin);
        //initialize gamma
        gamma = new double[M][][];
        System.out.println("Collapsed Variational Bayes 0...");
    }
    
    /**
     *
     */
    @Override
    public void initialize() {
        nd = new TIntDoubleHashMap[M];
        for(int d=0;d<M;d++) nd[d] = new TIntDoubleHashMap();
        Random r=new Random();
        for(int d=0;d<M;d++) {
            int[] labels = data.getDocs().get(d).getLabels();
            int K_m = labels.length;
            int documentLength = data.getDocs().get(d).getWords().size();
            gamma[d] = new double[documentLength][K_m];
            for(int w=0;w<documentLength;w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                double sum=0;
                for(int k=0;k<K_m;k++) {
                    gamma[d][w][k] = r.nextDouble();
                    sum+= gamma[d][w][k];
                }
                //normalize
                for(int k=0;k<K_m;k++) {
                    gamma[d][w][k] = gamma[d][w][k]/sum;
                    int label = labels[k]-1;
                    nw[label].adjustOrPutValue(word, gamma[d][w][k], gamma[d][w][k]);
                    nd[d].adjustOrPutValue(label, gamma[d][w][k], gamma[d][w][k]);
                    nwsum[label] += gamma[d][w][k];
                    if(!phi[label].contains(word)) phi[label].put(word, 0);
                }
            }
        }
        initAlpha();
    }
    
    /**
     *
     * @param d
     */
    @Override
    public void update(int d) {
        int documentLength = data.getDocs().get(d).getWords().size();
        int[] labels = data.getDocs().get(d).getLabels();
        int K_m = labels.length;
        for (int w = 0; w < documentLength; w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            double probs[] = new double[K_m];
            for (int k = 0; k < K_m; k++) {
                int label = labels[k]-1;
                double oldGamma = gamma[d][w][k];
                nw[label].adjustValue(word, -oldGamma);

                nd[d].adjustValue(label, -oldGamma);
                nwsum[label] -= oldGamma;
                probs[k] = (nw[label].get(word) + beta) * (nd[d].get(label) + alpha[label]) / (nwsum[label] + nw[label].size() * beta);
            }
            probs = Utils.normalize(probs, 1);

            //update
            for (int k = 0; k < K_m; k++) {
                int label = labels[k]-1;
                double newGamma = probs[k];
                gamma[d][w][k] = newGamma;

                nw[label].adjustOrPutValue(word, newGamma, newGamma);
                nd[d].adjustOrPutValue(label, newGamma, newGamma);
                
                nwsum[label] += newGamma;
            }
        }
    }    
}
