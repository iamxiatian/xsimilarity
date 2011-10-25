package ruc.irm.similarity.util;

/**
 * 遍历接口, 对于需要遍历的东西，通过传入该接口，可以实现实际的访问处理
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * 
 * @param <T>
 */
public interface TraverseEvent<T> {
	
	/** 
	 * 遍历时访问其中的一个条目
	 * @param item
	 * @return
	 */
	public boolean visit(T item);
}
