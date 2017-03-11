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
package gr.auth.csd.intelligence.preprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class JSONtoWarpLDA {

    public static void main(String[] args) {
        String json = args[0];
        String warpFile = args[1];
        CorpusJSON c = new CorpusJSON(json);
        c.reset();
        Document doc;
        StringBuilder sb = new StringBuilder();
        
        
        while ((doc = c.nextDocument()) != null) {
            List<String> tokens = doc.getContentAsSentencesOfTokens(true);
            sb.append(doc.getId()).append(" ").append(doc.getId()).append(" ");
            for(String token:tokens) sb.append(token);
            sb.append("\n");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(warpFile))) {
            bw.append(sb);
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(JSONtoWarpLDA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
