package ruc.irm.similarity.word.hownet2.concept;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ruc.irm.similarity.util.BlankUtils;
import ruc.irm.similarity.word.hownet2.sememe.BaseSememeParser;
import ruc.irm.similarity.word.hownet2.sememe.XiaSememeParser;

/**
 * 概念解析器的实现,用于获取概念、计算概念的相似度等, 与原论文比较，加入了剪枝处理，当组合过多的时候，就自动停止后面的组合情况， 保证运行速度
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class XiaConceptParser extends BaseConceptParser {
    private static final int MAX_COMBINED_COUNT = 12;

    private static XiaConceptParser instance = null;

    public static XiaConceptParser getInstance() {
        if (instance == null) {
            try {
                instance = new XiaConceptParser();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    private XiaConceptParser() throws IOException {
        super(new XiaSememeParser());
    }

    public XiaConceptParser(BaseSememeParser sememeParser) throws IOException {
        super(sememeParser);
    }

    @Override
    protected double calculate(double sim_v1, double sim_v2, double sim_v3, double sim_v4) {
        return beta1 * sim_v1 + beta2 * sim_v1 * sim_v2 + beta3 * sim_v1 * sim_v3 + beta4 * sim_v1 * sim_v4;
    }

    @Override
    public Collection<Concept> getConcepts(String key) {
        Collection<Concept> concepts = super.getConcepts(key);
        if (BlankUtils.isBlank(concepts)) {
            concepts = autoCombineConcepts(key, null);
        }
        return concepts;
    }

    /**
     * 获取知网本身自带的概念，不组合处理
     * @param key
     * @return
     */
    public Collection<Concept> getInnerConcepts(String key) {
        return super.getConcepts(key);
    }

    /**
     * 获取两个词语的相似度，如果一个词语对应多个概念，则返回相似度最大的一对
     * 
     * @param word1
     * @param word2
     * @return
     */
    @Override
    public double getSimilarity(String word1, String word2) {
        double similarity = 0.0;

        // 如果两个词语相同,则直接返回1.0
        if (word1.equals(word2)) {
            return 1.0;
        }

        Collection<Concept> concepts1 = super.getConcepts(word1);
        Collection<Concept> concepts2 = super.getConcepts(word2);

        // 如果是blank，则说明是未登录词, 需要计算组合概念
        if (BlankUtils.isBlank(concepts1) && !BlankUtils.isBlank(concepts2)) {
            concepts1 = autoCombineConcepts(word1, concepts2);
        } else if (BlankUtils.isBlank(concepts2) && !BlankUtils.isBlank(concepts1)) {
            concepts2 = autoCombineConcepts(word2, concepts1);
        } else if (BlankUtils.isBlank(concepts1) && BlankUtils.isBlank(concepts2)) {
            concepts1 = autoCombineConcepts(word1, concepts2);
            concepts2 = autoCombineConcepts(word2, concepts1);
            // 相互修正
            concepts1 = autoCombineConcepts(word1, concepts2);
            concepts2 = autoCombineConcepts(word2, concepts1);
        }

        // 两个for循环分别计算词语所有可能的概念的相似度
        for (Concept c1 : concepts1) {
            for (Concept c2 : concepts2) {
                double v = getSimilarity(c1, c2);

                if (v > similarity) {
                    similarity = v;
                }

                if (similarity == 1.0) {
                    break;
                }
            }
        }

        return similarity;
    }

    /**
     * 把未登录词进行概念切分, 形成多个概念的线性链表，并倒排组织, 如“娱乐场”切分完毕后存放成: 【场】 → 【娱乐】
     * 
     * @param oov_word
     *            未登录词
     * @return
     */
    private List<String> segmentOOV(String oov_word, int topN) {
        List<String> results = new LinkedList<String>();

        String word = oov_word;
        int count = 0;
        while (word != null && !word.equals("")) {
            String token = word;
            while (token.length() > 1 && BlankUtils.isBlank(super.getConcepts(token))) {
                token = token.substring(1);
            }
            results.add(token);
            count++;
            if(count>=topN) break;

            word = word.substring(0, (word.length() - token.length()));
        }

        return results;
    }

    /**
     * 计算未登录词语oov_word自动组合语义
     * 
     * @param oov_word
     *            未登录词，此处指知网概念中未出现的词语，需要进行切分,求解组合语义，
     *            组合的语义关系通过参照概念refConcepts进行修正
     * @param refConcepts
     *            简单计算出的oov_word的概念定义，需要通过refConcepts修正义原之间的符号、关系等
     * @return
     */
    public Collection<Concept> autoCombineConcepts(String oov_word, Collection<Concept> refConcepts) {
        ConceptLinkedList oovConcepts = new ConceptLinkedList();

        if (oov_word == null) {
            return oovConcepts;
        }

        //只获取倒排后的三个未识别词语，如果太多了，一方面会影响运行速度，另一方面组合过多的意义也不是很有用
        for (String concept_word : segmentOOV(oov_word, 3)) {
            Collection<Concept> concepts = super.getConcepts(concept_word);
            if (oovConcepts.size() == 0) {
                oovConcepts.addAll(concepts);
                continue;
            }

            ConceptLinkedList tmpConcepts = new ConceptLinkedList();
            for (Concept head : concepts) {
                for (Concept tail : oovConcepts) {
                    if (!BlankUtils.isBlank(refConcepts)) {
                        for (Concept ref : refConcepts) {
                            tmpConcepts.addByDefine(autoCombineConcept(head, tail, ref));
                        }
                    } else {
                        tmpConcepts.addByDefine(autoCombineConcept(head, tail, null));
                    }
                }
            }
            oovConcepts = tmpConcepts;
        }

        /** 如果组合过多，则删除最后的1/3个组合 */
        if ((oovConcepts.size() > MAX_COMBINED_COUNT)) {
            oovConcepts.removeLast(MAX_COMBINED_COUNT / 3);
        }

        return oovConcepts;
    }

    /**
     * 计算两个概念的组合概念, 计算过程中根据参照概念修正组合结果, 实际应用中的两个概念 应具有一定的先后关系(体现汉语“重心后移”特点),
     * 如对于娱乐场，first="娱乐" second="场", 另外，
     * 还需要修正第一个概念中的符号义原对于第二个概念主义原的实际关系，当参照概念起作用时，
     * 即大于指定的阈值，则需要判断是否把当前义原并入组合概念中，对于第一个概念，还需要同时修正符号关系, 符合关系与参照概念保持一致.
     * 
     * @param head
     *            第一个概念
     * @param tail
     *            第二个概念
     * @param ref
     *            参照概念
     * @return
     */
    public Concept autoCombineConcept(Concept head, Concept tail, Concept ref) {
        // 一个为null，一个非null，直接返回非null的克隆新概念
        if (tail == null && head != null) {
            return new Concept(head.getWord(), head.getPos(), head.getDefine());
        } else if (head == null && tail != null) {
            return new Concept(tail.getWord(), tail.getPos(), tail.getDefine());
        }

        // 第二个概念不是实词，直接返回第一个概念
        if (!tail.isSubstantive()) {
            return new Concept(head.getWord() + tail.getWord(), head.getPos(), head.getDefine());
        }

        // 如果没有参照概念、或者参照概念为虚词，则直接相加，即参照概念不再起作用
        if (ref == null || !ref.isSubstantive()) {
            String define = tail.getDefine(); // define存放新的定义结果

            // 把第一个概念的定义合并到第二个上
            List<String> sememeList = getAllSememes(head, true);
            for (String sememe : sememeList) {
                if (!define.contains(sememe)) {
                    define = define + "," + sememe;
                }
            }
            return new Concept(head.getWord() + tail.getWord(), tail.getPos(), define);
        }

        // 正常处理：参照概念非空，并且是实词概念
        String define = tail.getMainSememe(); // define存放新的定义结果

        List<String> refSememes = getAllSememes(ref, false);
        List<String> headSememes = getAllSememes(head, true);
        List<String> tailSememes = getAllSememes(tail, false);

        // 如果参照概念与第二个概念的主义原的义原相似度大于阈值THETA，
        // 则限制组合概念定义中与第二个概念相关的义原部分为: 第二个概念的义原集合与参照概念义原集合的模糊交集
        double main_similarity = sememeParser.getSimilarity(tail.getMainSememe(), ref.getMainSememe());
        if (main_similarity >= PARAM_THETA) {
            // 求交集
            for (String tail_sememe : tailSememes) {
                double max_similarity = 0.0;
                String max_ref_sememe = null;
                for (String ref_sememe : refSememes) {
                    double value = sememeParser.getSimilarity(tail_sememe, ref_sememe);
                    if (value > max_similarity) {
                        max_similarity = value;
                        max_ref_sememe = ref_sememe;
                    }
                }

                // 如果tail_sememe与参照概念中的相似度最大的义原经theta约束后超过阈值XI，则加入生成的组合概念定义中
                if (max_similarity * main_similarity >= PARAM_XI) {
                    define = define + "," + tail_sememe;
                    refSememes.remove(max_ref_sememe);
                }
            }// end for
        } else {
            define = tail.getDefine();
        }// end if

        // 合并第一个概念的义原到组合概念定义中
        for (String head_sememe : headSememes) {
            double max_similarity = 0.0;
            String max_ref_sememe = "";
            for (String ref_sememe : refSememes) {
                double value = sememeParser.getSimilarity(getPureSememe(head_sememe), getPureSememe(ref_sememe));
                if (value > max_similarity) {
                    max_similarity = value;
                    max_ref_sememe = ref_sememe;
                }
            }

            if (main_similarity * max_similarity >= PARAM_OMEGA) {
                // 调整符号关系, 用参照概念的符号关系替换原符号关系, 通过把参照概念的非符号部分替换成前面义原的非符号内容即可
                String sememe = max_ref_sememe.replace(getPureSememe(max_ref_sememe), getPureSememe(head_sememe));
                if (!define.contains(sememe)) {
                    define = define + "," + sememe;
                }
            } else if (!define.contains(head_sememe)) {
                define = define + "," + head_sememe;
            }
        }// end for

        return new Concept(head.getWord() + tail.getWord(), tail.getPos(), define);
    }

    /**
     * 获取概念的所有义原
     * 
     * @param concept
     * @param includeMainSememe
     *            是否包含主义原
     * @return
     */
    private List<String> getAllSememes(Concept concept, boolean includeMainSememe) {
        List<String> results = new ArrayList<String>();
        if (concept != null) {
            if (includeMainSememe) {
                results.add(concept.getMainSememe());
            }

            for (String sememe : concept.getSecondSememes()) {
                results.add(sememe);
            }

            for (String sememe : concept.getSymbolSememes()) {
                results.add(sememe);
            }

            for (String sememe : concept.getRelationSememes()) {
                results.add(sememe);
            }
        }
        return results;
    }

    /**
     * 去掉义原的关系或者符号
     * 
     * @param sememe
     *            原始义原
     * @return 去掉义原的关系或者符号的数值
     */
    private String getPureSememe(String sememe) {
        String line = sememe.trim();

        if ((line.charAt(0) == '(') && (line.charAt(line.length() - 1) == ')')) {
            line = line.substring(1, line.length() - 1);
        }

        // 先判断是否为符号义元
        String symbol = line.substring(0, 1);
        for (int i = 0; i < Symbol_Descriptions.length; i++) {
            if (symbol.equals(Symbol_Descriptions[i][0])) {
                return line.substring(1);
            }
        }

        // 如果不是符号义元，则进一步判断是关系义元还是第二基本义元
        int pos = line.indexOf('=');
        if (pos > 0) {
            line = line.substring(pos + 1);
        }
        return line;
    }

}
