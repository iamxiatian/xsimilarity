package ruc.irm.similarity.word.hownet2.concept;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ruc.irm.similarity.util.BlankUtils;
import ruc.irm.similarity.word.WordSimilarity;
import ruc.irm.similarity.word.hownet.HownetMeta;
import ruc.irm.similarity.word.hownet2.sememe.BaseSememeParser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 概念解析器: 包括概念的加载，内部组织、索引、查询以及概念的相似度计算等.
 * 概念保存到数组中, 没有保存到Map中, 以尽量降低对内存空间的使用.
 * 算法的核心思想请参看论文《汉语词语语义相似度计算研究》
 * <br/><br/>
 * improvement:
 * <ol>
 * 	<li>两个义原集合的运算方式，支持均值方式或Fuzzy方式</li>
 * </ol>
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * 
 * @see ke.commons.similarity.Similariable
 */
public abstract class BaseConceptParser implements HownetMeta, WordSimilarity{
	/** the logger */
	protected Log LOG = LogFactory.getLog(this.getClass());

	/** 所有概念存放的对象 */
	private static Multimap<String, Concept> CONCEPTS = null;

	protected BaseSememeParser sememeParser = null;

	/** 集合运算类型，目前支持均值运算和模糊集运算两种形式 */
	public enum SET_OPERATE_TYPE {AVERAGE, FUZZY};
	
	/** 默认的集合运算类型为均值法 */
	private SET_OPERATE_TYPE currentSetOperateType = SET_OPERATE_TYPE.AVERAGE;
	
	public BaseConceptParser(BaseSememeParser sememeParser) throws IOException{	
		this.sememeParser = sememeParser;
		synchronized (this) {
			if(CONCEPTS == null){				
				firstLoad();
			}
		}		
	}
	
	/**
	 * 加载用户自定义的概念词典文件
	 * @param xmlFile
	 * @throws IOException
	 */
	public static void load(File xmlFile) throws IOException{
		if(CONCEPTS == null){				
			firstLoad();
		}
		load(new FileInputStream(xmlFile));
	}
	
	private static void firstLoad() throws IOException{
		CONCEPTS = HashMultimap.create();
		String conceptFile = BaseConceptParser.class.getPackage().getName().replaceAll("\\.", "/") + "/concept.xml.gz";
		InputStream input = BaseConceptParser.class.getClassLoader().getResourceAsStream(conceptFile);
		input = new GZIPInputStream(input);
		load(input);
	}
	
