package ruc.irm.similarity.word.hownet.sememe;

import java.io.IOException;

import ruc.irm.similarity.util.BlankUtils;


/**
 * 义原相似度计算, 实现了SememeParser中定义的抽象方法
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * @deprecated 
 */
public class MySememeParser extends SememeParser {
	
	public MySememeParser() throws IOException{
		super();
	}
	
	/**
	 * 计算两个义原的相似度	 
	 */
	@Override
	public double getSimilarity(final Sememe sememe1, final Sememe sememe2) {		
		Sememe sem1 = sememe1;
		Sememe sem2 = sememe2;		

		if (sememe1 == null || sememe2 == null){
			return 0.0f;
		}else if(sememe1.getId() == sememe2.getId()){
			return 1.0f;
		}
		
		//变为深度相同，然后一次上找共同的父节点
		int level = sememe1.getDepth() - sememe2.getDepth();		
		for (int i = 0; i < ((level < 0) ? level * -1 : level); i++) {
			if (level > 0){
				sem1 = SEMEMES[sem1.getParentId()];
			}else{
				sem2 = SEMEMES[sem2.getParentId()];
			}
		}
		
		while(sem1.getId() != sem2.getId()){
			// 如果有一个已经到达根节点，仍然不同，则返回0
			if (sem1.getId() == sem1.getParentId()
					|| sem2.getId() == sem2.getParentId()) {
				return 0.0f;
			}
			
			sem1 = SEMEMES[sem1.getParentId()];
			sem2 = SEMEMES[sem2.getParentId()];
		}
		
		return sem1.getDepth()*2.0f/(sememe1.getDepth() + sememe2.getDepth());
	}

	/**
	 * 计算两个义元之间的相似度，由于义元可能相同，计算结果为其中相似度最大者 similarity = alpha/(distance+alpha),
	 * 如果两个字符串相同或都为空，直接返回1.0
	 * 
	 * @param key1 第一个义原字符串
	 * @param key2 第二个义原字符串
	 * @return
	 */
	@Override
	public double getSimilarity(String item1, String item2) {	
		if(BlankUtils.isBlankAll(item2, item2)){
			return 1.0;
		} else if(BlankUtils.isBlankAtLeastOne(item1, item2)){
			return 0.0;
		} else if(item1.equals(item2)){
			return 1.0;
		}		

		String key1 = item1.trim();
		String key2 = item2.trim();

		// 去掉()符号
		if ((key1.charAt(0) == '(') && (key1.charAt(key1.length() - 1) == ')')) {
			
			if (key2.charAt(0) == '(' && key2.charAt(key2.length() - 1) == ')') {
				key1 = key1.substring(1, key1.length() - 1);
				key2 = key2.substring(1, key2.length() - 1);
			} else {
				return 0.0;
			}
			
		}

		// 处理关系义元,即x=y的情况
		int pos = key1.indexOf('=');
		if (pos > 0) {
			int pos2 = key2.indexOf('=');
			// 如果是关系义元，则判断前面部分是否相同，如果相同，则转为计算后面部分的相似度，否则为0
			if ((pos == pos2)
					&& key1.substring(0, pos).equals(key2.substring(0, pos2))) {
				key1 = key1.substring(pos + 1);
				key2 = key2.substring(pos2 + 1);
			} else {
				return 0.0;
			}
		}

		// 处理符号义元,即前面有特殊符号的义元
		String symbol1 = key1.substring(0, 1);
		String symbol2 = key2.substring(0, 1);

		for (int i = 0; i < Symbol_Descriptions.length; i++) {
			if (symbol1.equals(Symbol_Descriptions[i][0])) {
				if (symbol1.equals(symbol2)) {
					key1 = item1.substring(1);
					key2 = item2.substring(1);
					break;
				} else {
					return 0.0; // 如果不是同一关系符号，则相似度直接返回0
				}
			}
		}

		if ((pos = key1.indexOf("|")) >= 0) {
			key1 = key1.substring(pos + 1);
		}
		if ((pos = key2.indexOf("|")) >= 0) {
			key2 = key2.substring(pos + 1);
		}

		// 如果两个字符串相等，直接返回距离为0
		if (key1.equals(key2)) {
			return 1.0;
		}
		
		Integer[] myset1 = getSememes(key1);
		Integer[] myset2 = getSememes(key2);
		
		double similarity = 0.0;
		for(int id1:myset1){
			for(int id2:myset2){
				double s = getSimilarity(SEMEMES[id1], SEMEMES[id2]);
				if(s>similarity){
					similarity = s;
				}
			}
		}
		
		return similarity;
	}

	
}