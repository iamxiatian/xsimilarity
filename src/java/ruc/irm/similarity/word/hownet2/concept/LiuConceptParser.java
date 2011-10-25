package ruc.irm.similarity.word.hownet2.concept;

import java.io.IOException;
import java.util.Collection;

import ruc.irm.similarity.util.BlankUtils;
import ruc.irm.similarity.word.hownet2.sememe.LiuqunSememeParser;
import ruc.irm.similarity.word.hownet2.sememe.BaseSememeParser;


/**
 * 刘群老师的相似度计算方式，对概念解析的处理方式
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class LiuConceptParser extends BaseConceptParser{
	
	private static LiuConceptParser instance = null;
	
	public static LiuConceptParser getInstance(){
		if(instance == null){
			try {
				instance = new LiuConceptParser();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return instance;
	}
	
	private LiuConceptParser(BaseSememeParser sememeParser) throws IOException {
		super(sememeParser);
	}
	
	private LiuConceptParser() throws IOException{
		super(new LiuqunSememeParser());
	}

	@Override
	protected double calculate(double sim_v1, double sim_v2, double sim_v3, double sim_v4){
		return beta1 * sim_v1 
        + beta2 * sim_v1 * sim_v2
        + beta3 * sim_v1 * sim_v2 * sim_v3 
        + beta4 * sim_v1 * sim_v2 * sim_v3 * sim_v4;		
	}

	@Override
	public double getSimilarity(String word1, String word2) {
		double similarity = 0.0;

		// 如果两个句子相同,则直接返回1.0
		if (word1.equals(word2)) {
			return 1.0;
		}

		Collection<Concept> concepts1 = getConcepts(word1);
		Collection<Concept> concepts2 = getConcepts(word2);
		
		//如果是blank，则说明是未登录词, 需要计算组合概念
		if(BlankUtils.isBlank(concepts1)  || BlankUtils.isBlank(concepts2)){
			return 0.0;
		}
		
		//两个for循环分别计算词语所有可能的概念的相似度
		for(Concept c1:concepts1){
			for(Concept c2:concepts2){				
				double v = getSimilarity(c1, c2);

				if(v>similarity){
					similarity = v;
				}
				
				if(similarity == 1.0){
					break;
				}
			}
		}		

		return similarity;
	}
	
}