	/**
	 * 从XML格式文件输入流中加载概念知识。用户自定义的领域概念，也可以通过该方式加载到词典中
	 * 
	 * @throws IOException
	 */
	private static void load(InputStream input) throws IOException {
		System.out.print("loading concepts...");
		long time = System.currentTimeMillis();
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(input);
			int count=0;
			while (xmlEventReader.hasNext()) {
				XMLEvent event = xmlEventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().toString().equals("c")) {
						String word = startElement.getAttributeByName(QName.valueOf("w")).getValue();
						String define = startElement.getAttributeByName(QName.valueOf("d")).getValue();
						String pos = startElement.getAttributeByName(QName.valueOf("p")).getValue();
						CONCEPTS.put(word, new Concept(word, pos, define));
						count++;
						if(count%500==0){
							System.out.print(".");
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		time = System.currentTimeMillis()-time;
		System.out.println("\ncomplete!. time elapsed: " + (time/1000) + "s");		
	}

	/**
	 * 获取两个词语的相似度，如果一个词语对应多个概念，则返回相似度最大的一对
	 * 
	 * @param word1
	 * @param word2
	 * @see ke.commons.similarity.Similariable
	 * @return
	 */	 
	public abstract double getSimilarity(String word1, String word2);
	
	/**
	 * 计算四个组成部分的相似度方式，不同的算法对这四个部分的处理或者说权重分配不同
	 * @param sim_v1 主义原的相似度
	 * @param sim_v2 其他基本义原的相似度
	 * @param sim_v3 关系义原的相似度
	 * @param sim_v4 符号义原的相似度
	 * @return
	 */
	protected abstract double calculate(double sim_v1, double sim_v2, double sim_v3, double sim_v4);
	
	
	/**
	 * 判断一个词语是否是一个概念
	 * @param word
	 * @return
	 */
	public boolean isConcept(String word){
		return !BlankUtils.isBlank(CONCEPTS.get(word));
	}
	
	/**
	 * 根据名称获取对应的概念定义信息，由于一个词语可能对应多个概念，因此返回一个集合
	 * 
	 * @param key 要查找的概念名称
	 * @return
	 */
	public Collection<Concept> getConcepts(String key) {
		return CONCEPTS.get(key);
	}
	
	/**
	 * 获取两个概念之间的相似度，如果两个概念的词性都不相同，则直接返回0;
	 * 否则，计算对应义元的相似度，并加权求和
	 * 
	 * @param c1 第一个参与运算的概念
	 * @param c2 第二个参与运算的概念
	 * @return
	 */
	public double getSimilarity(Concept c1, Concept c2) {
		double similarity = 0.0;

		// 如果概念1或者概念2有一个为空，或者两个概念的词性不同，直接返回0
		if (c1 == null || c2 == null || !c1.getPos().equals(c2.getPos())) {
			return 0.0;
		}

		// 如果两个概念相同，无需进一步计算，直接返回1.0
		if (c1.equals(c2)) {
			return 1.0;
		}

		// 虚词概念和实词概念的相似度总是0
		if (c1.isSubstantive() != c2.isSubstantive()) {
			return 0.0;
		} else if (c1.isSubstantive() == false) {
			// 虚词相似度直接计算义原的相似度
			similarity = sememeParser.getSimilarity(c1.getMainSememe(), c2.getMainSememe());
		} else {
			// 实词的相似度计算
			double sim1 = sememeParser.getSimilarity(c1.getMainSememe(), c2.getMainSememe());
			double sim2 = getSimilarity(c1.getSecondSememes(), c2.getSecondSememes());
			double sim3 = getSimilarity(c1.getRelationSememes(), c2.getRelationSememes());
			double sim4 = getSimilarity(c1.getSymbolSememes(), c2.getSymbolSememes());
			similarity = calculate(sim1, sim2, sim3, sim4);			
		}

		return similarity;
	}		
	
	/**
	 * 计算两个义原集合的相似度，每一个集合都是一个概念的某一类义原集合，如第二基本义原、符号义原、关系义原等，
	 * 可以采用多种方式计算两个义原集合的相似度，如均值法、Fuzzy运算等
	 * 
	 * @param set1 义原集合1
	 * @param set2 义原集合2
	 * @return
	 */
	protected double getSimilarity(String[] sememes1, String[] sememes2) {
		if(currentSetOperateType == SET_OPERATE_TYPE.FUZZY){
			return getSimilarity_Fuzzy(sememes1, sememes2);
		}else {
			return getSimilarity_AVG(sememes1, sememes2);
		}
	}
	
	/** 设置当前的集合运算类型 */
	public void setSetOperateType(SET_OPERATE_TYPE type){
		this.currentSetOperateType = type;
	}
	/**
	 * 考虑到义原集合中的义原先后关系影响不大，因此计算两个集合的义原相似度的平均值作为这两个义原集合的相似度, 
	 * 即此处采用的是均值方法：
	 * The rank of a value assignment is the average of the weights of the constituent values.
	 * @param sememes1
	 * @param sememes2
	 * @return
	 */
	private double getSimilarity_AVG(String[] sememes1, String[] sememes2) {
		double similarity = 0.0;
		double scoreArray[][];

		if(BlankUtils.isBlank(sememes1) || BlankUtils.isBlank(sememes2)){
			if(BlankUtils.isBlank(sememes1) && BlankUtils.isBlank(sememes2)){
				return 1.0;
			}else{
				//任一个非空值与空值的相似度为一个较小的常数
				return delta;
			}
		}
		
		double score = 0.0;
		int arrayLen = sememes1.length > sememes2.length ? sememes1.length : sememes2.length;
		scoreArray = new double[arrayLen][arrayLen];
		//初始化数组
		for (int i = 0; i < arrayLen; i++) {
			for (int j = 0; j < arrayLen; j++) {
				scoreArray[i][j] = 0;
			}
		}

		// 计算两个集合两两之间的相似度，存到scoreArray数组之中
		for(int i=0; i<sememes1.length; i++){
			for(int j=0; j<sememes2.length; j++){
				scoreArray[i][j] = sememeParser.getSimilarity(sememes1[i], sememes2[j]);
			}
		}

		// 依次求出最大的相似度,并把总分数存于score之中
		score = 0.0;
		while (scoreArray.length > 0) {
			double[][] tmp;
			int row = 0;
			int column = 0;
			double max = scoreArray[row][column];

			for (int i = 0; i < scoreArray.length; i++) {
				for (int j = 0; j < scoreArray[i].length; j++) {
					if (scoreArray[i][j] > max) {
						row = i;
						column = j;
						max = scoreArray[i][j];
					}
				}
			}

			// 最大值由row和column标记出
			score += max;

			// 从该数组中去掉该行和该列, 继续计算，直到数组为0停止
			tmp = new double[scoreArray.length - 1][scoreArray.length - 1];
			for (int i = 0; i < scoreArray.length; i++) {
				if (i == row){
					continue;
				}
				for (int j = 0; j < scoreArray[i].length; j++) {
					if (j == column){
						continue;
					}
					
					int tmprow = i;
					int tmpcol = j;
					if (i > row)
						tmprow--;
					if (j > column)
						tmpcol--;
					tmp[tmprow][tmpcol] = scoreArray[i][j];
				}
			}
			scoreArray = tmp; // 把临时数组重新赋给scoreArray
			
		}

		similarity = score / arrayLen;
		return similarity;
	}

	/**
	 * 采用Fuzzy Set方式计算两个义原集合的相似度
	 * @param sememes1
	 * @param sememes2
	 * @return
	 */
	protected double getSimilarity_Fuzzy(String[] sememes1, String[] sememes2) {
		//@ TODO 
		return 0.0;
	}
	

}
