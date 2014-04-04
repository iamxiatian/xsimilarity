package ruc.irm.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ruc.irm.similarity.sentence.SegmentProxy;
import ruc.irm.similarity.sentence.SegmentProxy.Word;

/**
 * 代表一个文档实例
 * 
 * @author xiatian
 * 
 */
public class Instance {
	/** 文档类别 */
	private String category;
	/** 文档内容 */
	private Set<String> bag = new HashSet<String>();

	public Instance() {
	}

	public Instance(String category, File f, String encoding) {
		this.category = category;
		String line = null;
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding));
		
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				List<Word> words = SegmentProxy.segment(line);
				for(Word w:words) {
                    if (w.getPos().endsWith("adj")
                            || w.getPos().startsWith("n")
                            || w.getPos().startsWith("v")) {
                        bag.add(w.getWord());
                    }
                }
            }
		} catch (IOException e) {
			System.out.println("current file:" + f.getAbsolutePath());
			System.out.println("current line:" + line);
			e.printStackTrace();
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Set<String> getWords() {
		return bag;
	}

}
