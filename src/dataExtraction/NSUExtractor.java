package dataExtraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.*;

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



    public void extractNSUs(){

        try {
            BufferedReader reader = Files.newBufferedReader(nsurf, charset);
            String line;
            while ((line = reader.readLine()) != null) {




            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }
}
