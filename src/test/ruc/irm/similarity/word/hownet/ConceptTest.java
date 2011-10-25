package ruc.irm.similarity.word.hownet;

import java.util.Collection;

import ruc.irm.similarity.word.hownet2.concept.Concept;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ConceptTest {
	public static void main(String[] args) {
		Multimap<String, Concept> CONCEPTS = HashMultimap.create();
//		CONCEPTS = ArrayListMultimap.create();
		
		CONCEPTS.put("打", new Concept("打", "V", "TakeOutOfWater|捞起"));
		CONCEPTS.put("打", new Concept("打", "V", "TakeOutOfWater|捞起"));
		CONCEPTS.put("打", new Concept("打", "V", "TakeOutOfWater|捞起"));
		CONCEPTS.put("打", new Concept("打", "V", "TakeOutOfWater|捞起"));
		
		Collection<Concept> collection = CONCEPTS.get("打");
		for(Concept c:collection){
			System.out.println(c);
		}
		
		Multimap<String, Integer> map = HashMultimap.create();
//	map = ArrayListMultimap.create();
	
	map.put("打", 1);
	map.put("打", 1);
	map.put("打", 1);
	map.put("打", 2);
	
	Collection<Integer> cc = map.get("打");
	for(Integer i:cc){
		System.out.println(i);
	}
  }
}
