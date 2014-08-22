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

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

/**
 * 对词法分析程序的封装代理，目前内部封装了对Ictclas4j（夏天改进版）的调用<br/>
 * 为方便演示程序快速启动，对Segment的调用采用了单例模式，实现需要时的延迟加载。
 *
 * @CHANGE 2014/04/04 采用Ansj词法分析器取代Ictclas4j-summer version
 *
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class SegmentProxy {

    public static class Word {
        /**
         * 词语内容
         */
        private String word;
        /**
         * 词语词性代号
         */
        private String pos;

        public Word(String word, String pos) {
            this.word = word;
            this.pos = pos;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }
    }

    public static List<Word> segment(String sentence) {
        List<Word> results = new ArrayList<Word>();
        List<Term> terms = ToAnalysis.parse(sentence);
        new NatureRecognition(terms).recognition();

        for (Term term : terms) {
            results.add(new Word(term.getName(), term.getNatrue().natureStr));
        }

        return results;
    }

    public static String getSegmentedString(String sentence) {
        List<Word> words = segment(sentence);
        StringBuilder sb = new StringBuilder();
        for (Word word : words) {
            sb.append(word.getWord() + "/" + word.getPos()).append(" ");
        }
        return sb.toString();
    }

    public static JPanel createPanel() {
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
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String sentence = senField.getText();
                String text = "[" + sentence + "]的词法分析结果为:";

                text = text + "\n" + getSegmentedString(sentence);
                text = text + "\n________________________________\n" + result.getText();
                result.setText(text);
            }

        });
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        northPanel.add(mainPanel);

        return fullPanel;
    }
}
