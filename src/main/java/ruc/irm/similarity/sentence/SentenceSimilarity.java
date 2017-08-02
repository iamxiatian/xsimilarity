package ruc.irm.similarity.sentence;

import ruc.irm.similarity.Similarity;

public interface SentenceSimilarity extends Similarity {
    double getSimilarity(String sentence1, String sentence2);
}
