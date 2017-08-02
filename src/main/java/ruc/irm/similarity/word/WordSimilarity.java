package ruc.irm.similarity.word;

import ruc.irm.similarity.Similarity;

public interface WordSimilarity extends Similarity {
    double getSimilarity(String word1, String word2);
}
