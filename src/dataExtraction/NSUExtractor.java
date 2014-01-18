package dataExtraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    Path extractedNSUFile = Paths.get("C:\\DataFiles\\Programming\\4th Year Project - Ellipsis Interpretation\\NSUs.txt");
    Path noTagNSUFile = Paths.get("C:\\DataFiles\\Programming\\4th Year Project - Ellipsis Interpretation\\NSUs (no POS).txt");

    List<SentenceReference> sentenceRefs = new ArrayList<SentenceReference>();

    /**
     * Extract sentences containing NSUs (as listed in M. Purver's corpus) and their antecedents to a separate file
     */
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
            BufferedWriter writer = Files.newBufferedWriter(extractedNSUFile, charset);

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

    /**
     * Remove POS tags from the extracted NSU file  (s, w, c, ptr, u tags)
     */
    public void removePOSTags(){

        try {
            BufferedReader reader = Files.newBufferedReader(extractedNSUFile, charset);
            BufferedWriter writer = Files.newBufferedWriter(noTagNSUFile, charset);

            String line;

            //tags - carry POS and structure info in BNC, unnecessary for input to my program
            Pattern sTag = Pattern.compile("<s n=\"[0-9]+\">");
            Pattern wTag = Pattern.compile("<w [A-Z0-9\\-]+>");
            Pattern cTag = Pattern.compile("<c PUN>");
            Pattern pTag = Pattern.compile("<ptr target=[A-Z0-9]+>");
            Pattern uTag = Pattern.compile("</u>");

            while ((line = reader.readLine()) != null){

                // Remove all occurrences of tags
                Matcher matcherS = sTag.matcher(line);
                String noSline = matcherS.replaceAll("");

                Matcher matcherW = wTag.matcher(noSline);
                String noSWline = matcherW.replaceAll("");

                Matcher matcherC = cTag.matcher(noSWline);
                String noSWCline = matcherC.replaceAll("");

                Matcher matcherP = pTag.matcher(noSWCline);
                String noSWCPline = matcherP.replaceAll("");

                Matcher matcherU = uTag.matcher(noSWCPline);
                String finalLine = matcherU.replaceAll("");

                writer.append(finalLine);
                writer.newLine();
            }

            reader.close();
            writer.close();

        } catch (IOException x){
            System.err.format("IOException: %s%n", x);
        }

    }
}
