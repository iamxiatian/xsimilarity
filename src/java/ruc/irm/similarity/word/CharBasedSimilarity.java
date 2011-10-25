package ruc.irm.similarity.word;

import java.util.ArrayList;
import java.util.List;

import ruc.irm.similarity.Similaritable;


/**
 * 字面相似度计算方法
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class CharBasedSimilarity implements Similaritable {

	private double alpha = 0.6;
	private double beta = 0.4;
	
	@Override
	public double getSimilarity(String word1, String word2) {
		if(isBlank(word1)&& isBlank(word2)){
			return 1.0;
		}
		if(isBlank(word1)|| isBlank(word2)){
			return 0.0;
		}
		
		List<Character> sameHZ = new ArrayList<Character>();
		
		String longString = word1.length()>=word2.length()?word1:word2;
		String shortString = word1.length()<word2.length()?word1:word2;
		for(int i=0; i<longString.length(); i++){
			Character ch = longString.charAt(i);
			if(shortString.contains(ch.toString())){
				sameHZ.add(ch);				
			}
		}
		
		double dp = Math.min(1.0*word1.length()/word2.length(), 1.0*word2.length()/word1.length());
		double part1 = alpha*(1.0*sameHZ.size()/word1.length() + 1.0*sameHZ.size()/word2.length())/2.0;				
		double part2 = beta*dp*(getWeightedResult(word1, sameHZ) + getWeightedResult(word2, sameHZ))/2.0;

		return part1+part2;
	}

	private double getWeightedResult(String word1, List<Character> sameHZ){
		double top = 0;
		double bottom = 0;
		for(int i=0; i<word1.length(); i++){
			if(sameHZ.contains(word1.charAt(i))){
				top+=(i+1);
			}
			bottom += (i+1);
		}
		return 1.0*top/bottom;
	}
	
	private boolean isBlank(String str){
		return str == null || str.trim().equals("");
	}
	
}
