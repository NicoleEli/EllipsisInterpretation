package ellipsisInterpretation;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
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
        List<String> candidates = followingPOSorCD(parse);
        candidates.addAll(rightmostInNounPhrase(parse));

        promoteDuplicates(candidates);

        provideDefault(candidates, "thing");

        return candidates;
    }

    /**
     * Return candidate antecedents for a sentence known to contain verb phrase ellipsis, in order of preference.
     */
    private List<String> resolveVPE(Tree parse, Collection typedDependencies) {

        //CURRENT MODEL: any verb phrase in the sentence
        List<String> candidates = allVerbPhrases(parse);
        //List<String> candidates = verbPhrasesWithoutDanglingMDs(parse);

        int ellipsisIndex = locateVPE(parse);

        System.out.println("VPE at " + ellipsisIndex + " / ");

        //promoteClosest(candidates, ellipsisIndex, parse);
        if (candidates.size() > 0) {
            //demoteDoesPhrases(candidates);
        }

        return candidates;
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
    private List<String> nounPhrases(Tree parse) {
        List<String> candidates = new ArrayList<String>();
        candidates = findSubtreesOfType(parse, "NP");
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
                    System.out.println(taggedYield);

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
     * NPE model: candidates are nouns which follow POS or CRD
     */
    private List<String> followingPOSorCD(Tree parse) {

        List<String> candidates = new ArrayList<String>();

        for (Tree t : parse.preOrderNodeList()) {
            if (t.label().value().startsWith("NN")) {
                Tree parent = t.parent(parse);
                int tIndex = parent.objectIndexOf(t);
                if (tIndex > 0) {
                    List<TaggedWord> siblingLeaves = parent.getChild(tIndex - 1).taggedYield();   //tagged yield of preceding sibling tree
                    TaggedWord preceding = siblingLeaves.get(siblingLeaves.size() - 1);
                    if (preceding.tag().equals("POS") || preceding.tag().equals("PRP$") || preceding.tag().equals("CD")) { //if t (the NN*) followed a POS or CRD, we care about it
                        List<Word> yield = t.yieldWords();
                        String candidate = "";
                        for (Word w : yield) {
                            candidate = candidate + " " + w.word();
                        }
                        candidates.add(candidate.trim());
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

        candidates = findSubtreesOfType(parse, "VP");

        return candidates;
    }

    private List<String> verbPhrasesWithoutSBAR(Tree parse) {
        return null;
    }

    private List<String> verbPhrasesWithoutDanglingMDs(Tree parse) {

        List<String> candidates = new ArrayList<String>();

        Iterator iterator = parse.iterator();

        while (iterator.hasNext()) {

            Tree subtree = (Tree) iterator.next();

            if (subtree.label().value().equals("VP")) {
                List<TaggedWord> taggedYield = subtree.taggedYield();


                Tree[] children = subtree.children();
                subtree.pennPrint();
                System.out.println(children[0].label() + " // " + children[1].label());
                boolean danglingMD = (children.length == 1 && children[0].label().value().equals("MD"))
                        || (children.length == 2 && children[0].label().value().equals("MD") && children[0].label().value().equals("RB"));
                if (!danglingMD) {
                    System.out.println(taggedYield);

                    String candidate = "";
                    for (int i = 0; i < taggedYield.size(); i++) {
                        candidate = candidate + " " + taggedYield.get(i).word();
                    }
                    candidates.add(candidate.trim());
                }
            }

        }
        return candidates;
    }

    private List<String> sentences(Tree parse) {
        List<String> candidates = new ArrayList<String>();

        List<TaggedWord> taggedWords = parse.taggedYield();
        int index = 0;
        String candidate = "";
        while (index < taggedWords.size()) {
            if (taggedWords.get(index).tag().equals(".")) {
                candidates.add(candidate);
                candidate = "";
            } else {
                candidate = candidate + " " + taggedWords.get(index).word();
            }
            index++;
        }

        return candidates;
    }

    /**
     * Given a parse tree, identify subtrees with a particular label.
     */
    private List<String> findSubtreesOfType(Tree parse, String subtreeType) {
        List<String> candidates = new ArrayList<String>();

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
        return candidates;
    }


    /**
     * Demote candidates containing phrases "does/do so/too" - optimisation for VPE
     *
     * @param candidates
     */
    private void demoteDoesPhrases(List<String> candidates) {
        List<String> demotedCandidates = new ArrayList<String>();
        for (String s : candidates) {
            boolean containsDoesPhrase = s.contains("does so") || s.contains("does too") || s.contains("do too") || s.contains("do so");
            if (containsDoesPhrase) {
                String candidate = candidates.get(candidates.indexOf(s));
                demotedCandidates.add(candidate);
            }
        }

        for (String ds : demotedCandidates) {
            candidates.remove(candidates.indexOf(ds));
            candidates.add(ds);
        }
    }

    /**
     * Identify location of verb phrase ellipsis within sentence.
     */
    private int locateVPE(Tree parse) {
        int vpeIndex = 0;
        List<Integer> potentialVPEIndices = new ArrayList<Integer>();

        List<TaggedWord> taggedYield = parse.taggedYield();

        String prevTag = "";
        String prevWord = "";
        boolean foundVPE = false;
        boolean foundPotentialVPE = false;
        int index = 0;
        int characterIndex = 0;
        while (index < taggedYield.size() && !foundVPE) {
            String curTag = taggedYield.get(index).tag();
            String curWord = taggedYield.get(index).word();
            if (prevTag.equals("MD") && !curTag.startsWith("VB")) {                                  //following a modal aux
                if (curWord.equals("n't") || curWord.equals("not")) {
                    if (checkNextWord(taggedYield, index)) {
                        foundVPE = true;
                        vpeIndex = characterIndex + curWord.length() + 1;
                    }
                } else {
                    foundVPE = true;
                    vpeIndex = characterIndex;
                }
            } else if ((prevWord.equals("do") || prevWord.equals("does")) && !curTag.startsWith("VB")) {
                if (curWord.equals("n't") || curWord.equals("not")) {
                    if (checkNextWord(taggedYield, index)) {
                        foundPotentialVPE = true;
                        potentialVPEIndices.add(characterIndex + curWord.length() + 1);
                    }
                } else {
                    foundPotentialVPE = true;
                    potentialVPEIndices.add(characterIndex);
                }
            }
            index++;
            characterIndex += curWord.length() + 1;
            prevWord = curWord;
            prevTag = curTag;
        }

        if (foundVPE) {
            return vpeIndex;
        } else if (foundPotentialVPE) {
            return potentialVPEIndices.get(0);
        } else {
            return -1;
        }

    }

    /**
     * Promote candidates occurring closer in the sentence to the ellipsis.
     */
    private void promoteClosest(List<String> candidates, int ellipsisIndex, Tree parse) {

        List<TaggedWord> taggedYield = parse.taggedYield();

        String fullInput = "";
        for (TaggedWord tw : taggedYield) {
            fullInput = fullInput + " " + tw.word();
        }

        Map<String, Integer> distances = new HashMap<String, Integer>();
        for (String s : candidates) {
            int dist = Math.abs(ellipsisIndex - fullInput.indexOf(s));
            System.out.printf("Candidate %s occurring at distance %d from ellipsis.%n", s, dist);
            if (dist != -1) {
                distances.put(s, dist);
            }
        }

        //sort with largest distance first
        List<String> sortedPromotions = sortPromotions(distances, false);

        candidates.removeAll(sortedPromotions);

        for (String s : sortedPromotions) {
            candidates.add(0, s);
        }


    }

    private boolean checkNextWord(List<TaggedWord> taggedYield, int currentIndex) {
        int nextIndex = currentIndex + 1;
        boolean isNextWordVerb = false;
        if (taggedYield.get(nextIndex).tag().startsWith("VB")) {
            isNextWordVerb = true;
        }
        return isNextWordVerb;
    }


    /**
     * Remove duplicates AND give precedence to words which occur multiple times.
     */
    private void promoteDuplicates(List<String> candidates) {
        Map<String, Integer> promotedCandidates = findDuplicates(candidates);

        candidates.removeAll(promotedCandidates.keySet());

        List<String> sortedPromotions = sortPromotions(promotedCandidates, true);

        for (String cand : sortedPromotions) {
            candidates.add(0, cand);
        }

    }

    /**
     * Sort candidates to be promoted by some associated value,
     *
     * @param promotedCandidates
     * @return
     */
    private List<String> sortPromotions(Map<String, Integer> promotedCandidates, boolean smallToLarge) {
        List<String> sortedPromotions = new ArrayList<String>();
        for (String k : promotedCandidates.keySet()) {
            if (sortedPromotions.size() == 0) {
                sortedPromotions.add(k);
            } else {
                int index = 0;
                int lastSmallerIndex = 0;
                if (smallToLarge) {
                    while (index < sortedPromotions.size() && (promotedCandidates.get(k) > promotedCandidates.get(sortedPromotions.get(index)))) {
                        index++;
                        lastSmallerIndex++;
                    }
                    sortedPromotions.add(lastSmallerIndex, k);
                } else {
                    while (index < sortedPromotions.size() && (promotedCandidates.get(k) < promotedCandidates.get(sortedPromotions.get(index)))) {
                        index++;
                        lastSmallerIndex++;
                    }
                    sortedPromotions.add(lastSmallerIndex, k);
                }
            }
        }
        return sortedPromotions;
    }

    /**
     * Remove duplicate candidates.
     */
    private void removeDuplicates(List<String> candidates) {
        Map<String, Integer> promotedCandidates = findDuplicates(candidates);
        candidates.removeAll(promotedCandidates.keySet());
    }

    /**
     * Find candidates which occur multiple times in a list, and produce a count of occurrences for each.
     */
    private Map<String, Integer> findDuplicates(List<String> candidates) {
        Map<String, Integer> promotedCandidates = new HashMap<String, Integer>();
        for (int i = 0; i < candidates.size(); i++) {
            String cand = candidates.get(i);
            int count = 0;
            for (String s : candidates) {
                if (s.equals(cand)) {
                    count++;
                }
            }
            if (count > 1) {
                promotedCandidates.put(cand, count);
            }
        }
        return promotedCandidates;
    }

    /**
     * Handling for the case where no antecedent has been identified - replace with a default.
     */
    private void provideDefault(List<String> candidates, String defaultValue) {
        if (candidates.size() == 0) {
            candidates.add(defaultValue);
        }
    }


}
