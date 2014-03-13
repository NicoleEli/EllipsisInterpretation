package typeClassification;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesianLogisticRegression;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Classification is handled by a series of binary classifiers, i.e. VPE/not-VPE, gapping/not-gapping etc.
 */
public abstract class BinaryEllipsisClassifier {


    /** Dataset of training data for this classifier */
    protected Instances dataset;
    protected boolean datasetUpToDate = true;
    /** Plug in WEKA classifier of appropriate kind. */
    private Classifier wekaClassifier = new BayesianLogisticRegression();

    /**
     * Instantiate a new binary classifier
     *
     * @param attributes        vector of feature (attribute) names
     * @param datasetName       name of classification performed by this instance
     */
    public BinaryEllipsisClassifier(FastVector attributes, String datasetName){
        dataset = new Instances(datasetName,attributes,100);
        dataset.setClassIndex(0);       //class attribute will be first in the feature vector
    }

    public boolean classify(FastVector featureVector) throws Exception {

        if (dataset.numInstances() == 0){
            throw new Exception("No classifier available");
        }

        if (!datasetUpToDate){
            wekaClassifier.buildClassifier(dataset);
            datasetUpToDate = true;
        }

        Instance testInstance = makeInstance(featureVector);

        double predicted = wekaClassifier.classifyInstance(testInstance);

        System.out.println("Classified as: " + predicted);
        return false;  //TODO: return statement
    }

    protected Instance makeInstance(FastVector featureValues){
        int numAtts = dataset.numAttributes();
        Instance instance = new Instance(numAtts);

        for (int i=0; i<numAtts; i++){
            instance.setValue(i, (String) featureValues.elementAt(i)); //TODO: change from FastVector to make this not String??
        }
        instance.setDataset(dataset);
        return instance;
    }

    /**
     * Update dataset with a new piece of training data.
     *
     * @param featureVector         vector of feature values
     */
    public void updateTrainingData(FastVector featureVector){
        Instance instance = makeInstance(featureVector);
        instance.setClassValue((String) featureVector.elementAt(0)); //TODO: need to do this??
        dataset.add(instance);
        datasetUpToDate = false;
    }
}
