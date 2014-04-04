package ruc.irm.similarity.word.cilin;

/**
 * 表2-3 哈工大词林扩展版规则编码表<br/>
 * <table border="1" style="color:red;">
 * <tr>
 * <td>编码位</td><td>1</td><td>	2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td>
 * </tr>
 * <tr>
 * <td>编码示例</td><td>C</td><td>b</td><td>0</td><td>7</td><td>A</td><td>0</td><td>3</td><td>=</td>
 * </tr><tr>
 * <td>类别级别</td><td>第一级</td><td>第二级</td><td colspan="2">第三级</td><td>第四级</td><td colspan="2">第五级</td><td>标记位</td><td>
 * </tr><tr>
 * <td>类别含义</td><td>大类</td><td>中类</td><td colspan="2">小类</td><td>词群</td><td colspan="2">原子词群</td><td>词语关系</td>
 * </tr>
 * </table>
 * <br/>
 * 表中编码位从左到右顺序排列，其中，第8位对应的标记位为“=”、“#”和“@”三种符号之一。其中“=”代表常见的“同义”关系，“#”代表词语之间的相关关系，“@”则代表词语自我封闭的独立性质，它在词典中既没有同义词，也没有相关词。
 * 
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class CilinCoding {
	public static double[] WEIGHT = new double[]{1.2, 1.2, 1.0, 1.0, 0.8, 0.4};
	public static double TOTAL_WEIGHT = 5.6;
	
	public static String getCodeLevel(String code,int level){
		switch(level){
		case 1:
			return code.substring(0, 1);
		case 2:
			return code.substring(1, 2);
		case 3:
			return code.substring(2, 4);
		case 4:
			return code.substring(4, 5);
		case 5:
			return code.substring(5, 7);
		case 6:
			return code.substring(7);
		}

		return "";
	}
	
	/**
	 * 获取共同部分编码的权重
	 * @param code1
	 * @param code2
	 * @return
	 */
	public static double calculateCommonWeight(String code1, String code2){
		double weight = 0.0;
		for(int i=1; i<=6; i++){
			String c1 = getCodeLevel(code1,i);
			String c2 = getCodeLevel(code2,i);
			if(c1.equals(c2)){
				weight += WEIGHT[i-1];
			}else{
				break;
			}
		}
		return weight;
	}
	
	public static String printCoding(String code){
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<=6; i++){
			if(i==1){
				sb.append("[LEVEL_" + i);
			}else{
				sb.append(", LEVEL_" + i);
			}
			sb.append(": ");
			sb.append(getCodeLevel(code, i));
		}
		sb.append("]");
		
		return sb.toString();
	}
}
