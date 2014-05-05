package ellipsisInterpretation;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.Tree;
import ellipsisDetection.EllipsisType;

import java.util.*;

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

        List<String> candidates = new ArrayList<String>();

        //CURRENT MODEL: rightmost non-elided nouns. v. simplistic.
        candidates = rightmostNonelidedNouns(candidates, parse);

        System.out.println();
        System.out.println(candidates);

        //heuristic: take first occurring candidate antecedent
        if (candidates.size() > 0){
            return candidates.get(0);
        }

        System.err.println("No candidate antecedents identified");
        return null;
    }

    /**
     * Simple model for NPE resolution: candidate antecedents are the rightmost nouns in NPs within the sentence.
     * (NPs which finish in cardinals, adjectives or possessives are not included as antecedent donors.)
     * //TODO: adjectives are only in the above list because there is no ordinal tag!
     *
     * @param candidates
     * @param parse
     */
    private List<String> rightmostNonelidedNouns(List<String> candidates, Tree parse) {

        Iterator iterator = parse.iterator();

        //Find subtrees of type "NP"
        while (iterator.hasNext()){

            Tree subtree = (Tree) iterator.next();

            if (subtree.label().value().equals("NP")){
                List<TaggedWord> taggedYield = subtree.taggedYield();

                System.out.println(taggedYield);


                //if there is a possessive or cardinal (or ordinal??) tag attached to the final word in this NP, this NP is elided
                TaggedWord finalWord = taggedYield.get(taggedYield.size()-1);
                String finalTag = finalWord.tag();
                boolean finalTagOfInterest = finalTag.equals("POS") || finalTag.equals("CD") || finalTag.startsWith("JJ");
                if (finalTagOfInterest){
                    //case of elided NP
                    //TODO: use this identification to mark elliptical position in output
                }
                //and in all other cases, there might be an antecedent here (under this simple model)
                else {
                    System.out.println("else case "+taggedYield);

                    String rightmostNoun = null;
                    for(TaggedWord tw : taggedYield){
                        //identify rightmost noun in the NP
                        if (tw.tag().startsWith("NN")){
                            rightmostNoun = tw.word();
                        }
                    }
                    if (rightmostNoun != null){
                        candidates.add(rightmostNoun);
                    }
                }
            }

        }

        return candidates;
    }

    private String resolveVPE(Tree parse, Collection typedDependencies){
        List<String> candidates = new ArrayList<String>();

        //CURRENT MODEL:
        candidates = renameThisModel(candidates, parse);

        System.out.println();
        System.out.println(candidates);

        //heuristic: take first occurring candidate antecedent
        if (candidates.size() > 0){
            return candidates.get(0);
        }

        System.err.println("No candidate antecedents identified");
        return null;
    }

    private List<String> renameThisModel(List<String> candidates, Tree parse){


        return candidates;
    }


}
