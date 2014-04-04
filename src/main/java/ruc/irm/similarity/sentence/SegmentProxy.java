package ruc.irm.similarity.sentence;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.ictclas4j.bean.SegNode;
import org.ictclas4j.bean.SegResult;
import org.ictclas4j.segment.Segment;
import org.ictclas4j.utility.POSTag;

/**
 * 对词法分析程序的封装代理，目前内部封装了对Ictclas4j（夏天改进版）的调用<br/>
 * 为方便演示程序快速启动，对Segment的调用采用了单例模式，实现需要时的延迟加载。
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class SegmentProxy {
	static Segment seg = null;
	
	private static Segment getSegment(){
		if(null==seg){
			seg = new Segment(1);
		}
		return seg;
	}
	
	public static class Word {
		/** 词语内容 */
		private String word;
		/** 词语词性数字代号 */
		private int pos;
		
		public Word(String word, int pos){
			this.word = word;
			this.pos = pos;
		}		
		public String getWord() {
			return word;
		}
		public void setWord(String word) {
			this.word = word;
		}
		public int getPos() {
			return pos;
		}
		public void setPos(int pos) {
			this.pos = pos;
		}			
	}
	
	public static List<Word> segment(String sentence){
		List<Word> results = new ArrayList<Word>();
		SegResult segResult = getSegment().split(sentence);
		for(SegNode segNode: segResult.getSegNodes()){
			if (segNode.getPos() != POSTag.SEN_BEGIN && segNode.getPos() != POSTag.SEN_END) {
				results.add(new Word(segNode.getSrcWord(), segNode.getPos()));
			}
		}
		return results;
	}
	
	public static String getSegmentedString(String sentence){
		SegResult segResult = getSegment().split(sentence);
		return segResult.getFinalResult();
	}
	
	public static JPanel createPanel(){
    	//声明总的大面板, fullPanel包括一个NorthPanel和一个centerPanel
		JPanel fullPanel = new JPanel();
        fullPanel.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        fullPanel.add(northPanel, "North");
        
        //centerPanel包括了一个文本框
        JPanel centerPanel = new JPanel();
        fullPanel.add(centerPanel, "Center");        
        centerPanel.setLayout(new BorderLayout());        
        final JTextArea result = new JTextArea();
        //result.setFont(new Font("宋体", Font.PLAIN, 16));
        result.setLineWrap(true);
        JScrollPane centerScrollPane = new JScrollPane(result);
        centerPanel.add(centerScrollPane, "Center");
        
        northPanel.setLayout(new GridLayout(1, 1));        
        
        //以下加入northPanel中的第一个面板
    	final JTextField senField = new JTextField("什么是计算机病毒");    
        senField.setColumns(50);
        
        JPanel mainPanel = new JPanel();        
        mainPanel.setLayout(new GridLayout(2, 1));        
                
        JPanel linePanel = new JPanel();
        linePanel.add(new JLabel("句子:"));        
        linePanel.add(senField); 
        mainPanel.add(linePanel);
        
        linePanel = new JPanel();
        JButton goButton = new JButton("词法分析");
        linePanel.add(goButton);
        mainPanel.add(linePanel);
        goButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String sentence = senField.getText();
				String text = "[" + sentence + "]的词法分析结果为:" ;
				
				SegResult segResult = getSegment().split(sentence);
				text = text + "\n" + segResult.getFinalResult();
				text = text + "\n________________________________\n" + result.getText();
     			result.setText(text);
			}
        	
        });
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        northPanel.add(mainPanel);        
        
        return fullPanel;
    }
}
