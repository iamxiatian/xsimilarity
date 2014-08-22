package ruc.irm.similarity.sentence.morphology;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ruc.irm.similarity.sentence.SegmentProxy;
import ruc.irm.similarity.sentence.SegmentProxy.Word;
import ruc.irm.similarity.sentence.SentenceSimilarity;
import ruc.irm.similarity.word.WordSimilarity;
import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;

/**
 * 基于语义的词形和词序句子相似度计算
 *
 * 《中文信息相似度计算理论与方法》5.4.3小节所介绍的基于词形和词序的句子相似度计算算法
 * 在考虑语义时，无法直接获取OnceWS(A, B)，为此，通过记录两个句子的词语匹配对中相似度
 * 大于某一阈值的词语对最为相同词语，计算次序相似度。
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * 
 */
public class SemanticSimilarity implements SentenceSimilarity {
    private static Logger LOG = LoggerFactory.getLogger(SemanticSimilarity.class);
    
    /** 词形相似度占总相似度的比重 */
    private final double LAMBDA1 = 0.8;
    /** 词序相似度占总相似度的比重 */
    private final double LAMBDA2 = 0.2;   
    
    /** 如果两个词语的相似度大于了该阈值， 则作为相同词语，计算词序相似度 */
    private final double GAMMA = 0.6;
    
    /** 词语相似度的计算 */
    private WordSimilarity wordSimilarity = null;
    
    private static String FILTER_CHARS = " 　，。；？《》()｜！,.;?<>|_^…!";
    
    private static SemanticSimilarity instance = null;
    
    public static SemanticSimilarity getInstance(){
    	if(instance == null){
    		instance = new SemanticSimilarity();
    	}
    	return instance;
    }
    
    private SemanticSimilarity(){
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
        
        return calculate(firstList,secondList);
    }
       
    /**
     * 获取两个集合的词形相似度, 同时获取相对于第一个句子中的词语顺序，第二个句子词语的顺序变化次数
     * @param firstList
     * @param secondList
     * @return
     */
    public double calculate(String[] firstList, String[] secondList){    	
    	if(firstList.length == 0 || secondList.length == 0){
    		return 0;
    	}
    	
    	//首先计算出所有可能的组合
    	double[][] scores = new double[firstList.length][secondList.length];
    	
    	//代表第1个句子对应位置是否已经被使用, 默认为未使用，即false
    	boolean[] firstFlags = new boolean[firstList.length];
    	
    	//代表第2个句子对应位置是否已经被使用, 默认为未使用，即false
        boolean[] secondFlags = new boolean[secondList.length];
        
        //PSecond的定义参见书中5.4.3节， 为避免无必要的初始化数组，
        //数组中0值表示在第一个句子中没有对应的相似词语，大于0的值
        //则表示在第一个句子中的位置（从1开始编号了）
        int[] PSecond = new int[secondList.length];
        
    	for(int i=0; i<firstList.length; i++){
    	    //firstFlags[i] = false;
    		for(int j=0; j<secondList.length; j++){
    			scores[i][j] = wordSimilarity.getSimilarity(firstList[i], secondList[j]);
    		}
    	}

    	double total_score = 0;
    	
    	//从scores[][]中挑选出最大的一个相似度，然后减去该元素(通过Flags数组表示)，进一步求剩余元素中的最大相似度    	    	
    	while(true){
    		double max_score = 0;
    		int max_row = -1;
    		int max_col = -1;
    		
    		//先挑出相似度最大的一对：<row, column, max_score> 
    		for(int i=0; i<scores.length; i++){
    		    if(firstFlags[i]) continue;
    			for(int j=0; j<scores[i].length; j++){
    			    if(secondFlags[j]) continue;
    			    
    				if(max_score<scores[i][j]){
    					max_row = i;
    					max_col = j;
    					max_score = scores[i][j];
    				}
    			}
    		}
    		
    		if(max_row>=0) {
    		    total_score += max_score;
    		    firstFlags[max_row] = true;
    		    secondFlags[max_col] = true;
    		    if(max_score>=GAMMA) {
    		        PSecond[max_col] = max_row+1;
    		    }
    		} else {
    		    break;
    		}
    	}
    	
    	double wordSim = (2*total_score) / (firstList.length + secondList.length);
    	
    	int previous = 0;
    	int revOrdCount = 0;
    	int onceWSSize = 0;
    	for(int i=0; i<PSecond.length; i++) {
    	    if(PSecond[i]>0) {
    	        onceWSSize++;
    	        if(previous>0 && (previous>PSecond[i])) {
    	            revOrdCount++;
    	        } 
    	        previous = PSecond[i];
    	    }
    	}
    	
    	double ordSim = 0;
    	if(onceWSSize==1) {
    	    ordSim = 1;
    	} else if(onceWSSize == 0) {
    	    ordSim = 0;
    	} else {
    	    ordSim = 1.0 - revOrdCount*1.0/(onceWSSize-1);
    	}
    	
    	System.out.println("wordSim ==> " + wordSim + ", ordSim ==> " + ordSim);
    	
    	return LAMBDA1*wordSim+LAMBDA2*ordSim;
    }
    
    public String[] segment(String sentence){
    	List<Word> list = SegmentProxy.segment(sentence);
    	String[] results = new String[list.size()];
    	for(int i=0; i<list.size(); i++){
    		results[i] = list.get(i).getWord();
    	}
    	return results;
    }
    
}
