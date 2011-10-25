package ruc.irm.similarity.sentence.editdistance;

import java.util.ArrayList;
import java.util.List;

import ruc.irm.similarity.sentence.SegmentProxy;
import ruc.irm.similarity.sentence.SegmentProxy.Word;


/**
 * 超级字符串，可以存放指定的数据类型
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 *
 * @param <T>
 */
public class SuperString<T> {
	private List<T> contents = new ArrayList<T>();
	
	public SuperString(List<T> contents){
		this.contents = contents;
	}
	
	public static SuperString<CharEditUnit> createCharSuperString(String str){
		List<CharEditUnit> list = new ArrayList<CharEditUnit>(str.length());
		for(int i=0; i<str.length(); i++){
			list.add(new CharEditUnit(str.charAt(i)));
		}
		SuperString<CharEditUnit> s = new SuperString<CharEditUnit>(list);
		return s;
	}
	
	public static SuperString<WordEditUnit> createWordSuperString(String sentence){
		List<Word> wordList = SegmentProxy.segment(sentence);
		List<WordEditUnit> unitList = new ArrayList<WordEditUnit>(wordList.size());
		for(int i=0; i<wordList.size(); i++){
			unitList.add(new WordEditUnit(wordList.get(i)));
		}
		SuperString<WordEditUnit> s = new SuperString<WordEditUnit>(unitList);
		return s;
	}
	
	
	public T elementAt(int pos){
		if(pos<0 || pos>=contents.size()){
			throw new ArrayIndexOutOfBoundsException("下标越界");
		}
		return contents.get(pos);
	}
	
	public int indexOf(SuperString<?> substring){
		int result = -1;
		for(int i=0; i<length(); i++){
			int j=0;
			if(i+substring.length()>length()) return -1;
			
			for(;j<substring.length();j++){				
				if(elementAt(i+j).equals(substring.elementAt(j))){
					continue;					
				}else{
					break;
				}
			}
			if(j==substring.length()){
				return i;
			}
		}
		return result;
	}
	
	public SuperString<T> substring(int fromIndex, int toIndex){
		return new SuperString<T>(contents.subList(fromIndex, toIndex));
	}
	
	public SuperString<T> substring(int fromIndex){
		return new SuperString<T>(contents.subList(fromIndex, contents.size()));
	}
	
	public int length(){
		return contents.size();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<length(); i++){
			sb.append(elementAt(i));
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object other){
    	return toString().equals(other.toString());
	}
}
