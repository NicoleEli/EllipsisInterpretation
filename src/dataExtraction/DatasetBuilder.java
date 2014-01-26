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

            String line;
            while ((line = reader.readLine()) != null) {

                String[] lineSplit = line.split(" -- ");
                String raw = lineSplit[0];              //elliptical example
                String classification = lineSplit[1];            //classification of example e.g. NPE

                Tree parse = parser.getParse(raw);
                Collection typedDependencies = parser.getDependencies(parse);

                List<Feature> features = featureGenerator.genFeatures(parse,typedDependencies);
                System.out.println(features.size());

                /*
                If this is the first sentence, output the attribute/feature names
                 */
                if (!gotFeatureNames){
                    String names = "class";
                    for (int i = 0; i < features.size(); i++){
                        names = names + features.get(i).getName();

                        if (i != features.size()){
                            names = names + ", ";
                        }
                    }
                    writer.append(names);
                    writer.newLine();
                    writer.newLine();

                    gotFeatureNames = true;
                }

                String values = classification;
                for (int i = 0; i < features.size(); i++){
                    values = values + features.get(i).getValue();

                    if (i != features.size()){
                        values = values + ", ";
                    }
                }
                System.out.println(values);
                writer.append(values);
                writer.newLine();

                featureGenerator.reset();

            }

            reader.close();
            writer.close();

        } catch (IOException e){
            System.err.format("IOException: %s%n", e);
        }
    }

}
