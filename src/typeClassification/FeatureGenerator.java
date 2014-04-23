package typeClassification;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         02/11/13
 */
public class FeatureGenerator {

    String featureNameFile;

    public static final String START_MARKER = "START";
    Map<String, Integer> featureList = new LinkedHashMap<String, Integer>();
    String[] punctuationArray = {".", ",", "'", "\"", "-", "/", "\\", "(", ")", "!", "?", ":", ";"};
    List<String> punctuation = Arrays.asList(punctuationArray);

    public FeatureGenerator(String featureNameFile){
        this.featureNameFile = featureNameFile;
    }

    public Map<String, Integer> initialiseFeatures() {
        try {

            BufferedReader reader = Files.newBufferedReader(Paths.get(featureNameFile),
                    Charset.forName("UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                featureList.put(line.trim(), 0);
            }

            reader.close();
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return featureList;
    }

    //returns list of features, populated with correct values
    public Map<String, Integer> genFeatures(Tree parse, Collection typedDependencies) {

        List<Word> words = parse.yieldWords();
        List<TaggedWord> tagWords = parse.taggedYield();
        tagWords.add(0, new TaggedWord("", START_MARKER));     //special start-of-sentence marker

        //Populating feature vector
        getSentenceLength(words);                   //Sentence length
        getPOSCounts(tagWords);                     //POS counts
        getPOSPairCounts(tagWords);                 //POS pair counts
        existsConjunction(tagWords);                //Is there a conjunction?
        existsDoesPhrase(words);                    //Is there a "does too", "does so", "doesn't" or similar phrase?
        isWPFinal(tagWords);                        //Is the sentence wh-pronoun-final?
        existsNegFinalVP(parse);                    //Is there a neg-final VP?
        getAugmentedPOS(parse);                     //counts of POS tags plus ancestor information

        return featureList;
    }

    /**
     * Returns the length of the sentence represented by the given parse tree.
     *
     * @param words The words making up the given sentence.
     * @return The sentence length.
     */
    private void getSentenceLength(List<Word> words) {
        int size = words.size();
        for (Word w : words) {
            boolean isPunctuation = punctuation.contains(w.value().trim());
            if (isPunctuation) {
                size -= 1;
            }
        }
        featureList.put("sentence length", size);
    }

    /**
     * Generates features for number of each kind of POS tag.
     * (Note POS tags include punctuation tags.)
     *
     * @param tagWords Words & associated POS tags for given sentence.
     */
    private void getPOSCounts(List<TaggedWord> tagWords) {
        Map<String, Integer> tagCounts = new HashMap<String, Integer>();
        for (TaggedWord tw : tagWords) {
            String key = tw.tag();
            if (!key.equals(START_MARKER)) {
                if (tagCounts.containsKey(key)) {
                    tagCounts.put(key, tagCounts.get(key) + 1);
                } else {
                    tagCounts.put(key, 1);
                }
            }
        }
        for (String tag : tagCounts.keySet()) {
            if (featureList.containsKey(tag)) {
                featureList.put(tag, tagCounts.get(tag));
            } else {
                System.out.println("tried to add different key " + tag);
            }
        }
    }

    /**
     * Generates features for pairs of neighbouring POS tags.
     *
     * @param tagWords Words & associated POS tags for up given sentence.
     */
    private void getPOSPairCounts(List<TaggedWord> tagWords) {
        Map<String, Integer> tagCounts = new HashMap<String, Integer>();

        for (int i = 0; i < tagWords.size() - 1; i++) {
            String pair = tagWords.get(i).tag() + "/" + tagWords.get(i + 1).tag();
            if (tagCounts.containsKey(pair)) {
                tagCounts.put(pair, tagCounts.get(pair) + 1);
            } else {
                tagCounts.put(pair, 1);
            }
        }
        for (String key : tagCounts.keySet()) {
            if (featureList.containsKey(key)) {
                featureList.put(key, tagCounts.get(key));
            } else {
                System.out.println("tried to add different key " + key);
            }
        }

    }

    /**
     * Determines whether the given sentence contains a conjunction.
     *
     * @param tagWords Words & associated POS tags for up given sentence.
     * @return 1 if there is a conjunction, 0 otherwise
     */
    //TODO: Differentiate between prep & sub-conj ("IN")
    private void existsConjunction(List<TaggedWord> tagWords) {
        for (TaggedWord tw : tagWords) {
            if (tw.tag().equals("CC") || tw.tag().equals("IN")) {
                featureList.put("exists conjunction", 1);
            }
        }
    }

    /**
     * Is there a VP in the sentence which ends in a negative?
     *
     * @param parse     Parse tree of the sentence
     */
    private void existsNegFinalVP(Tree parse) {
        String[] negatives = {"not", "n't", "no"};
        List<Tree> verbPhrases = new ArrayList<Tree>();
        Iterator<Tree> iterator = parse.iterator();

        Tree current;
        while (iterator.hasNext()){
            current = iterator.next();
            String currentLabel = current.label().value().trim();
            if (currentLabel.equals("VP")){
                verbPhrases.add(current);
            }
        }
        boolean hadNegative = false;
        for (Tree vp : verbPhrases){
            List<Word> vpWords = vp.yieldWords();
            Word lastWord = vpWords.get(vpWords.size()-1);
            for (String n : negatives){
                if (lastWord.word().trim().equals(n)){
                    hadNegative = true;
                }
            }
        }
        if (hadNegative) {
            featureList.put("exists neg-final VP", 1);
        }
    }

    /**
     * Is the last word of the sentence a Wh-pronoun?
     *
     * @param tagWords Words & associated POS tags for up given sentence.
     * @return 1 if true, 0 otherwise
     */
    private void isWPFinal(List<TaggedWord> tagWords) {
        int finalIndex = tagWords.size() - 1;
        TaggedWord finalWord = tagWords.get(finalIndex);
        while (punctuation.contains(finalWord.tag())) {
            tagWords.remove(finalIndex);
            finalIndex = tagWords.size() - 1;
            finalWord = tagWords.get(finalIndex);
        }
        if (finalWord.tag().equals("WP")) {
            featureList.put("WP-final", 1);
        } else {
        }
    }

    /**
     * Do phrases like "does so", "does too", "doesn't" occur in the given sentence?
     *
     *
     * @param words     Words of parsed sentence
     * @return
     */
    private void existsDoesPhrase(List<Word> words) {
        for (Word w : words){
            String word = w.word().trim().toLowerCase();
            if (word.equals("does") || word.equals("do")){
                String nextWord = words.get(words.indexOf(w)+1).word().trim().toLowerCase();
                if (nextWord.equals("too") || nextWord.equals("so") || nextWord.equals("n't")){
                    featureList.put("exists does phrase", 1);
                }
            }
        }
    }

    /**
     * Return a list of elements which encode POS in the given sentence augmented with
     * information from their ancestors in the parse tree.
     *
     * @param parse parse tree of given sentence
     * @return list of augmented POS tags
     */
    private void getAugmentedPOS(Tree parse) {
        Map<String, Integer> augTagCounts = new HashMap<String, Integer>();

        List<Tree> leaves = parse.getLeaves();

        for (Tree l : leaves){
            Tree parent = l.parent(parse);
            String posTag = parent.label().value().trim();
            Tree grandparent = parent.parent(parse);
            String grandparentLabel = grandparent.label().value().trim();
            String featureLabel = "Aug-" + posTag+"/"+grandparentLabel;

            if (augTagCounts.containsKey(featureLabel)){
                augTagCounts.put(featureLabel,augTagCounts.get(featureLabel)+1);
            } else {
                augTagCounts.put(featureLabel,1);
            }

        }

        for (String augTag : augTagCounts.keySet()) {
            if (featureList.containsKey(augTag)) {
                featureList.put(augTag, augTagCounts.get(augTag));
            } else {
                System.out.println("tried to add different key " + augTag);
            }
        }
    }

    public void printFeatures() {
        for (String feature : featureList.keySet()) {
            System.out.println(feature + " : " + featureList.get(feature));
        }
    }

    public Set<String> getFeatureNames(){
        return featureList.keySet();
    }

    public void reset() {
        for (String key : featureList.keySet()) {
            featureList.put(key, 0);
        }
    }


}
