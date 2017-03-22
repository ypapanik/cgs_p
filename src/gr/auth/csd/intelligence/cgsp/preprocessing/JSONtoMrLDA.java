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
package gr.auth.csd.intelligence.cgsp.preprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class JSONtoMrLDA {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String json = args[0];
        String mallet = args[1];
        JSONtoMrLDA json2mallet = new JSONtoMrLDA(json, mallet);
    }

    /**
     *
     * @param json
     * @param mallet
     */
    public JSONtoMrLDA(String json, String mallet) {
        CorpusJSON c = new CorpusJSON(json);
        c.reset();
        Document doc;
        StringBuilder sb = new StringBuilder();
        while ((doc = c.nextDocument()) != null) {
            sb.append(doc.getId()).append("\t").append(doc.getTitle())
                    .append(" ").append(doc.getAbs()).append("\n");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mallet))) {
            bw.append(sb);
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(JSONtoMrLDA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
