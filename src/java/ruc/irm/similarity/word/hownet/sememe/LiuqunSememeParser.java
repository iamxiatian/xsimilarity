package ruc.irm.similarity.word.hownet.sememe;

import java.io.IOException;

/**
 * 刘群老师计算义原相似度的方法, 实现了SememeParser中定义的抽象方法
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * 
 * @author <a href="xiat@ruc.edu.cn">xiatian</a>
 * @version 1.0
 * @deprecated
 */
public class LiuqunSememeParser extends SememeParser {
		
	/** 计算义元相似度的可调节的参数，默认为1.6 */
	private final float alpha = 1.6f;	
	
	public LiuqunSememeParser() throws IOException {
		super();		
	}

	/**
	 * 计算两个义元之间的相似度，由于义元可能相同，计算结果为其中相似度最大者 
	 * <br/>similarity = alpha/(distance+alpha)
	 * 
	 * @param key1
	 * @param key2
	 * @return
	 */
	@Override
	public double getSimilarity(String item1, String item2) {
		int pos;

		// 如果为空串，直接返回0
		if (item1 == null || item2 == null || item1.equals("")
				|| item2.equals(""))
			return 0.0;

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
		if ((pos = key1.indexOf('=')) > 0) {
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

		int distance = getDistance(key1, key2);
		if (distance < 0)
			return 0.0;
		else
			return alpha / (distance + alpha);
	}

	@Override
	public double getSimilarity(Sememe sem1, Sememe sem2) {
		int distance = getDistance(sem1, sem2);
		if (distance <= 0)
			return 0.0f;
		else
			return alpha / (distance + alpha);
	}
}
