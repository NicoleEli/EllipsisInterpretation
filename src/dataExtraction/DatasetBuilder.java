package dataExtraction;

import typeClassification.FeatureGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         26/01/14
 *
 * Build a training dataset of attributes from raw data, to be given to WEKA algorithms for learning.
 */
public class DatasetBuilder {

    Charset charset = Charset.forName("US-ASCII");

    Path rawDataFile;                     //file to read raw data from
    Path datasetFile;                     //file to write attribute data to

    FeatureGenerator featureGenerator;      //gen features from given raw data

    public DatasetBuilder(FeatureGenerator fGen, String rawDataPath, String datasetPath){
        this.featureGenerator = fGen;
        this.rawDataFile = Paths.get(rawDataPath);
        this.datasetFile = Paths.get(datasetPath);
    }

    public void buildDataset(){
        try{

            BufferedReader reader = Files.newBufferedReader(rawDataFile, charset);
            BufferedWriter writer = Files.newBufferedWriter(datasetFile, charset);

            String line;
            while ((line = reader.readLine()) != null) {

                String[] lineSplit = line.split(" -- ");

            }

            reader.close();
            writer.close();

        } catch (IOException e){
            System.err.format("IOException: %s%n", e);
        }
    }

}
