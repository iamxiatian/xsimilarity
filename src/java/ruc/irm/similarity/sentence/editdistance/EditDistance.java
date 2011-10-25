package ruc.irm.similarity.sentence.editdistance;

import ruc.irm.similarity.Similaritable;


/**
 * 编辑距离的父类，定义了其中的主要行为
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public abstract class EditDistance implements Similaritable {
        
    public abstract double getEditDistance(SuperString<? extends EditUnit> S, SuperString<? extends EditUnit> T);    
 
    public double getSimilarity(String s1, String s2){
    	SuperString<WordEditUnit> S = SuperString.createWordSuperString(s1);
    	SuperString<WordEditUnit> T = SuperString.createWordSuperString(s2);
    	
    	return 1-(getEditDistance(S, T))/(Math.max(S.length(), T.length()));
    }
}
