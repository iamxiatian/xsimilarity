package ruc.irm.similarity.util;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Load word2vec model(trained by c implementation version), and do analysis in
 * Java.
 *
 * @date Feb 05, 2015 11:45 PM
 * @see <a href="http://blog.csdn.net/zhoubl668/article/details/24314769">
 * http://blog.csdn.net/zhoubl668/article/details/24314769</a>
 */
public class Word2Vec {
    private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

    private int words;
    private int size;
    private int topNSize = 40;

    /**
     * Load trained model
     */
    public void loadModel(String path) throws IOException {
        DataInputStream dis = null;
        BufferedInputStream bis = null;
        double len = 0;
        float vector = 0;
        try {
            System.out.print("Loading " + path + "...");
            bis = new BufferedInputStream(new FileInputStream(path));
            dis = new DataInputStream(bis);
            //读取词数
            words = Integer.parseInt(readString(dis));
            //大小
            size = Integer.parseInt(readString(dis));

            String word;
            float[] vectors = null;
            for (int i = 0; i < words; i++) {
                word = readString(dis);
                vectors = new float[size];
                len = 0;
                for (int j = 0; j < size; j++) {
                    vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = (float) vector;
                }
                len = Math.sqrt(len);

                for (int j = 0; j < vectors.length; j++) {
                    vectors[j] = (float) (vectors[j] / len);
                }
                wordMap.put(word, vectors);
                dis.read();
            }
            System.out.println("Done.");
        } finally {
            bis.close();
            dis.close();
        }
    }

    private static final int MAX_SIZE = 50;


    public Set<WordEntry> distance(String words) {
        List<String> list = Arrays.asList(words.split(" "));
        return distance(list);
    }

    public float similarity(float[] vector1, float[] vector2) {
        float cosine = 0.0f;
        for (int i = 0; i < vector1.length; i++) {
            cosine += vector1[i] * vector2[i];
        }

        return cosine;
    }

    /**
     * 根据输入的一组词语，得到近义词
     *
     * @param words
     * @return
     */
    public Set<WordEntry> distance(List<String> words) {
        float[] wordVector = getWordVector(words);
        if (wordVector == null) {
            return null;
        }
        Set<Entry<String, float[]>> entrySet = wordMap.entrySet();
        float[] tempVector = null;
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
        String name = null;
        for (Entry<String, float[]> entry : entrySet) {
            name = entry.getKey();
            if (words.contains(name)) {
                continue;
            }
            float dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<WordEntry>(wordEntrys);
    }

    /**
     * 近义词
     *
     * @return
     */
    public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
        float[] wv0 = getWordVector(word0);
        float[] wv1 = getWordVector(word1);
        float[] wv2 = getWordVector(word2);

        if (wv1 == null || wv2 == null || wv0 == null) {
            return null;
        }
        float[] wordVector = new float[size];
        for (int i = 0; i < size; i++) {
            wordVector[i] = wv1[i] - wv0[i] + wv2[i];
        }
        float[] tempVector;
        String name;
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
        for (Entry<String, float[]> entry : wordMap.entrySet()) {
            name = entry.getKey();
            if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
                continue;
            }
            float dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++) {
                dist += wordVector[i] * tempVector[i];
            }
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<WordEntry>(wordEntrys);
    }

    private void insertTopN(String name, float score, final List<WordEntry> wordsEntrys) {
        if (wordsEntrys.size() < topNSize) {
            wordsEntrys.add(new WordEntry(name, score));
            return;
        }
        float min = Float.MAX_VALUE;
        int minOffe = 0;
        for (int i = 0; i < topNSize; i++) {
            WordEntry wordEntry = wordsEntrys.get(i);
            if (min > wordEntry.score) {
                min = wordEntry.score;
                minOffe = i;
            }
        }

        if (score > min) {
            wordsEntrys.set(minOffe, new WordEntry(name, score));
        }

    }

    public static class WordEntry implements Comparable<WordEntry> {
        public String name;
        public float score;

        public WordEntry(String name, float score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return this.name + "\t" + score;
        }

        @Override
        public int compareTo(WordEntry o) {
            if (this.score > o.score) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    public float[] getWordVector(String word) {
        return wordMap.get(word);
    }

    public float[] vectorPlus(float[] v1, float[] v2, boolean newSpaceCopy) {
        float[] vector = newSpaceCopy ? v1.clone() : v1;

        double len = 0.0f;
        for (int i = 0; i < vector.length; i++) {
            vector[i] += v2[i];
            len += vector[i] * vector[i];
        }

//        len = Math.sqrt(len);
//        for (int i = 0; i < vector.length; i++) {
//            vector[i] = (float)(vector[i]/len);
//        }

        return vector;
    }

    public float[] normalize(float[] vector) {
        double len = 0.0f;
        for (int i = 0; i < vector.length; i++) {
            len += vector[i] * vector[i];
        }

        len = Math.sqrt(len);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / len);
        }
        return vector;
    }

    /**
     * 得到一组词语的平均词向量
     *
     * @param words
     * @return
     */
    public float[] getWordVector(List<String> words) {
        if (words.size() == 1) {
            return getWordVector(words.get(0));
        }

        //复制拷贝，防止更改模型内部的数值, @TODO 考虑该数组复用，不用每次生成空间
        float[] vector = new float[size];
        int count = 0;
        for (String w : words) {
            if (!wordMap.containsKey(w)) continue;

            float[] tmpVector = wordMap.get(w);
            for (int i = 0; i < vector.length; i++) {
                vector[i] += tmpVector[i];
            }
            count++;
        }

        if (count == 0) {
            return null;
        } else if (count > 1) {
            //Normalize
            double len = 0.0f;
            for (int i = 0; i < vector.length; i++) {
                len += vector[i] * vector[i];
            }
            len = Math.sqrt(len);
            for (int i = 0; i < vector.length; i++) {
                vector[i] = (float) (vector[i] / len);
            }
        }

        return vector;
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    /**
     * 读取一个float
     */
    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 读取一个字符串
     */
    private static String readString(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

    public int getTopNSize() {
        return topNSize;
    }

    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public HashMap<String, float[]> getWordMap() {
        return wordMap;
    }

    public int getWords() {
        return words;
    }

    public int getSize() {
        return size;
    }
}