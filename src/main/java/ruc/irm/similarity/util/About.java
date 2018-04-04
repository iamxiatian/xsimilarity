package ruc.irm.similarity.util;

import com.google.common.io.Resources;

import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 关于xsimilarity项目的说明信息
 *
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class About extends JFrame {
    private static final long serialVersionUID = -2307582155443587993L;

    public static JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JTextPane editorPane = new JTextPane();
        editorPane.setEditable(false);
        //让长文本自动换行
        editorPane.setEditorKit(new StyledEditorKit());
        editorPane.setContentType("text/html");
        try {
            URL url = Resources.getResource("about.html");//可以用html格式文件做你的帮助系统了
            editorPane.setPage(url);
        } catch (IOException e1) {
            editorPane.setText(e1.getMessage());
        }
        //editorPane.setText("<html><body>个人主页：<a href='xiatian.irm.cn'>http://xiatian.irm.cn/</a></body></html>");


        mainPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        return mainPanel;
    }

    public About() {
        this.setTitle("关于XSimilarity");

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(600, 400));
        this.getContentPane().add(createPanel());
        this.pack();
    }

    public static void main(String[] args) {
        new About().setVisible(true);
    }
}
