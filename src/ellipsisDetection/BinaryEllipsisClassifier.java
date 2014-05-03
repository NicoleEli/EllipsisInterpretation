package ellipsisDetection;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesianLogisticRegression;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


/**
 * @author Nicole Williams
 *         28/10/13
 *         <p/>
 *         Classification is handled by a series of binary classifiers, i.e. VPE/not-VPE, gapping/not-gapping etc.
 */
public class BinaryEllipsisClassifier {


    public String name;

    /**
     * Dataset of training data for this classifier
     */
    protected Instances dataset;
    protected boolean datasetUpToDate = true;
    /**
     * Plug in WEKA classifier of appropriate kind.
     */
    private Classifier wekaClassifier = new BayesianLogisticRegression();
    /**
     * Vector of possible classes
     */
    private FastVector classValues;
    Attribute classAtt;

    /**
     * Instantiate a new binary classifier
     *
     * @param attributes  vector of feature (attribute) names
     * @param datasetName name of classification performed by this instance
     */
    public BinaryEllipsisClassifier(FastVector attributes, String datasetName) {
        //binary classifier has two classes, true and false
        this.classValues = new FastVector();
        classValues.addElement("true");
        classValues.addElement("false");

        //add class attribute to feature attributes passed down
        classAtt = new Attribute("class", classValues);
        attributes.insertElementAt(classAtt, 0);

        //new dataset, class attribute being the one we just defined
        dataset = new Instances(datasetName, attributes, 100);
        dataset.setClass(classAtt);

        this.name = datasetName;

    }

    public boolean classify(FastVector featureVector) {
        try {
            if (dataset.numInstances() == 0) {
                throw new Exception("No classifier available");
            }

            if (!datasetUpToDate) {
                wekaClassifier.buildClassifier(dataset);
                datasetUpToDate = true;
            }

            Instance testInstance = makeInstance(featureVector, false);

            double predicted = wekaClassifier.classifyInstance(testInstance);
            String classification = (String) classValues.elementAt((int) predicted);

            //System.out.println("Classified as: " + predicted + " " + classification);

            if (classification == "true"){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
        }
        return false;
    }

    public double[] getDistribution(FastVector featureVector){
        try {
            if (dataset.numInstances() == 0) {
                throw new Exception("No classifier available");
            }

            if (!datasetUpToDate) {
                wekaClassifier.buildClassifier(dataset);
                datasetUpToDate = true;
            }

            Instance testInstance = makeInstance(featureVector, false);

            double[] distribution = wekaClassifier.distributionForInstance(testInstance);

            return distribution;

        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
        }
        return null;
    }

    protected Instance makeInstance(FastVector featureValues, boolean isKnownClass) {
        int numAtts = dataset.numAttributes();
        Instance instance = new Instance(numAtts);
        instance.setDataset(dataset);


        //add instance attribute values - att 0 is class is nominal and only present for training data; all others are numeric
        if(isKnownClass){
            instance.setClassValue((String) featureValues.elementAt(0));
            for (int i = 1; i < numAtts; i++) {
                instance.setValue(i, Integer.valueOf((String) featureValues.elementAt(i)));
            }
        } else {
            instance.setClassMissing();
            for (int i = 1; i < numAtts; i++) {
                instance.setValue(i, (Integer) featureValues.elementAt(i-1));
            }
        }




        return instance;
    }

    /**
     * Update dataset with a new piece of training data.
     *
     * @param featureVector vector of feature values
     */
    public void updateTrainingData(FastVector featureVector) {
        Instance instance = makeInstance(featureVector, true);
        dataset.add(instance);
        datasetUpToDate = false;
    }
}
