package controllers;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         01/11/13
 */
public class ParsingController {

    LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    TreebankLanguagePack treebankLanguagePack = new PennTreebankLanguagePack();
    GrammaticalStructureFactory grammaticalStructureFactory = treebankLanguagePack.grammaticalStructureFactory();
    TokenizerFactory toke = treebankLanguagePack.getTokenizerFactory();

    public Tree getParse(List<HasWord> sentence){
        Tree parse = lexicalizedParser.apply(sentence);
        return parse;
    }

    public Tree getParse(String sentence){
        Tokenizer<? extends HasWord> tokenizer = toke.getTokenizer(new StringReader(sentence));
        List <? extends HasWord> tokenizedSentence = tokenizer.tokenize();
        Tree parse = lexicalizedParser.apply(tokenizedSentence);
        return parse;
    }

    public Collection getDependencies(Tree parse){
        GrammaticalStructure gStruct = grammaticalStructureFactory.newGrammaticalStructure(parse);
        Collection typedDependencies = gStruct.typedDependenciesCCprocessed();
        return typedDependencies;
    }

}
