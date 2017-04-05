/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.auth.csd.intelligence.cgsp.preprocessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class JsonToTxt {

    public static void readNwrite(String input, String output, boolean addSpace) {
        CorpusJSON in = new CorpusJSON(input);
        Document doc;
        in.reset();
        List<String> docs = new ArrayList<>();
        while ((doc = in.nextDocument()) != null) {
            List<String> phrases = doc.getContentAsSentencesOfTokens(true);
            for (String phrase : phrases) {
//                phrase += "\n";
                docs.add(phrase);
            }
        }

        try {
            Files.write(Paths.get(output), docs, Charset.defaultCharset());
        } catch (IOException ex) {
            Logger.getLogger(JsonToTxt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
