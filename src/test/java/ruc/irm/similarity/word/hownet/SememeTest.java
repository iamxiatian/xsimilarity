package ruc.irm.similarity.word.hownet;

import java.io.InputStream;

import ruc.irm.similarity.util.FileUtils;
import ruc.irm.similarity.word.hownet.sememe.Sememe;
import ruc.irm.similarity.word.hownet.sememe.SememeDictTraverseEvent;
import ruc.irm.similarity.word.hownet2.sememe.XiaSememeParser;


/**
 * 针对义原的测试
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室 
 */
public class SememeTest {
	public static void main(String[] args) throws Exception{
		String id1 = "2-1-3-4";
//		String id2 = "2-1-2";
//		System.out.println(getDistance(id1, id2));
//		System.out.println(getSimilarityBySememeId(id1, id2));
		
		int pos = id1.lastIndexOf("-");
		String parentId = "root";
		if(pos>0){
			parentId = id1.substring(0, pos);
		}
		System.out.println(parentId);
		new XiaSememeParser().getSimilarity("test", "hello");
	}
	
	static void saveXML() throws Exception{
		String sememeFile = Sememe.class.getPackage().getName().replaceAll("\\.", "/") + "/sememe.dat";		
		InputStream input = Sememe.class.getClassLoader().getResourceAsStream(sememeFile);
		SememeDictTraverseEvent event = new SememeDictTraverseEvent();		
		
		FileUtils.traverseLines(input, "utf8", event);
		event.saveToXML("/home/xiatian/Desktop/sememe.xml");
	}
	
	static double getSimilarityBySememeId(final String id1, final String id2) {		
		
		int position = 0;
		String[] array1 = id1.split("-");
		String[] array2 = id2.split("-");
		for (position = 0; position < array1.length && position < array2.length; position++) {
			if (!array1[position].equals(array2[position])) {
				break;
			}
		}
		
		return 2.0*position/(array1.length + array2.length);
	}

	static int getDistance(String id1, String id2) {
		// 两个Id相同的位置终止地方
		int position = 0;
		String[] array1 = id1.split("-");
		String[] array2 = id2.split("-");
		for (position = 0; position < array1.length && position < array2.length; position++) {
			if (!array1[position].equals(array2[position])) {
				return array1.length + array2.length - position - position;
			}
		}

		if (array1.length == array2.length) {
			return 0;
		} else if (array1.length == position) {
			return array2.length - position;
		} else {
			return array1.length - position;
		}
	}
}
