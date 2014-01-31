package typeClassification;

import weka.core.FastVector;
import weka.core.Instances;

/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Classification is handled by a series of binary classifiers, i.e. VPE/not-VPE, gapping/not-gapping etc.
 */
public interface BinaryEllipsisClassifier {

    public boolean classify(FastVector featureVector);

}
