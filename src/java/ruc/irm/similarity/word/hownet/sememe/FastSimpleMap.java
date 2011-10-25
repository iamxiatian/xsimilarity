package ruc.irm.similarity.word.hownet.sememe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 一种新的Map，跟标准的Map不同，它的的Key可以有重复, 内部采用快速排序和二分查找,
 * 保持较少的变量，结构简单，可根据主键查找返回的结果是一个数组
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * 
 * @param <T>
 * @param <V>
 * @deprecated
 */
public class FastSimpleMap<K extends Comparable<K>, V> {
	private K[] keys;
	private V[] values;
	
	public FastSimpleMap(K[] keys, V[] values) throws IOException{
		if(keys.length!=values.length){
			throw new IOException("keys length must be equals values");
		}
		this.keys = keys;
		this.values = values;
		
		// 根据keys进行排序
		quicksort(0, keys.length-1);
	}
	
	/**
	 * 查找键对应的值集合
	 * @param key
	 * @return
	 */
	public Collection<V> get(K key) {
		int low = 0;
		int high = keys.length - 1;
		
		Collection<V> results = new ArrayList<V>();

		while (low <= high) {
			int mid = (low + high) >> 1;
			K item = keys[mid];
			int cmp = key.compareTo(item);
			
			if (cmp > 0) {
				low = mid + 1;
			} else if (cmp < 0) {
				high = mid - 1;
			} else {				
				// 找到起始位置，该位置前后相同的都是该主键对应的值
				for(int i=mid;i>=0 && keys[i].equals(key); i--){
					results.add(values[i]);
				}				
				for(int i=mid+1; i<keys.length && keys[i].equals(key); i++){
					results.add(values[i]);
				}
				
				break; // break while
			}
		}
		
		return results;
	}
	
	/**
	 * 根据keys快速排序，排序的同时交换values
	 * 
	 * @param a
	 * @param low
	 * @param high
	 */
	private void quicksort (int low, int high)
	{
		//low is the lower index, high is the upper index
		//of the region of array a that is to be sorted
	    int i=low, j=high;
	    K h;
	    V v;
	    K x=keys[(low+high)>>1];

	    //partition
	    do {    
	        while (keys[i].compareTo(x)<0) i++; 
	        while (keys[j].compareTo(x)>0) j--;
	        
	        if (i<=j)
	        {
	            h=keys[i]; keys[i]=keys[j]; keys[j]=h;
	            v=values[i]; values[i]=values[j]; values[j]=v;
	            i++; j--;
	        }
	    } while (i<=j);

	    //  recursion
	    if (low<j) quicksort(low, j);
	    if (i<high) quicksort(i, high);
	}
}
