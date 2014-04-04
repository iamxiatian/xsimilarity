package ruc.irm.tendency.word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ruc.irm.similarity.util.BlankUtils;
import ruc.irm.similarity.word.hownet2.concept.Concept;
import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;
import ruc.irm.similarity.word.hownet2.sememe.XiaSememeParser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 临时训练及测试类
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class Training {
    
    void test(boolean testPositive) throws IOException{
        WordTendency tendency = new HownetWordTendency();
        File f = new File("./dict/sentiment/负面情感词语（中文）.txt");
        if(testPositive){
            //f = new File("./dict/sentiment/正面情感词语（中文）.txt");
            f = new File("./dict/sentiment/正面评价词语（中文）.txt");
        }
        String encoding = "utf-8";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f),    encoding));
        String line;
        int wordCount = 0;
        int correctCount = 0;
        while ((line = in.readLine()) != null) {
            if(line.length()>5) continue;
            wordCount++;
            
            double value =tendency.getTendency(line.trim());
            if(value>0 && testPositive){
                 correctCount++;                
            }else if(value<0 && !testPositive){
                correctCount++;                
            }else{
                System.out.println("error:" + line + "\t value:" + value);
            }
        }
        System.out.println("correct:" + correctCount);
        System.out.println("total:" + wordCount);
        System.out.println("ratio:" + correctCount*1.0/wordCount);
    }
	
	/**
	 * 该方法用于统计知网提供的情感词集合所涉及的义原以及出现频度
	 * @throws IOException 
	 */
	/**
	 * @throws IOException
	 */
	void countSentimentDistribution() throws IOException{	    
		Map<String, Integer> sememeMap = new HashMap<String, Integer>();
		File f = new File("./dict/sentiment/负面情感词语（中文）.txt");
		String encoding = "utf-8";
		boolean autoCombineConcept = false;
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f),	encoding));
		
		XiaConceptParser parser = new XiaConceptParser(new XiaSememeParser());
		
		String line = null;

		int conceptCount = 0;
		int wordCount = 0;
		while ((line = in.readLine()) != null) {
			if(line.length()>5) continue;
			wordCount++;
			String word = line.trim();
			Collection<Concept> concepts = parser.getInnerConcepts(word);
			//由于目前的词典为知网2000版本，所以默认情况下仅对词典中出现的概念进行统计
			if(BlankUtils.isBlank(concepts) && autoCombineConcept ){
				concepts = parser.autoCombineConcepts(word, null);
			}
			for(Concept c: concepts){
			    conceptCount++;
				List<String> names = new ArrayList<String>();
				
				//加入主义原
				names.add(c.getMainSememe());
				
				//加入关系义原
				for(String item:c.getRelationSememes()){
					names.add(item.substring(item.indexOf("=") + 1));
				}				

				//加入符号义原
				for(String item:c.getSymbolSememes()){
					names.add(item.substring(1));
				}
				
                //加入其他义原集合
                for(String item:c.getSecondSememes()){
                    names.add(item);
                }
                
				for(String item:names){
					Integer count = sememeMap.get(item);
					if(count==null){
						sememeMap.put(item, 1);
					}else{
						sememeMap.put(item, count+1);
					}
				}
			}			
		}
		in.close();
		
		//以下是为了按照义原出现的数量进行排序的代码
		Multimap<Integer, String> map2 = HashMultimap.create();
		for(String key:sememeMap.keySet()){
		    map2.put(sememeMap.get(key), key);
		}
		List<Integer> keys = new ArrayList<Integer>();
		for(Integer key: map2.keySet()){
		    keys.add(key);
		}
		Collections.sort(keys);
		
		int smallSememeCount = 0; //较少出现的不同义原数量
		int smallAppearTotal = 0;    //较少出现的义原在概念众出现的次数总和
		for(int index=(keys.size()-1); index>=0; index--){
		    Integer key = keys.get(index);
		    Collection<String> values = map2.get(key);
		    double ratio =  (key*100.0/conceptCount);
		    System.out.print(key + "(" + ratio + "%): ");
		    for(String v:values){
		        System.out.print(v+ "\t");
		    }
		    System.out.println();
		    if(ratio<0.7){
		        smallSememeCount += values.size();
		        smallAppearTotal += key*values.size();
		    }
		}		
		
		System.out.println("small info: ");
		System.out.println("\tdifferent sememes:" + smallSememeCount);
		System.out.println("\tappear count:" + smallAppearTotal);
        System.out.println("\tratio:" + smallAppearTotal*100.0/conceptCount);
		System.out.println("wordCount:" + wordCount);
		System.out.println("conceptCount:" + conceptCount);
	}

    public static void main(String[] args) throws IOException {
        Training training = new Training();
        training.countSentimentDistribution();
//        System.out.println("test positive:");
//        training.test(true);
//        
//        System.out.println("test negative:");
        //training.test(false);
    }
}
