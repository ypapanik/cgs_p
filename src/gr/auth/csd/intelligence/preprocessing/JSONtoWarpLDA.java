/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author User
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
