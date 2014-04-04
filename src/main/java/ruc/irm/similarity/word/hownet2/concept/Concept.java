package ruc.irm.similarity.word.hownet2.concept;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import ruc.irm.similarity.word.hownet.HownetMeta;


/**
 * 知网的概念表示类 <br/>example和英文部分对于相似度的计算不起作用，考虑到内存开销， 在概念的表示中去掉了这部分数据的对应定义
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class Concept implements HownetMeta {
	/** 中文概念名称 */
	protected String word;
	/** 词性: Part of Speech */
	protected String pos;
	/** 定义 */
	protected String define;

	/** 是否是实词，false表示为虚词, 一般为实词 */
	protected boolean bSubstantive;
	/** 第一基本义原 */
	protected String mainSememe;
	/** 其他基本义原 */
	protected String[] secondSememes;
	/** 关系义元原 */
	protected String[] relationSememes;
	/** 关系符号描述 */
	protected String[] symbolSememes;

	static String[][] Concept_Type = { { "=", "事件" },
			{ "aValue|属性值", "属性值" }, { "qValue|数量值", "数量值" },
			{ "attribute|属性", "属性" }, { "quantity|数量", "数量" },
			{ "unit|", "单位" }, { "%", "部件" } };	

	public Concept(String word, String pos, String def) {		
		this.word = word;
		this.pos = pos;
		this.define = (def == null) ? "" : def.trim();
		
		// 虚词用{***}表示
		if (define.length() > 0 
				&& define.charAt(0) == '{'
				&& define.charAt(define.length() - 1) == '}'){
			this.bSubstantive = false;
		} else {
			this.bSubstantive = true;
		}

		parseDefine();
	}

	/**
	 * 处理定义，把定义分为第一基本义元、其他基本义元、关系义元和符号义元四类
	 */
	private void parseDefine() {
		List<String> secondList = new ArrayList<String>(); 		//其他基本义原
		List<String> relationList = new ArrayList<String>(); 	//关系义原
		List<String> symbolList = new ArrayList<String>(); 		//符号义原
		
		String tokenString = this.define;

		//如果不是实词，则处理“{}”中的内容
		if (!this.bSubstantive) {			
			tokenString = define.substring(1, define.length() - 1);
		}
		
		StringTokenizer token = new StringTokenizer(tokenString, ",", false);

		// 第一个为第一基本义元
		if (token.hasMoreTokens()) {
			this.mainSememe = token.nextToken();
		}
		
		main_loop: while (token.hasMoreTokens()) {
			String item = token.nextToken();
			if (item.equals("")) continue;
			
			// 先判断是否为符号义元
			String symbol = item.substring(0, 1);		
			for(int i=0;i< Symbol_Descriptions.length;i++){
		    	if(symbol.equals( Symbol_Descriptions[i][0])){
		            symbolList.add(item);		            
		            continue main_loop;
		    	}
			}
			
			//如果不是符号义元，则进一步判断是关系义元还是第二基本义元, 带有“=”表示关系义原
			if (item.indexOf('=') > 0){
				relationList.add(item);
			} else {
				secondList.add(item);
			}			
		}
		
		this.secondSememes = secondList.toArray(new String[secondList.size()]);
		this.relationSememes = relationList.toArray(new String[relationList.size()]);
		this.symbolSememes = symbolList.toArray(new String[symbolList.size()]);

	}
	
	/**
	 * 获取第一义元
	 * 
	 * @return
	 */
	public String getMainSememe() {
		return mainSememe;
	}
	
	/**
	 * 获取其他基本义元描述
	 * 
	 * @return
	 */
	public String[] getSecondSememes() {
		return secondSememes;
	}

	/**
	 * 获取关系义元描述
	 * 
	 * @return
	 */
	public String[] getRelationSememes() {
		return relationSememes;
	}

	/**
	 * 获取符号义元描述
	 * 
	 * @return
	 */
	public String[] getSymbolSememes() {
		return symbolSememes;
	}
	
	public Set<String> getAllSememeNames(){
	    Set<String> names = new HashSet<String>();
        
        //加入主义原
        names.add(getMainSememe());
        
        //加入关系义原
        for(String item:getRelationSememes()){
            names.add(item.substring(item.indexOf("=") + 1));
        }               

        //加入符号义原
        for(String item:getSymbolSememes()){
            names.add(item.substring(1));
        }
        
        //加入其他义原集合
        for(String item:getSecondSememes()){
            names.add(item);
        }
        return names;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=");
		sb.append(this.word);
		sb.append("; pos=");
		sb.append(this.pos);
		sb.append("; define=");
		sb.append(this.define);
		sb.append("; 第一基本义元:[" + mainSememe);
		
		sb.append("]; 其他基本义元描述:[");
		for(String sem: secondSememes){
			sb.append(sem);
			sb.append(";");
		}

		sb.append("]; [关系义元描述:");
		for(String sem: relationSememes){
			sb.append(sem);
			sb.append(";");
		}

		sb.append("]; [关系符号描述:");
		for(String sem: symbolSememes){
			sb.append(sem);
			sb.append(";");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 是实词还是虚词
	 * 
	 * @return true:实词；false:虚词
	 */
	public boolean isSubstantive() {
		return this.bSubstantive;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getDefine() {
		return define;
	}

	public void setDefine(String define) {
		this.define = define;
	}

	/**
	 * 获取该概念的类型
	 * 
	 * @return
	 */
	public String getType() {
		for (int i = 0; i < Concept_Type.length; i++) {
			if (define.toUpperCase().indexOf(Concept_Type[i][0].toUpperCase()) >= 0) {
				return Concept_Type[i][1];
			}
		}
		return "普通概念";
	}	

	@Override
	public int hashCode(){
		return define==null?word.hashCode():define.hashCode();
	}
	
	@Override
	public boolean equals(Object anObject){
		if(anObject instanceof Concept){
			Concept c = (Concept)anObject;
			return word.equals(c.word) && define.equals(c.define);
		}else{
			return false;
		}
	}

}