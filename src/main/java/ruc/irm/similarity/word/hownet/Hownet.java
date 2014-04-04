package ruc.irm.similarity.word.hownet;

import java.io.IOException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ruc.irm.similarity.Similaritable;
import ruc.irm.similarity.word.hownet2.concept.BaseConceptParser;
import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;
import ruc.irm.similarity.word.hownet2.sememe.XiaSememeParser;
import ruc.irm.similarity.word.hownet2.sememe.BaseSememeParser;

/**
 * Hownet的主控制类, 通过知网的概念和义原及其关系计算汉语词语之间的相似度. 
 * 相似度的计算理论参考论文《汉语词语语义相似度计算研究》
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * 
 * @see ke.commons.similarity.Similariable
 */
public class Hownet implements Similaritable{	
	/** the logger */
	private static final Log LOG = LogFactory.getLog(Hownet.class);
	/** 知网的单例 */
	private static Hownet instance = null;
	
	private BaseConceptParser conceptParser = null;
	
	private Hownet(){
		try {
			BaseSememeParser sememeParser = new XiaSememeParser();
			conceptParser = new XiaConceptParser(sememeParser);
		} catch (IOException e) {			
			e.printStackTrace();
			LOG.error(e);
		}
	}
	
	/**
	 * 单例获取知网对象
	 * @return
	 */
	public static Hownet instance(){
		if(null == instance){
			instance = new Hownet();
		}
		
		return instance;
	}
	
	/**
	 * 获取概念解析器
	 * @return
	 */
	public BaseConceptParser getConceptParser(){
		return conceptParser;
	}
		
	public double getSimilarity(String item1, String item2) {		
		return conceptParser.getSimilarity(item1, item2);
	}
		
}
