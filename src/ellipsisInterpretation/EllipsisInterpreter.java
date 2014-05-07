package ellipsisInterpretation;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.Tree;
import ellipsisDetection.EllipsisType;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         03/05/14
 *         <p/>
 *         Class to handle finding an antecedent for an instance of ellipsis in a given sentence.
 */
public class EllipsisInterpreter {

    /**
     * Given the parsed representation of a sentence and the type of ellipsis detected in it, provide an antecedent.
     *
     * @param parse             Parse of sentence containing ellipsis
     * @param typedDependencies Typed dependencies of same sentence
     * @param ellipsisType      Type of ellipsis detected in that sentence
     * @return An antecedent for the elliptical construction
     */
    public String interpretEllipsis(Tree parse, Collection typedDependencies, EllipsisType ellipsisType) {

        List<String> candidates = new ArrayList<String>();

        switch (ellipsisType) {
            case NPE:
                candidates = resolveNPE(parse, typedDependencies);
                break;
            case VPE:
                candidates = resolveVPE(parse, typedDependencies);
                break;
            case NSU:
                candidates = resolveNSU(parse, typedDependencies);
                break;
            case GAPPING:
                //TODO: content
                break;
            default:
                //TODO: content
                break;
        }

        System.out.println();
        System.out.println(candidates);

        //assume candidates presented in order of preference
        //TODO: this is currently a heuristic "take the first one"
        if (candidates.size() > 0) {
            return candidates.get(0);
        }

        System.err.println("No candidate antecedents identified");
        return null;
    }

    /**
     * Return candidate antecedents for a sentence known to contain noun phrase ellipsis, in order of preference.
     */
    private List<String> resolveNPE(Tree parse, Collection typedDependencies) {

        //CURRENT MODEL: rightmost non-elided nouns. v. simplistic.
        List<String> candidates = rightmostInNounPhrase(parse);

        return candidates;
    }

    /**
     * Return candidate antecedents for a sentence known to contain verb phrase ellipsis, in order of preference.
     */
    private List<String> resolveVPE(Tree parse, Collection typedDependencies) {

        //CURRENT MODEL: any verb phrase in the sentence
        List<String> candidates = allVerbPhrases(parse);

        //optimisation: demote verb phrases with does phrases
        if (candidates.size() > 1){
            demoteDoesPhrases(candidates);
        }

        return candidates;
    }

    /**
     * Demote candidates containing phrases "does/do so/too" - optimisation for VPE
     * @param candidates
     */
    private void demoteDoesPhrases(List<String> candidates) {
        List<String> demotedCandidates = new ArrayList<String>();
        for (String s : candidates){
            boolean containsDoesPhrase = s.contains("does so") || s.contains("does too") || s.contains("do too") ||  s.contains("do so");
            if (containsDoesPhrase){
                String candidate = candidates.get(candidates.indexOf(s));
                demotedCandidates.add(candidate);
            }
        }

        for (String ds : demotedCandidates){
            candidates.remove(candidates.indexOf(ds));
            candidates.add(candidates.size()-1, ds);
        }
    }

    /**
     * Return candidate antecedents for a non-sentential utterance, in order of preference.
     */
    private List<String> resolveNSU(Tree parse, Collection typedDependencies) {

        //CURRENT MODEL: the antecedent is a full sentence
        List<String> candidates = sentences(parse);

        return candidates;
    }

    /**
     * Simplest model for NPE resolution: candidate antecedents are noun phrases.
     */
    private List<String> nounPhrases(Tree parse){
        List<String> candidates = new ArrayList<String>();
        findSubtreesOfType(candidates, parse, "NP");
        return candidates;
    }

    /**
     * Simple model for NPE resolution: candidate antecedents are the rightmost nouns in NPs within the sentence.
     * (NPs which finish in cardinals, adjectives or possessives are not included as antecedent donors.)
     * //TODO: adjectives are only in the above list because there is no ordinal tag!
     *
     * @param parse
     */
    private List<String> rightmostInNounPhrase(Tree parse) {

        List<String> candidates = new ArrayList<String>();

        Iterator iterator = parse.iterator();

        //Find subtrees of type "NP"
        while (iterator.hasNext()) {

            Tree subtree = (Tree) iterator.next();

            if (subtree.label().value().equals("NP")) {
                List<TaggedWord> taggedYield = subtree.taggedYield();

                System.out.println(taggedYield);


                //if there is a possessive or cardinal (or ordinal??) tag attached to the final word in this NP, this NP is elided
                TaggedWord finalWord = taggedYield.get(taggedYield.size() - 1);
                String finalTag = finalWord.tag();
                boolean finalTagOfInterest = finalTag.equals("POS") || finalTag.equals("CD") || finalTag.startsWith("JJ");
                if (finalTagOfInterest) {
                    //case of elided NP
                    //TODO: use this identification to mark elliptical position in output
                }
                //and in all other cases, there might be an antecedent here (under this simple model)
                else {
                    System.out.println("else case " + taggedYield);

                    String rightmostNoun = null;
                    for (TaggedWord tw : taggedYield) {
                        //identify rightmost noun in the NP
                        if (tw.tag().startsWith("NN")) {
                            rightmostNoun = tw.word();
                        }
                    }
                    if (rightmostNoun != null) {
                        candidates.add(rightmostNoun);
                    }
                }
            }

        }

        return candidates;
    }

    /**
     * Simple model for resolving verb phrase ellipsis - candidates are verb phrases occurring in the sentence.
     *
     * @param parse
     * @return
     */
    private List<String> allVerbPhrases(Tree parse) {

        List<String> candidates = new ArrayList<String>();

        findSubtreesOfType(candidates, parse, "VP");

        return candidates;
    }

    private List<String> sentences(Tree parse) {
        List<String> candidates = new ArrayList<String>();

        List<TaggedWord> taggedWords = parse.taggedYield();
        int index = 0;
        String candidate = "";
        while (index < taggedWords.size()){
            if (taggedWords.get(index).tag().equals(".")){
                candidates.add(candidate);
                candidate = "";
            } else {
                candidate = candidate + " " + taggedWords.get(index).word();
            }
            index++;
        }

        return candidates;
    }


    private void findSubtreesOfType(List<String> candidates, Tree parse, String subtreeType) {
        parse.pennPrint();

        Iterator iterator = parse.iterator();

        while (iterator.hasNext()) {

            Tree subtree = (Tree) iterator.next();

            if (subtree.label().value().equals(subtreeType)) {
                List<TaggedWord> taggedYield = subtree.taggedYield();

                System.out.println(taggedYield);

                String candidate = "";
                for (int i = 0; i < taggedYield.size(); i++) {
                    candidate = candidate + " " + taggedYield.get(i).word();
                }
                candidates.add(candidate.trim());
            }

        }
    }

}
