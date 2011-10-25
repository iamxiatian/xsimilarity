package ruc.irm.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ruc.irm.similarity.sentence.editdistance.CharEditUnit;
import ruc.irm.similarity.sentence.editdistance.EditDistance;
import ruc.irm.similarity.sentence.editdistance.GregorEditDistance;
import ruc.irm.similarity.sentence.editdistance.StandardEditDistance;
import ruc.irm.similarity.sentence.editdistance.SuperString;
import ruc.irm.similarity.sentence.editdistance.XiatianEditDistance;
import ruc.irm.similarity.sentence.morphology.MorphoSimilarity;


public class SentenceSimilarityUI {
    /**
     * 句子相似度的演示面板
     * @return
     */
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
//    	final JTextField senField1 = new JTextField("什么是计算机病毒");
//    	final JTextField senField2 = new JTextField("电脑病毒是什么");  
    	final JTextField senField1 = new JTextField("什么是计算机病毒");
    	final JTextField senField2 = new JTextField("电脑病毒会传染给人吗？");  
    	
        senField1.setColumns(50);
        senField2.setColumns(50);
        
    	JPanel mainPanel = new JPanel();   
        mainPanel.setLayout(new GridLayout(4, 1));        
                
        JPanel linePanel = new JPanel();
        linePanel.add(new JLabel("交换代价:"));  
        final JTextField swapField = new JTextField("0.5");
        swapField.setColumns(20);
        linePanel.add(swapField);
        JButton button = new JButton("设置");
        button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				double cost = Double.parseDouble(swapField.getText());
				XiatianEditDistance.swapCost = cost;
				GregorEditDistance.swapCost = cost;				
			}
        	
        });
        linePanel.add(button);
        mainPanel.add(linePanel);
        
        linePanel = new JPanel();
        linePanel.add(new JLabel("句子1:"));        
        linePanel.add(senField1); 
        mainPanel.add(linePanel);
        
        linePanel = new JPanel();
        linePanel.add(new JLabel("句子2:"));        
        linePanel.add(senField2); 
        mainPanel.add(linePanel);
        
        linePanel = new JPanel();
        JButton goButton = new JButton("计算句子相似度");
        linePanel.add(goButton);
        mainPanel.add(linePanel);
        goButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String sentence1 = senField1.getText();
				String sentence2 = senField2.getText();
				long time = 0;
				String text = "[" + sentence1 + "]与[" + sentence2 + "]的编辑距离为:" ;
				
				MorphoSimilarity similarity = MorphoSimilarity.getInstance();
				text = text + "\n词形和词序结合法结果：";		
				text = text + "\n" + similarity.getSimilarity(sentence1, sentence2);
				System.out.println(text);
				
				EditDistance ed = new StandardEditDistance();
				text = text + "\n标准编辑距离算法结果：";				
				time = System.currentTimeMillis();
				text = text + "\n词语处理:" + ed.getSimilarity(sentence1, sentence2);
				text = text + "(时间:" + time + ")";
				System.out.println(text);
				
				ed = new GregorEditDistance();
				text = text + "\nGregor编辑距离算法结果：";
				time = System.currentTimeMillis();
				text = text + "\n词语处理:" + ed.getSimilarity(sentence1, sentence2);
				text = text + "(时间:" + time + ")";		
				System.out.println(text);
				
				ed = new XiatianEditDistance();
				text = text + "\n夏氏编辑距离算法结果：";
				time = System.currentTimeMillis();
				text = text + "\n词语处理:" + ed.getSimilarity(sentence1, sentence2);
				text = text + "(时间:" + time + ")";
				System.out.println(text);
				
//				EditDistance ed = new StandardEditDistance();
//				text = text + "\n标准编辑距离算法结果：";				
//				time = System.currentTimeMillis();
//				text = text + "\n字符串处理:" + ed.getEditDistance(SuperString.createCharSuperString(sentence1), SuperString.createCharSuperString(sentence2));
//				time = System.currentTimeMillis() - time;
//				text = text + "(时间:" + time + ")";				
//				time = System.currentTimeMillis();
//				text = text + "\n词语处理:" + ed.getEditDistance(SuperString.createWordSuperString(sentence1), SuperString.createWordSuperString(sentence2));
//				text = text + "(时间:" + time + ")";
//				
//				ed = new GregorEditDistance();
//				text = text + "\nGregor编辑距离算法结果：";
//				time = System.currentTimeMillis();
//				text = text + "\n字符串处理:" + ed.getEditDistance(SuperString.createCharSuperString(sentence1), SuperString.createCharSuperString(sentence2));
//				time = System.currentTimeMillis() - time;
//				text = text + "(时间:" + time + ")";				
//				time = System.currentTimeMillis();
//				text = text + "\n词语处理:" + ed.getEditDistance(SuperString.createWordSuperString(sentence1), SuperString.createWordSuperString(sentence2));
//				text = text + "(时间:" + time + ")";				
//				
//				ed = new XiatianEditDistance();
//				text = text + "\n夏氏编辑距离算法结果：";
//				time = System.currentTimeMillis();
//				text = text + "\n字符串处理:" + ed.getEditDistance(SuperString.createCharSuperString(sentence1), SuperString.createCharSuperString(sentence2));
//				time = System.currentTimeMillis() - time;
//				text = text + "(时间:" + time + ")";				
//				time = System.currentTimeMillis();
//				text = text + "\n词语处理:" + ed.getEditDistance(SuperString.createWordSuperString(sentence1), SuperString.createWordSuperString(sentence2));
//				text = text + "(时间:" + time + ")";
//					
//				ed = new XiatianEditDistance2();
//				text = text + "\n夏氏编辑距离算法结果2：";
//				time = System.currentTimeMillis();
//				text = text + "\n字符串处理:" + ed.getEditDistance(SuperString.createCharSuperString(sentence1), SuperString.createCharSuperString(sentence2));
//				time = System.currentTimeMillis() - time;
//				text = text + "(时间:" + time + ")";				
//				time = System.currentTimeMillis();
//				text = text + "\n词语处理:" + ed.getEditDistance(SuperString.createWordSuperString(sentence1), SuperString.createWordSuperString(sentence2));
//				text = text + "(时间:" + time + ")";
								
				text = text + "\n________________________________\n" + result.getText();
     			result.setText(text);
			}
        	
        });
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        northPanel.add(mainPanel);        
        
        return fullPanel;
    }
    
    public static void main(String[] args) {
		EditDistance ed1 = new StandardEditDistance();
		EditDistance ed2 = new GregorEditDistance();
		EditDistance ed3 = new XiatianEditDistance();
		SuperString<CharEditUnit> s1 = SuperString.createCharSuperString("abcdefghijkl");
		SuperString<CharEditUnit> s2 = SuperString.createCharSuperString("qwerabcjkls1");

		long time = new Date().getTime();
		for(int i=0; i<100; i++){
			ed1.getEditDistance(s1, s2);			
		}
		System.out.println((new Date().getTime()-time));
		
		time = new Date().getTime();
		for(int i=0; i<100; i++){
			ed2.getEditDistance(s1, s2);			
		}
		System.out.println((new Date().getTime()-time));

		time = new Date().getTime();
		for(int i=0; i<100; i++){
			ed3.getEditDistance(s1, s2);			
		}
		System.out.println((new Date().getTime()-time));
	}
}
