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
package gr.auth.csd.intelligence.cgsp.lda;

import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.cgsp.utils.LDACmdOption;
import gr.auth.csd.intelligence.cgsp.lda.models.Model;
import gr.auth.csd.intelligence.cgsp.preprocessing.Corpus;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import gr.auth.csd.intelligence.cgsp.preprocessing.Labels;
import java.io.Serializable;
import java.util.ArrayList;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class LabelsDataset extends LDADataset implements Serializable {

    private final Labels labels;

    /**
     *
     * @param dic
     * @param inf
     * @param K
     * @param trainedPhi
     * @param labs
     */
    public LabelsDataset(Dictionary dic, boolean inf, int K, String trainedPhi, String labs) {
        super(dic, inf, K, trainedPhi);
        labels = Labels.readLabels(labs);
        V = labels.getSize();
        if (!inf) {
            this.K = K;
        } else {
            this.K = Model.readPhi(trainedPhi).length;
        }
    }

    //Creates the dataset by reading data, dictionary, labels, etc   

    /**
     *
     * @param corpus
     */

    @Override
    public void create(Corpus corpus) {
        gr.auth.csd.intelligence.cgsp.preprocessing.Document doc;
        docs = new ArrayList<>();
        corpus.reset();
        while ((doc = corpus.nextDocument()) != null) {
            TIntArrayList wordIds = new TIntArrayList();
            for (String l : doc.getLabels()) {
                wordIds.add(labels.getIndex(l) - 1);
            }
            setDoc(new Document(wordIds, doc.getId(), null, doc.getJournal()), docs.size());
        }

    }

    /**
     *
     * @param index
     * @return
     */
    @Override
    public String getWord(Integer index) {
        return labels.getLabel(index + 1);
    }
}
