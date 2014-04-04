package ruc.irm.similarity.word.pinyin;

import java.util.Set;

import ruc.irm.similarity.Similaritable;
import ruc.irm.similarity.util.EditDistance;
import ruc.irm.similarity.util.PinyinUtils;


/**
 * 通过拼音计算两个词语是否相似，拼音的相似程度采用编辑距离算法，并进行归一化衡量
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class PinyinSimilarity implements Similaritable {
	
	public double getSimilarity(String item1, String item2) {
		Set<String> pinyinSet1 = PinyinUtils.getInstance().getPinyin(item1);
		Set<String> pinyinSet2 = PinyinUtils.getInstance().getPinyin(item2);

		double max = 0.0;
		for(String pinyin1:pinyinSet1){
			for(String pinyin2:pinyinSet2){
				double distance = new EditDistance().getEditDistance(pinyin1, pinyin2);		
				double similarity = 1 - distance/( (pinyin1.length()>pinyin2.length())?pinyin1.length():pinyin2.length());
				max = (max>similarity)?max:similarity;
				if(max==1.0){
					return max;
				}
			}
		}
		return max;
	}
	
}
