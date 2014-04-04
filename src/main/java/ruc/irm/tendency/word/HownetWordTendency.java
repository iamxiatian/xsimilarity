package ruc.irm.tendency.word;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ruc.irm.similarity.word.hownet2.concept.BaseConceptParser;
import ruc.irm.similarity.word.hownet2.concept.Concept;
import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;
import ruc.irm.similarity.word.hownet2.sememe.BaseSememeParser;
import ruc.irm.similarity.word.hownet2.sememe.XiaSememeParser;

/**
 * 基于知网实现的词语倾向性判别
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class HownetWordTendency implements WordTendency {
    public static String[] POSITIVE_SEMEMES = new String[]{
        "良",
        "喜悦",
        "夸奖",
        "满意",
        "期望",
        "注意",
        "致敬",
        "喜欢",
        "专",
        "敬佩",
        "同意",
        "爱惜",
        "愿意",
        "思念",
        "拥护",
        "祝贺",
        "福",
        "需求",
        "奖励",
        "致谢",
        "欢迎",
        "羡慕",
        "感激",
        "爱恋"
    };
    
    public static String[] NEGATIVE_SEMEMES = new String[]{
        "莠",
        "谴责",
        "害怕",
        "生气",
        "悲哀",
        "着急",
        "轻视",
        "羞愧",
        "烦恼",
        "灰心",
        "犹豫",
        "为难",
        "懊悔",
        "厌恶",
        "怀疑",
        "怜悯",
        "忧愁",
        "示怒",
        "不满",
        "仇恨",
        "埋怨",
        "失望",
        "坏"
    };
    private BaseConceptParser conceptParser = null;
    private BaseSememeParser sememeParser = null;
    
    public HownetWordTendency(){
        this.conceptParser =XiaConceptParser.getInstance();
        try {
            this.sememeParser = new XiaSememeParser();
        } catch (IOException e) {            
            e.printStackTrace();
        }
    }
    
    @Override
    public double getTendency(String word) {
        double positive = getSentiment(word, POSITIVE_SEMEMES);
        double negative = getSentiment(word, NEGATIVE_SEMEMES);;
        return positive - negative;
    }
    
    public double getSentiment(String word, String[] candidateSememes) {
        Collection<Concept> concepts = conceptParser.getConcepts(word);
        Set<String> sememes = new HashSet<String>();
        for (Concept c : concepts) {
            sememes.addAll(c.getAllSememeNames());
        }

        double max = 0.0;
        for(String item:sememes){
            double total = 0.0;
            for(String positiveSememe:candidateSememes){
                //如果有特别接近的义原，直接返回该相似值，避免其他干扰
                double value = sememeParser.getSimilarity(item, positiveSememe);
                if(value>0.9){
                    return value;
                }
                total += value;
            }
            double sim = total / candidateSememes.length;
            if(sim>max){
                max = sim;
            }
        }
        return max;
    }    
    
}
