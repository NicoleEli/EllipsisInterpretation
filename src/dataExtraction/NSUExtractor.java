package dataExtraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

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
    String textsDir = "C:\\BNC-world\\Texts\\";
    Path writeFile = Paths.get("C:\\DataFiles\\Programming\\4th Year Project - Ellipsis Interpretation\\NSUs.txt");

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

            reader.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }


        try{
            //open NSUs.txt for writing
            BufferedWriter writer = Files.newBufferedWriter(writeFile, charset);

            for (SentenceReference sRef : sentenceRefs){
                //texts are stored in e.g. G\G0\G0A
                String textIndex1 = sRef.text.substring(0,1);
                String textIndex2 = sRef.text.substring(0,2);
                Path textPath = Paths.get(textsDir+textIndex1+"\\"+textIndex2+"\\"+sRef.text);

                //prefixes of lines of interest - a new s-unit starts a new line in the text
                String beginAnt = "<s n=\""+sRef.antecedent+"\">";
                String beginNSU = "<s n=\""+sRef.nsu+"\">";

                //open the text for reading
                BufferedReader reader = Files.newBufferedReader(textPath, charset);
                String line;

                //write NSUs and their antecedents to NSUs.txt
                while ((line = reader.readLine()) != null){
                    if (line.startsWith(beginAnt)){
                        writer.append("antecedent -- "+line+" -- "+sRef.nsuCategory);
                        writer.newLine();
                    }
                    if (line.startsWith(beginNSU)){
                        writer.append("NSU -- "+line+" -- "+sRef.nsuCategory);
                        writer.newLine();
                        writer.newLine();
                    }
                }

                reader.close();
            }

            writer.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }


    }
}
