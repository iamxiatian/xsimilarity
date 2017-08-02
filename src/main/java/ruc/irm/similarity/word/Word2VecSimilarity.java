package ruc.irm.similarity.word;

import ruc.irm.similarity.sentence.SegmentProxy;
import ruc.irm.similarity.util.Word2Vec;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * https://pan.baidu.com/s/1nvke1F7
 *
 * @author <a href="mailto:xiat@ruc.edu.cn">XiaTian</a>
 * @date Feb 07, 2015 12:14 PM
 */
public class Word2VecSimilarity implements WordSimilarity {
    private Word2Vec model = null;

    public Word2VecSimilarity() {
        try {
            SegmentProxy.segment("启动分词程序");
            model = new Word2Vec();
            model.loadModel("./wiki.word2vec.bin");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<String> segment(String text) {
        return SegmentProxy.segmentAsStrings(text);

    }

    public double getSimilarity(String text1, String text2) {
        float[] vec1 = model.getWordVector(segment(text1));
        float[] vec2 = model.getWordVector(segment(text2));
        return distance(vec1, vec2);
    }

    private double distance(float[] vec1, float[] vec2) {
        float dist = 0;
        for (int i = 0; i < vec1.length; i++) {
            dist += vec1[i] * vec2[i];
        }
        return dist;
    }

    public Set<Word2Vec.WordEntry> distance(String text) {
        return model.distance(segment(text));
    }
}
