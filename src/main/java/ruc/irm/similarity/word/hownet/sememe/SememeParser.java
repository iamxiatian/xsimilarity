package ruc.irm.similarity.word.hownet.sememe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ruc.irm.similarity.Similaritable;
import ruc.irm.similarity.util.BlankUtils;
import ruc.irm.similarity.util.FileUtils;
import ruc.irm.similarity.word.hownet.HownetMeta;

/**
 * 义原解析器, 包括义元数据的加载，义元的组织、索引、查询 以及义元的距离计算和相似度计算等.
 * 算法的核心思想请参看论文《汉语词语语义相似度计算研究》
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * 
 * @see ruc.irm.similarity.Similaritable
 * @deprecated
 */
public abstract class SememeParser implements HownetMeta, Similaritable {
	protected Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	/** 所有的义原都存放到一个数组之中，并且义元的ID号与数组的下标相同 */
	protected Sememe[] SEMEMES;

	/** 通过对义原的汉语词义进行索引，根据该索引快速定位义原，找出义原的id，再到sememes中查找 */
	private FastSimpleMap<String, Integer> sememeMap = null;		
	
	public SememeParser() throws IOException{
		String sememeFile = getClass().getPackage().getName().replaceAll("\\.", "/") + "/sememe.dat";
		
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(sememeFile);
		load(input, "UTF-8");
	}
	
	/**
	 * 获取两个义原描述串的相似度
	 * @param sememeName1
	 * @param sememeName2
	 * @see ke.commons.similarity.Similariable
	 * @return
	 */
	public abstract double getSimilarity(String sememeName1, String sememeName2);
	
	/**
	 * 获取两个确定义原的相似度
	 * @param sememe1
	 * @param sememe2
	 * @return
	 */
	public abstract double getSimilarity(Sememe sememe1, Sememe sememe2);
	
	/**
	 * 从文件中加载义元知识
	 * 
	 * @throws IOException
	 */
	public void load(InputStream input, String encoding) throws IOException {	
		SememeDictTraverseEvent event = new SememeDictTraverseEvent();
		LOG.info("loading sememe dictionary...");
		long time = System.currentTimeMillis();
		FileUtils.traverseLines(input, encoding, event);
		this.SEMEMES = event.getSememes();	
		
		String[] keys = new String[SEMEMES.length];
		Integer[] values = new Integer[SEMEMES.length];

	    //设置索引
	    for(int i=0; i<SEMEMES.length; i++){
	    	keys[i] = SEMEMES[i].getCnWord();
	    	values[i] = SEMEMES[i].getId();
	    }
	    sememeMap = new FastSimpleMap<String, Integer>(keys, values);
	    
	    time = System.currentTimeMillis() - time;
	    LOG.info("sememe dictionary load completely. time elapsed: " + time);
	}

	/**
	 * 根据汉语定义计算义元之间的距离,Integer.MAX_VALUE代表两个义元之间的距离为无穷大， 
	 * <br/>由于可能多个义元有相同的汉语词语，故计算结果为其中距离最小者
	 * 
	 * @param key1
	 * @param key2
	 * @return
	 */
	public int getDistance(String key1, String key2) {
		int distance = Integer.MAX_VALUE;

		// 如果两个字符串相等，直接返回距离为0
		if (key1.equals(key2)) {
			return 0;
		}

		Integer[] semArray1 = getSememes(key1);
		Integer[] semArray2 = getSememes(key2);
		
		// 如果key1或者key2不是义元，并且key1<>key2,则返回无穷大
		if (semArray1.length == 0 || semArray2.length == 0) {
			return Integer.MAX_VALUE;
		}

		for(int i:semArray1){
			for(int j:semArray2){
				int d = getDistance(SEMEMES[i], SEMEMES[j]);
				if(d<distance){
					distance = d;
				}
			}
		}
		
		return distance;
	}

	/**
	 * 获取两个义元在义原树中的距离
	 * 
	 * @param sem1
	 *            第一个义原
	 * @param sem2
	 *            第二个义原
	 * @return 两个义原的距离
	 */
	public int getDistance(Sememe sem1, Sememe sem2) {
		Sememe mysem1 = sem1;
		Sememe mysem2 = sem2;
		int distance = 0;

		if (mysem1 == null || mysem2 == null)
			return Integer.MAX_VALUE;
		
		//变为深度相同，然后一次上找共同的父节点
		int level = mysem1.getDepth() - mysem2.getDepth();
		for (int i = 0; i < ((level < 0) ? level * -1 : level); i++) {
			if (level > 0)
				mysem1 = SEMEMES[mysem1.getParentId()];
			else
				mysem2 = SEMEMES[mysem2.getParentId()];
			distance++;
		}

		//从不同的分支（深度相同）同时向上寻找共同的祖先节点
		while (mysem1.getId() != mysem2.getId()) {
			// 如果已经到达根节点，仍然不同，则返回无穷大(-1)
			if (mysem1.getId() == mysem1.getParentId()
					|| mysem2.getId() == mysem2.getParentId()) {
				distance = Integer.MAX_VALUE;
				break;
			}

			mysem1 = SEMEMES[mysem1.getParentId()];
			mysem2 = SEMEMES[mysem2.getParentId()];
			distance += 2;
		}

		return distance;
	}

	/**
	 * 获取从该义元到根节点的路径表示字符串
	 * 
	 * @param key
	 * @return
	 */
	public String getPath(String key) {
		StringBuilder path = new StringBuilder();
		
		Sememe sem = getSememe(key);
		while (sem != null && sem.getId() != sem.getParentId()) {
			path.insert(0, "->" + sem.getCnWord());
			sem = SEMEMES[sem.getParentId()];
		}
		
		if (sem != null){
			path.insert(0, "->" + sem.getCnWord());
		}			
		path.insert(0, "START");
		return path.toString();
	}

	/**
	 * 根据义原的名字，获取该义原的位置信息，义原体系中有时会有一个名字对应多个义原，一并返回到
	 * 义原数组中
	 * @param sememeName
	 * @return
	 */
	public Integer[] getSememes(String sememeName) {
		Collection<Integer> ids = sememeMap.get(sememeName);

		return ids.toArray(new Integer[ids.size()]);
	}	
	
	/**
	 * 获取其中的一个义原，大部分义原就只有一个
	 * @param sememeName
	 * @return
	 */
	public Sememe getSememe(String sememeName){
		Integer[] ids = getSememes(sememeName);
		
		if(BlankUtils.isBlank(ids)){
			return null;
		}else{
			return SEMEMES[ids[0]];
		}
	}
	
	/**
	 * 过滤义原字符串，去掉其中的英文部分
	 * @param sememeString
	 * @return
	 */
	protected String filterSememeString(String sememeString){
		int pos = sememeString.indexOf("|");
		if (pos >= 0) {
			sememeString = sememeString.substring(pos + 1);
		}
		return sememeString;
	}
	
}
