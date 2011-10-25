package ruc.irm.similarity.word;

import junit.framework.TestCase;

public class CharBasedSimilarityTest extends TestCase {
    public void test() {
        CharBasedSimilarity sim = new CharBasedSimilarity();
        String s1 = "手机";
        String s2 = "飞机";

        assertTrue(sim.getSimilarity(s1, s2) > 0);
    }
}
