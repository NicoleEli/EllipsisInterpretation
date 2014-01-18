package dataExtraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         18/01/14
 *
 * Class to extract NSUs and their antecedents from the BNC, using IDs and labelling provided by M. Purver (KCL).
 */
public class NSUExtractor {

    Charset charset = Charset.forName("US-ASCII");

    Path nsurf = Paths.get("C:\\DataFiles\\Programming\\4th Year Project - Ellipsis Interpretation\\NSU-RF");

    List<SentenceReference> sentenceRefs = new ArrayList<SentenceReference>();

    public void extractNSUs(){

        try {
            BufferedReader reader = Files.newBufferedReader(nsurf, charset);
            String line;

            //create a list of sentence references from the NSU-RF file
            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split(" ");

                String text = lineSplit[0];
                int antecedent = Integer.parseInt(lineSplit[1]);
                int nsu = Integer.parseInt(lineSplit[2]);
                String category = lineSplit[3];

                SentenceReference sref = new SentenceReference(text, antecedent, nsu, category);
                sentenceRefs.add(sref);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }
}
