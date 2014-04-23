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
public class BinaryEllipsisClassifier {


    /** Dataset of training data for this classifier */
    protected Instances dataset;
    protected boolean datasetUpToDate = true;
    /** Plug in WEKA classifier of appropriate kind. */
    private Classifier wekaClassifier = new BayesianLogisticRegression();
    /** Vector of possible classes */
    private FastVector classValues;

    /**
     * Instantiate a new binary classifier
     *
     * @param attributes        vector of feature (attribute) names
     * @param datasetName       name of classification performed by this instance
     */
    public BinaryEllipsisClassifier(FastVector attributes, String datasetName){
        //binary classifier has two classes, true and false
        this.classValues = new FastVector();
        classValues.addElement("true");
        classValues.addElement("false");

        //add class attribute to feature attributes passed down
        Attribute classAtt = new Attribute("class",classValues);
        attributes.addElement(classAtt);

        //new dataset, class attribute being the one we just defined
        dataset = new Instances(datasetName,attributes,100);
        dataset.setClass(classAtt);

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
        String classification = (String) classValues.elementAt((int) predicted);

        System.out.println("Classified as: " + predicted + " " + classification);
        return false;  //TODO: return statement - use vector of classes and double from classifyInstance
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
