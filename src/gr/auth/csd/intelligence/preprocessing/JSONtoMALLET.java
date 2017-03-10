/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.auth.csd.intelligence.preprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class JSONtoMALLET {

    public static void main(String[] args) {
        String json = args[0];
        String mallet = args[1];
        CorpusJSON c = new CorpusJSON(json);
        c.reset();
        Document doc;
        StringBuilder sb = new StringBuilder();
        while ((doc = c.nextDocument()) != null) {
            sb.append(doc.getId()).append("\t X \t").append(doc.getTitle())
                    .append(" ").append(doc.getAbs()).append("\n");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mallet))) {
            bw.append(sb);
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(JSONtoMALLET.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
