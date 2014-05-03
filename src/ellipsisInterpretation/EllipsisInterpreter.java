package ellipsisInterpretation;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.Tree;
import ellipsisDetection.EllipsisType;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         03/05/14
 *
 * Class to handle finding an antecedent for an instance of ellipsis in a given sentence.
 */
public class EllipsisInterpreter {

    /**
     * Given the parsed representation of a sentence and the type of ellipsis detected in it, provide an antecedent.
     *
     * @param parse                     Parse of sentence containing ellipsis
     * @param typedDependencies         Typed dependencies of same sentence
     * @param ellipsisType              Type of ellipsis detected in that sentence
     * @return                          An antecedent for the elliptical construction
     */
    public String interpretEllipsis(Tree parse, Collection typedDependencies, EllipsisType ellipsisType){

        switch (ellipsisType){
            case NPE:
                return resolveNPE(parse, typedDependencies);
            case VPE:
                return resolveVPE(parse, typedDependencies);
            case NSU:
                //TODO: content
                break;
            case GAPPING:
                //TODO: content
                break;
            default:
                //TODO: content
                break;
        }


        return null;        //TODO: default return statement.
    }

    /**
     * Return an antecedent for a sentence known to contain noun phrase ellipsis
     */
    private String resolveNPE(Tree parse, Collection typedDependencies){

        Iterator iterator = parse.iterator();

        while (iterator.hasNext()){
            Tree next = (Tree) iterator.next();
            System.out.println(next.label());       //TODO: acquire NP subtrees
        }

        return null;        //TODO: default return statement.
    }

    private String resolveVPE(Tree parse, Collection typedDependencies){
        return null;        //TODO: default return statement.
    }


}
