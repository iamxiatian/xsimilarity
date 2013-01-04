package ruc.irm.similarity.sentence;

import org.junit.Test;

import ruc.irm.similarity.sentence.morphology.SemanticSimilarity;

public class SemanticSimilarityTest {

    @Test
    public void test() {
        String s1 = "一个伟大的国家，中国";
        String s2 = "中国是一个伟大的国家";
        
        SemanticSimilarity similarity = SemanticSimilarity.getInstance();
        double sim = similarity.getSimilarity(s1, s2);
        System.out.println("sim ==> " + sim);
        
        
    }

}
