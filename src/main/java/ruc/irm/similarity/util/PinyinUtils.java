package ruc.irm.similarity.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 拼音处理的工具，负责从拼音词典加载内容，根据汉字词语或汉字查找拼音
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class PinyinUtils {
	/** 拼音的Map词典, 一个汉字可能对应多个拼音, 它所有的拼音放到一个集合中 */
	private Map<Character, Set<String>> pinyinDict = null;
	
	/** 单例 */
	private static PinyinUtils instance = null;
	
	private PinyinUtils() throws IOException{
		//从classpath中加载拼音词典文件
		String pinyinDictFile = getClass().getPackage().getName().replaceAll("\\.", "/") + "/F02-GB2312-to-PuTongHua-PinYin.txt";
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(pinyinDictFile);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(input,	"UTF-8"));
		String line = null;

		MyTraverseEvent event = new MyTraverseEvent();
		while ((line = in.readLine()) != null) {
			event.visit(line);
		}

		input.close();
		in.close();
		
		this.pinyinDict = event.getPinyins();
	}
	
	public static PinyinUtils getInstance(){
		if(instance == null){
			try {
				instance = new PinyinUtils();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		
		return instance;
	}
	
	/**
	 * 获取汉字的拼音, 由于汉字具有多音字，故返回一个集合
	 * @param hanzi
	 * @return
	 */
	public Set<String> getPinyin(Character hanzi){
		Set<String> set = pinyinDict.get(hanzi);
		if(set==null || set.size()==0){
			set = new HashSet<String>();
			set.add(hanzi.toString());
		}
		return set;
	}
	
	/**
	 * 获取词语的拼音, 一个词语可能对应多个拼音，把所有可能的组合放到集合中返回
	 * @param word
	 * @return
	 */
	public Set<String> getPinyin(String word){
		Set<String> word_set = new HashSet<String>();
		for(int i=0; i<word.length(); i++){
			Set<String> hanzi_set = getPinyin(word.charAt(i));
			if(word_set==null || word_set.size()==0){
				word_set.addAll(hanzi_set);
				continue;
			}
			
			Set<String> tmp_set = new HashSet<String>();
			for(String w:word_set){
				for(String h:hanzi_set){
					tmp_set.add(w + h);
				}
			}
			
			word_set = tmp_set;		
		}

		return word_set;
	}
	
	/**
	 * 获取拼音字符串，多音字只取一个
	 * @param word
	 * @return
	 */
	public String getPinyinSingle(String word){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<word.length(); i++){
			sb.append(getPinyin(word.charAt(i)).iterator().next());
		}
		return sb.toString();
	}
	
	/**
	 * 获取拼音串，对于多音字，给出所有拼音
	 * @param word
	 * @return
	 */
	public String getPinyinString(String word){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<word.length(); i++){
			Set<String> pinyin = getPinyin(word.charAt(i));
			sb.append(pinyin.toString());
		}
		return sb.toString();
	}
	
	/**
	 * 获取拼音首字母
	 * @param word
	 * @return
	 */
	public String getPinyinHead(String word){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<word.length(); i++){
			sb.append(getPinyin(word.charAt(i)).iterator().next().charAt(0));
		}
		return sb.toString();
	}
	
	private static class MyTraverseEvent {
		/** 一个汉字对应多个拼音, 多个拼音放到集合中 */
		private Map<Character, Set<String>> pinyins = null;
		
		public MyTraverseEvent(){
			this.pinyins = new HashMap<Character, Set<String>>();
		}
		
		public Map<Character, Set<String>> getPinyins(){
			return pinyins;
		}
		
		public boolean visit(String item) {
			if(item.startsWith("//")){
				return true;
			}
			
			char hanzi = item.charAt(0);
			//String pinyin = item.substring(2, item.length()-1);
			String pinyin = item.substring(2, item.length());
			Set<String> set = pinyins.get(hanzi);
			if(set==null){
				set = new HashSet<String>();
			}
			set.add(pinyin);
			
			pinyins.put(hanzi, set);
			return true;
		}		
	}
		
}
