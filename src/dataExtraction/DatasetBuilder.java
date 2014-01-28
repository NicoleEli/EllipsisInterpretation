package dataExtraction;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;
import typeClassification.*;

import controllers.ParsingController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         26/01/14
 *
 * Build a training dataset of attributes from raw data, to be given to WEKA algorithms for learning.
 */
public class DatasetBuilder {

    Charset charset = Charset.forName("UTF-8");

    Path rawDataFile;                     //file to read raw data from
    Path datasetFile;                     //file to write attribute data to

    ParsingController parser;
    FeatureGenerator featureGenerator;      //gen features from given raw data

    public DatasetBuilder(FeatureGenerator fGen, String rawDataPath, String datasetPath, ParsingController parseController){
        this.featureGenerator = fGen;
        this.rawDataFile = Paths.get(rawDataPath);
        this.datasetFile = Paths.get(datasetPath);
        this.parser = parseController;
    }

    public void buildDataset(){
        try{

            BufferedReader reader = Files.newBufferedReader(rawDataFile, charset);
            BufferedWriter writer = Files.newBufferedWriter(datasetFile, charset);

            boolean gotFeatureNames = false;
            featureGenerator.initialiseFeatures();

            String line;
            int numRead = 0;
            while ((line = reader.readLine()) != null) {

                String[] lineSplit = line.split(" :: ");
                String raw = lineSplit[0];              //elliptical example
                String classification = lineSplit[1];            //classification of example e.g. NPE

                Tree parse = parser.getParse(raw);
                Collection typedDependencies = parser.getDependencies(parse);

                Map<String,Integer> features = featureGenerator.genFeatures(parse,typedDependencies);

                /*
                If this is the first sentence, output the attribute/feature names
                 */
                if (!gotFeatureNames){
                    String names = "class, ";
                    for (String featureName : features.keySet()){
                        names = names + featureName + ", ";
                    }
                    writer.append(names);
                    writer.newLine();
                    writer.newLine();

                    gotFeatureNames = true;
                }

                String values = classification+", ";
                for (String featureName : features.keySet()){
                    values = values + features.get(featureName) + ", ";
                }
                writer.append(values);
                writer.newLine();

                featureGenerator.reset();
                numRead++;
                System.out.println(numRead);

            }

            reader.close();
            writer.close();

        } catch (IOException e){
            System.err.format("IOException: %s%n", e);
        }
    }

}
