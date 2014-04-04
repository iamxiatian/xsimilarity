package ruc.irm.similarity.util;

import java.util.Collection;

/**
 * 判断是否为空的工具类
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class BlankUtils {
	/**
	 * 判断字符串s是否是空串
	 * @param s
	 * @return
	 */
	public static boolean isBlank(String string){
		return string==null || string.trim().equals("");
	}	
	
	/**
	 * 判断数组是否是空
	 * @param array
	 * @return
	 */
	public static boolean isBlank(Object[] array){
		return array==null || array.length==0;
	}
	
	/**
	 * 判断集合是否是空
	 * @param array
	 * @return
	 */
	public static boolean isBlank(Collection<? extends Object> array){
		return array==null || array.size()==0;
	}
	
	/**
	 * 判断所有的集合是否都为空
	 * @param collections 
	 * @return
	 */
	public static boolean isBlankAll(Collection<?>...collections){
		for(Collection<?> c:collections){
			if(!isBlank(c)){
				return false;
			}
		}

		return true;	
	}
	
	/**
	 * 判断字符串strings中是否都是空串
	 * @param strings
	 * @return
	 */
	public static boolean isBlankAll(String... strings){
		for(String s:strings){
			if(!isBlank(s)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 判断collections集合中是否至少有一个为空
	 * @param collections
	 * @return
	 */
	public static boolean isBlankAtLeastOne(Collection<?>...collections){
		for(Collection<?> c:collections){
			if(isBlank(c)){
				return true;
			}
		}

		return false;	
	}
	
	/**
	 * 判断字符串strings中是否之首有一个为空
	 * @param strings
	 * @return
	 */
	public static boolean isBlankAtLeastOne(String... strings){
		for(String s:strings){
			if(isBlank(s)){
				return true;
			}
		}
		
		return false;
	}
}
