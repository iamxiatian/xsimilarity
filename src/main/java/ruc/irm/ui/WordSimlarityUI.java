package ruc.irm.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import ruc.irm.similarity.word.CharBasedSimilarity;
import ruc.irm.similarity.word.cilin.Cilin;
import ruc.irm.similarity.word.cilin.CilinDb;
import ruc.irm.similarity.word.hownet2.concept.BaseConceptParser;
import ruc.irm.similarity.word.hownet2.concept.Concept;
import ruc.irm.similarity.word.hownet2.concept.LiuConceptParser;
import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;

/**
 * 词语相似度计算面板
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class WordSimlarityUI extends JFrame {

    private static final long serialVersionUID = 632985744461208L;

    public WordSimlarityUI() {
        this.setTitle("同义词词林演示程序");
        this.setSize(400, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // //////////////////////////////////
        // add menu
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.add(new JMenuItem("Exit"));

        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        helpMenu.add(new JMenuItem("Help"));

        // ///////////////////////////////////
        // add toolbar
        JToolBar toolBar = new JToolBar();
        this.add(toolBar, BorderLayout.PAGE_START);
        toolBar.add(new JLabel("选项:"));

        // String iconFile = getClass().getPackage().getName().replaceAll("\\.",
        // "/") + "/house_go.png";
        // ImageIcon icon = new
        // ImageIcon(ClassLoader.getSystemResource(iconFile));
        // goButton = new JButton("GO", icon);
        // goButton = new JButton("相似度计算");
        // goButton.addActionListener(this);
        // toolBar.add(goButton);

        Container contentPane = this.getContentPane();

        JScrollPane scrollPane = new JScrollPane(createPanel());
        contentPane.add(scrollPane);

        // this.pack();
    }

    /**
     * 词语相似度的演示面板
     * 
     * @return
     */
    public static JPanel createPanel() {
        // 声明总的大面板, fullPanel包括一个NorthPanel和一个centerPanel
        final JPanel fullPanel = new JPanel();
        fullPanel.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        fullPanel.add(northPanel, "North");

        // centerPanel包括了一个文本框
        JPanel centerPanel = new JPanel();
        fullPanel.add(centerPanel, "Center");
        centerPanel.setLayout(new BorderLayout());
        final JTextArea result = new JTextArea();
        // result.setFont(new Font("宋体", Font.PLAIN, 16));
        result.setLineWrap(true);
        JScrollPane centerScrollPane = new JScrollPane(result);
        centerPanel.add(centerScrollPane, "Center");

        northPanel.setLayout(new GridLayout(2, 1));
        // northPanel.add(createWordPanel());
        // northPanel.add(createCilinPanel());

        // 以下加入northPanel中的第一个面板
        final JTextField wordField1 = new JTextField("电动车");
        final JTextField wordField2 = new JTextField("助力车");
        wordField1.setColumns(50);
        wordField2.setColumns(50);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1));

        JPanel linePanel = new JPanel();
        linePanel.add(new JLabel("词语1:"));
        linePanel.add(wordField1);
        mainPanel.add(linePanel);

        linePanel = new JPanel();
        linePanel.add(new JLabel("词语2:"));
        linePanel.add(wordField2);
        mainPanel.add(linePanel);

        linePanel = new JPanel();
        JButton loadButton = new JButton("加载自定义概念文件");
        linePanel.add(loadButton);
        JButton goButton = new JButton("计算词语相似度");
        linePanel.add(goButton);
        mainPanel.add(linePanel);
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append("[Help]概念词典格式举例：\n");
                sb.append("<?xml version=\"1.0\"?>\n");
                sb.append("<concepts>\n");
                sb.append("  <!--\n");
                sb.append("  <c w=\"汉语词语\" p=\"词性，取值为：V|N|ADJ|NUM|PREP等\" d=\"对应的义原形式的定义\"/>\n");
                sb.append("  -->\n");
                sb.append("  <c w=\"三聚氰胺\" p=\"N\" d=\"material|材料,#drinks|饮品\"/>\n");
                sb.append("  <c w=\"山寨\" p=\"V\" d=\"pretend|假装,content=RegardAs|当作\"/>\n");
                sb.append("</concepts>");
                result.setText(sb.toString());
                result.setCaretPosition(0);

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("选择要打开的概念文件");
                chooser.showOpenDialog(fullPanel);
                File choosedFile = chooser.getSelectedFile();
                if (choosedFile != null) {
                    try {
                        BaseConceptParser.load(choosedFile);
                        result.setText("加载完毕.\n------------------------------\n" + result.getText());
                    } catch (IOException e1) {
                        result.setText(e1.getMessage() + "\n------------------------------\n" + result.getText());
                        result.setCaretPosition(0);
                    }
                }
            }

        });
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String word1 = wordField1.getText();
                String word2 = wordField2.getText();
                String text = "[" + word1 + "]与[" + word2 + "]的相似度为:";
                text = text + "\n词林:" + Cilin.getInstance().getSimilarity(word1, word2);
                text = text + "\n刘群:" + LiuConceptParser.getInstance().getSimilarity(word1, word2);
                text = text + "\n夏天:" + XiaConceptParser.getInstance().getSimilarity(word1, word2);
                text = text + "\n字面:" + new CharBasedSimilarity().getSimilarity(word1, word2);
                text += "\n__________________________________\n";
                text += result.getText();
                result.setText(text);
                result.setCaretPosition(0);
            }

        });
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        northPanel.add(mainPanel);

        // 加入northPanel中的第2部分
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1));

        final JTextField cilinField = new JTextField("中国");
        cilinField.setColumns(50);
        linePanel = new JPanel();
        linePanel.add(new JLabel("词语或编码:"));
        linePanel.add(cilinField);
        mainPanel.add(linePanel);

        linePanel = new JPanel();
        JButton viewWordsInCodeButton = new JButton("该编码下的词林词语");
        JButton viewCodeButton = new JButton("该词语的词林编码");
        JButton viewConceptButton = new JButton("知网概念");
        viewWordsInCodeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = "具有编码" + cilinField.getText() + "的词语有:" + CilinDb.getInstance().getCilinWords(cilinField.getText());
                text += "\n__________________________________\n";
                text += result.getText();
                result.setText(text);
                result.setCaretPosition(0);
            }

        });
        viewCodeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = cilinField.getText() + "的编码为:" + CilinDb.getInstance().getCilinCoding(cilinField.getText());
                text += "\n__________________________________\n";
                text += result.getText();
                result.setText(text);
                result.setCaretPosition(0);
            }

        });
        viewConceptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = "";
                Collection<Concept> concepts = XiaConceptParser.getInstance().getConcepts(cilinField.getText());
                if (concepts == null || concepts.size() == 0) {
                    text = "自动组合概念结果:\n";
                    concepts = XiaConceptParser.getInstance().autoCombineConcepts(cilinField.getText(), null);
                }
                int i = 1;
                for (Concept c : concepts) {
                    text = text + "概念" + i + ": " + c.toString() + "\n\n";
                    i++;
                }

                text += "__________________________________\n";
                text += result.getText();
                result.setText(text);
                result.setCaretPosition(0);
            }

        });

        linePanel.add(viewWordsInCodeButton);
        linePanel.add(viewCodeButton);
        linePanel.add(viewConceptButton);
        mainPanel.add(linePanel);
        northPanel.add(mainPanel);

        return fullPanel;
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                WordSimlarityUI w = new WordSimlarityUI();
                w.setVisible(true);
            }
        });
    }

}
