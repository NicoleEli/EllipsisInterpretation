package typeClassification;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesianLogisticRegression;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         31/01/14
 */
public class NPEClassifier implements BinaryEllipsisClassifier {

    /** Plug in WEKA classifier of appropriate kind. */
    private Classifier wekaClassifier = new BayesianLogisticRegression();

    /** Dataset of training data for this classifier */
    private Instances dataset;

    /**
     *
     * @param attributes        vector of feature names
     */
    public NPEClassifier(FastVector attributes){
        String datasetName = "NPEclassification";
        dataset = new Instances(datasetName,attributes,100);
        dataset.setClassIndex(0);       //class attribute will be first in the feature vector

    }

    @Override
    public boolean classify(FastVector featureVector) {
        return false;  //TODO
    }


}
