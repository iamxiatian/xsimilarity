package ruc.irm.similarity.word.hownet.concept;

import java.util.LinkedList;

/**
 * 用于概念处理的LinkedList
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 *
 * @param <T>
 * @deprecated
 */
@SuppressWarnings("serial")
public class ConceptLinkedList extends LinkedList<Concept> {
	
	/**
	 * 删除链表中最后面的size个元素
	 * @param size
	 */
	public void removeLast(int size){
		for(int i=0;i<size;i++){
			this.removeLast();
		}
	}
	
	/**
	 * 根据概念的定义判断是否已经加入到链表中
	 * @param concept
	 */
	public void addByDefine(Concept concept){
		for(Concept c:this){
			if(c.getDefine().equals(concept.getDefine())){
				return;
			}
		}
		
		this.add(concept);
	}
}
