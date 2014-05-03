package dataExtraction;

import edu.stanford.nlp.trees.Tree;
import ellipsisDetection.*;

import controllers.ParsingController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

            //Before reading any data, output the attribute/feature names
            String names = "class, ";
            Set<String> featureNames = featureGenerator.getFeatureNames();
            for (String featureName : featureNames){
                names = names + featureName + ", ";
            }
            //writer.append(names);
            //writer.newLine();

            String line;
            int numRead = 0;
            while ((line = reader.readLine()) != null) {

                String[] lineSplit = line.split(" :: ");
                String raw = lineSplit[0];                       //data item
                String classification = lineSplit[1];            //data item classification e.g. NPE

                Tree parse = parser.getParse(raw);
                Collection typedDependencies = parser.getDependencies(parse);

                Map<String,Integer> features = featureGenerator.genFeatures(parse,typedDependencies);

                String values = classification+", ";
                for (String featureName : featureNames){
                    values = values + features.get(featureName) + ", ";
                }
                writer.append(values);
                writer.newLine();

                featureGenerator.reset();
                numRead++;
                System.out.println("DatasetBuilder has read in data item "+numRead);

            }

            reader.close();
            writer.close();

        } catch (IOException e){
            System.err.format("IOException: %s%n", e);
        }
    }

}
