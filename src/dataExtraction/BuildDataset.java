package dataExtraction;

import typeClassification.FeatureGenerator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         26/01/14
 *
 * Build a training dataset of attributes from raw data, to be given to WEKA algorithms for learning.
 */
public class BuildDataset {

    FeatureGenerator featureGenerator;      //gen features from given raw data
    String rawDataPath;                     //file to read raw data from
    String datasetPath;                     //file to write attribute data to

    public BuildDataset(FeatureGenerator fGen, String rawDataPath, String datasetPath){
        this.featureGenerator = fGen;
        this.rawDataPath = rawDataPath;
        this.datasetPath = datasetPath;
    }

    public void buildDataset(){

    }

}
