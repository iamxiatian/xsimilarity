package ruc.irm.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ruc.irm.tendency.word.HownetWordTendency;

/**
 * 测试词语倾向性的用户调用演示界面
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class TendencyUI extends JFrame {
    private static final long serialVersionUID = -3976827963973640651L;

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
        final JTextField wordField = new JTextField("恶心");    
        wordField.setColumns(40);
        
        JPanel mainPanel = new JPanel();        
        mainPanel.setLayout(new GridLayout(2, 1));        
                
        JPanel linePanel = new JPanel();
        linePanel.add(new JLabel("输入词语:"));        
        linePanel.add(wordField); 
        mainPanel.add(linePanel);
        
        linePanel = new JPanel();
        JButton goButton = new JButton("计算词语倾向");
        linePanel.add(goButton);
        mainPanel.add(linePanel);
        goButton.addActionListener(new ActionListener(){
            HownetWordTendency tendency = new HownetWordTendency();
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordField.getText();
                double positive = tendency.getSentiment(word, HownetWordTendency.POSITIVE_SEMEMES);
                double negative = tendency.getSentiment(word, HownetWordTendency.NEGATIVE_SEMEMES);
                String text = "[" + word + "]的倾向分析结果为:" ;
                
                text = text + "\n正面接近程度=" + positive;
                text = text + "\n负面接近程度=" + negative;
                text = text + "\n倾向性=" + (positive - negative);                
                text = text + "\n________________________________\n" + result.getText();
                result.setText(text);
                result.setCaretPosition(0);
            }
            
        });
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        northPanel.add(mainPanel);        
        
        return fullPanel;
    }

    public TendencyUI(){
        this.setTitle("词语倾向性演示");
        this.setSize(420, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(createPanel());
    }
    
    public static void main(String[] args) {
        new TendencyUI().setVisible(true);
    }
}
