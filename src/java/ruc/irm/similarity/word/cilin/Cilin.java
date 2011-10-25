package ruc.irm.similarity.word.cilin;

import java.util.Set;

import ruc.irm.similarity.Similaritable;


public class Cilin implements Similaritable {
	private static Cilin instance = null;
	
	public static Cilin getInstance(){
		if(instance == null){
			instance = new Cilin();
		}
		return instance;
	}
	
	private Cilin(){
		
	}
	
	@Override
	public double getSimilarity(String item1, String item2) {
		double sim = 0.0;
		
		if(item1==null && item2==null){
			return 1.0;
		}else if(item1==null || item2==null){
			return 0.0;
		}else if(item1.equalsIgnoreCase(item2)){
			return 1.0;
		}
		
		Set<String> codeSet1 = CilinDb.getInstance().getCilinCoding(item1);
		Set<String> codeSet2 = CilinDb.getInstance().getCilinCoding(item2);
		if(codeSet1==null || codeSet2==null){
			return 0.0;
		}
		for(String code1:codeSet1){
			for(String code2:codeSet2){
				double s = getSimilarityByCode(code1, code2);
				System.out.println(code1 + "-" + code2 + "-" +CilinCoding.calculateCommonWeight(code1, code2));
				if(sim<s) sim = s;
			}
		}
		return sim;
	}
	
	public double getSimilarityByCode(String code1, String code2){
		return CilinCoding.calculateCommonWeight(code1, code2)/CilinCoding.TOTAL_WEIGHT;
	}

}
