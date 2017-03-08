package gr.auth.csd.intelligence.preprocessing;

import gnu.trove.set.hash.THashSet;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;

/**
 *
 * @author Grigorios Tsoumakas
 * @version 2013.11.23
 *
 */
public class CorpusJSON extends Corpus {

    static final long serialVersionUID = 8913373272694212740L;

    private final String file;
    protected final JsonFactory jsonFactory;
    protected JsonParser jsonParser;

    public CorpusJSON(String aFile) {
        file = aFile;
        jsonFactory = new MappingJsonFactory();
    }

    @Override
    public Document nextDocument() {
        Document document = new Document();
        try {
            /*
             JsonToken token = jsonParser.nextToken();
             if (token == JsonToken.END_OBJECT) {
             return null;
             } */

            JsonToken current = jsonParser.nextToken();
            if (current == JsonToken.END_ARRAY) {
                return null;
            }
            JsonNode node = jsonParser.readValueAsTree();
            if (node.get("abstract") != null) {
                document.setAbstract(node.get("abstract").asText());
            } else if (node.get("abstractText") != null) {
                document.setAbstract(node.get("abstractText").asText());
            } else if(node.get("words") != null) {
                document.setAbstract(node.get("words").asText());
            }
            if (node.get("title") != null) document.setTitle(node.get("title").asText());
            if (node.get("journal") != null) document.setJournal(node.get("journal").asText());
            if (node.get("full_text") != null) document.setBody(node.get("full_text").asText());
            if (node.get("pmid") != null) document.setPmid(node.get("pmid").asText());
            if (node.get("id") != null) document.setPmid(node.get("id").asText());
            if (node.get("year") != null) document.setYear(node.get("year").asInt());
            if (node.get("meshMajor") != null) {
                JsonNode labelsNode = node.get("meshMajor");
                THashSet<String> meshTerms = new THashSet<>();
                Iterator<JsonNode> it = labelsNode.getElements();
                while (it.hasNext()) {
                    meshTerms.add(it.next().asText());
                }
                document.setLabels(meshTerms);
            }
            else if (node.get("labels") != null) {
                JsonNode labelsNode = node.get("labels");
                THashSet<String> meshTerms = new THashSet<>();
                Iterator<JsonNode> it = labelsNode.getElements();
                while (it.hasNext()) {
                    meshTerms.add(it.next().asText());
                }
                document.setLabels(meshTerms);
            }
            return document;

        } catch (IOException ex) {
            Logger.getLogger(CorpusJSON.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void reset() {
        File f;
        try {
            f = new File(file);
            jsonParser = jsonFactory.createJsonParser(f);
            jsonParser.nextToken(); // gets root object
            jsonParser.nextToken(); // get root array element, e.g. documents/articles
            jsonParser.nextToken(); // get start of array
        } catch (IOException ex) {
            Logger.getLogger(CorpusJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void close() {
        try {
            jsonParser.close();
        } catch (IOException ex) {
        }
    }
    
    public static int size(String c) {
        if (c == null) return 0;
        CorpusJSON corpus2 = new CorpusJSON(c);
        corpus2.reset();
        Document doc;
        int testSize = 0;
        while ((doc = corpus2.nextDocument()) != null) {
            testSize++;
        }
        return testSize;
    }
    
    public static int size(Corpus c) {
        if (c == null) return 0;
        c.reset();
        Document doc;
        int testSize = 0;
        while ((doc = c.nextDocument()) != null) {
            testSize++;
        }
        return testSize;
    }
}