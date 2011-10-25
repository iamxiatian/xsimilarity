package ruc.irm.similarity.sentence.morphology;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ruc.irm.similarity.sentence.SegmentProxy;
import ruc.irm.similarity.sentence.SegmentProxy.Word;
import ruc.irm.similarity.sentence.SentenceSimilarity;
import ruc.irm.similarity.word.WordSimilarity;
import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;

/**
 * 基于词形和词序的句子相似度计算算法，考虑了语义因素<BR>
 * 有关算法的详细信息，请参考图书《中文信息相似度计算理论与方法》5.4.3小节.
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class MorphoSimilarity implements SentenceSimilarity {
    private static Log LOG = LogFactory.getLog(MorphoSimilarity.class);
    
    /** 词形相似度占总相似度的比重 */
    private final double LAMBDA1 = 1.0;
    /** 次序相似度占总相似度的比重 */
    private final double LAMBDA2 = 0.0;   
    /** 词语相似度的计算 */
    private WordSimilarity wordSimilarity = null;
    
    private static String FILTER_CHARS = " 　，。；？《》()｜！,.;?<>|_^…!";
    
    private static MorphoSimilarity instance = null;
    
    public static MorphoSimilarity getInstance(){
    	if(instance == null){
    		instance = new MorphoSimilarity();
    	}
    	return instance;
    }
    
    private MorphoSimilarity(){
    	LOG.debug("used hownet wordsimilarity.");
    	this.wordSimilarity = XiaConceptParser.getInstance();
    	//this.segmenter = SegmentFactory.getInstance().getParser();
    }
    
    /**
     * 滤掉词串中的空格、标点符号
     * @param word_list
     * @return
     */
    private String[] filter(String[] word_list){
    	List<String> results = new ArrayList<String>();
    	for(String w:word_list){
    		if(!FILTER_CHARS.contains(w)){
    			results.add(w.toLowerCase());
    		}
    	}
    	
    	return results.toArray(new String[results.size()]);
    }
    
    /**
     * 计算两个句子的相似度
     * @see ruc.irm.similarity.Similaritable
     */
    public double getSimilarity(String firstSen,String secondSen){
    	//LOG.debug(segmenter.segmentToString(firstSen));
    	//LOG.debug(segmenter.segmentToString(secondSen));
        String[] firstList = filter(segment(firstSen));
        String[] secondList = filter(segment(secondSen));
        
        double wordSim = getOccurrenceSimilarity(firstList,secondList);
        //LOG.debug("词形相似度="+wordSim);
        
        double orderSim = getOrderSimilarity(firstList,secondList);
        //LOG.debug("词序相似度="+orderSim);
        
        return LAMBDA1*wordSim+LAMBDA2*orderSim;
    }
       
    /**
     * 获取两个集合的词形相似度, 同时获取相对于第一个句子中的词语顺序，第二个句子词语的顺序变化次数
     * @param firstList
     * @param secondList
     * @return
     */
    public double getOccurrenceSimilarity(String[] firstList, String[] secondList){    	
    	int max = firstList.length>secondList.length?firstList.length:secondList.length;
    	if(max==0){
    		return 0;
    	}
    	
    	//首先计算出所有可能的组合
    	double[][] scores = new double[max][max];
    	for(int i=0; i<firstList.length; i++){
    		for(int j=0; j<secondList.length; j++){
    			scores[i][j] = wordSimilarity.getSimilarity(firstList[i], secondList[j]);
    		}
    	}

    	double total_score = 0;
    	
    	//从scores[][]中挑选出最大的一个相似度，然后减去该元素，进一步求剩余元素中的最大相似度    	    	
    	while(scores.length > 0){
    		double max_score = 0;
    		int max_row = 0;
    		int max_col = 0;
    		
    		//先挑出相似度最大的一对：<row, column, max_score> 
    		for(int i=0; i<scores.length; i++){
    			for(int j=0; j<scores.length; j++){
    				if(max_score<scores[i][j]){
    					max_row = i;
    					max_col = j;
    					max_score = scores[i][j];
    				}
    			}
    		}
    		
    		//从数组中去除最大的相似度，继续挑选
        	double[][] tmp_scores = new double[scores.length-1][scores.length-1];
    		for(int i=0; i<scores.length; i++){
    			if(i == max_row) continue;
    			for(int j=0; j<scores.length; j++){
    				if(j == max_col) continue;
    				int tmp_i = max_row>i?i:i-1;
    				int tmp_j = max_col>j?j:j-1;
    				tmp_scores[tmp_i][tmp_j] = scores[i][j];
    			}
    		}
    		total_score += max_score;
    		scores = tmp_scores;    		
    	}
    	
    	return (2*total_score) / (firstList.length + secondList.length);
    }
    
    /**
     * 获取两个集合的词序相似度
     * @param firstList
     * @param secondList
     * @return
     */
    public double getOrderSimilarity(String[] firstList, String[] secondList){
    	double similarity = 0.0;
    	
    	return similarity;
    }    
    
//    @SuppressWarnings("unchecked")
//	public String[] segment(String sentence){
//    	MPWordSegment ws = new MPWordSegment();
//    	ws.parseReader(new StringReader(sentence));    	
//    	Vector tokens = ws.getTokens();
//    	String[] results = new String[tokens.size()];
//    	for(int i=0; i<tokens.size(); i++){
//    		Token token = (Token)tokens.get(i);
//    		results[i] = token.termText();    		
//    	}
//    	
//    	return results;
//    }
    
    public String[] segment(String sentence){
    	List<Word> list = SegmentProxy.segment(sentence);
    	String[] results = new String[list.size()];
    	for(int i=0; i<list.size(); i++){
    		results[i] = list.get(i).getWord();
    	}
    	return results;
    }
    
}
