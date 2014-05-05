package testing;

import controllers.ParsingController;
import edu.stanford.nlp.trees.Tree;
import ellipsisDetection.EllipsisType;
import ellipsisInterpretation.EllipsisInterpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         05/05/14
 *         <p/>
 *         Prepares assessment sheets for manual evaluation of ellipsis interpreter.
 */
public class InterpretationAsessment {

    Charset charset = Charset.forName("UTF-8");

    ParsingController parser;
    EllipsisInterpreter interpreter;

    public InterpretationAsessment(ParsingController parser, EllipsisInterpreter interpreter) {
        this.parser = parser;
        this.interpreter = interpreter;
    }

    /**
     * Prepare an assessment sheet for some raw data with known ellipsis types
     */
    public void prepareAssessmentSheet(String rawDataPath, String outputPath) {
        try {

            BufferedReader dataReader = Files.newBufferedReader(Paths.get(rawDataPath), charset);
            BufferedWriter sheetWriter = new BufferedWriter(new FileWriter(outputPath, false));

            String dataLine;
            while ((dataLine = dataReader.readLine()) != null){

                String[] splitLine = dataLine.split("::");
                String sentence = splitLine[0].trim();
                String ellipsisType = splitLine[1].trim();
                EllipsisType type = EllipsisType.valueOf(ellipsisType);

                Tree parse = parser.getParse(sentence);
                Collection typedDependencies = parser.getDependencies(parse);

                String antecedent = interpreter.interpretEllipsis(parse, typedDependencies, type);

                String writeLine = String.format("Sentence: %s%nAntecedent: %s%%nnGood\t\tBad%n%n", sentence, antecedent);

                sheetWriter.append(writeLine);

            }

            dataReader.close();
            sheetWriter.close();

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

    }

}
