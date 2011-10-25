package ruc.irm.similarity.word.cilin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ruc.irm.similarity.util.FileUtils;
import ruc.irm.similarity.util.TraverseEvent;

/**
 * 词林数据库
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class CilinDb {
	/** the logger */
	protected static Log LOG = LogFactory.getLog(CilinDb.class);
	/** 以词语为主键的索引表 */
	private Map<String, Set<String>> wordIndex = new HashMap<String, Set<String>>();
	/** 以编码为主键的索引表 */
	private Map<String, Set<String>> codeIndex = new HashMap<String, Set<String>>();
	
	private static CilinDb instance = null;
	
	public static CilinDb getInstance(){
		if(instance == null){
			try {
				instance = new CilinDb();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
		return instance;
	}
	
	private CilinDb() throws IOException{
		String cilinFile = getClass().getPackage().getName().replaceAll("\\.", "/") + "/cilin.db.gz";
		InputStream input = new GZIPInputStream(this.getClass().getClassLoader().getResourceAsStream(cilinFile));
		
		TraverseEvent<String> event = new TraverseEvent<String>(){
			@Override
			public boolean visit(String line) {
				String[] items = line.split(" ");
				Set<String> set = new HashSet<String>();
				for(int i=2; i<items.length; i++){
					String code = items[i].trim();
					if(!code.equals("")){
						set.add(code);
						
						//加入codeIndex编码
						Set<String> codeWords = codeIndex.get(code);
						if(codeWords==null){
							codeWords = new HashSet<String>();
						}
						codeWords.add(items[0]);
						codeIndex.put(code, codeWords);
					}
				}
				wordIndex.put(items[0], set);
				items = null;
				return false;
			}};
		LOG.info("loading cilin dictionary...");
		long time = System.currentTimeMillis();
		
		FileUtils.traverseLines(input, "UTF8", event);
		
		time = System.currentTimeMillis() - time;
		LOG.info("loading cilin dictionary completely. time elapsed: " + time);
		
	}
	
	/**
	 * 获取某个词语的词林编码，一个词语可以有多个编码，通过Set给出
	 * @param word
	 * @return
	 */
	public Set<String> getCilinCoding(String word){
		return wordIndex.get(word);
	}
	
	public Set<String> getCilinWords(String code){
		return codeIndex.get(code);
	}
	
	public static void main(String[] args) {
		CilinDb db = CilinDb.getInstance();
		String code = db.getCilinCoding("中国").iterator().next();
		System.out.println(CilinCoding.printCoding(code));
		System.out.println(db.getCilinWords(code));
	}
}
