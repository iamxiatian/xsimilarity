package ruc.irm.similarity;

import ruc.irm.similarity.sentence.SentenceSimilarity;
import ruc.irm.similarity.sentence.morphology.MorphoSimilarity;
import ruc.irm.similarity.word.WordSimilarity;
import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;

public class SimilarityFactory {
    private static WordSimilarity wordSimilarity = XiaConceptParser.getInstance();
    private static SentenceSimilarity sentenceSimilarity = MorphoSimilarity.getInstance();
    
    private SimilarityFactory(){}
    
    public static WordSimilarity getWordSimilarity(){
        return wordSimilarity;
    }
    
    public static SentenceSimilarity getSentenceSimilarity(){
        return sentenceSimilarity;
    }
}
