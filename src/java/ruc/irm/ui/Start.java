package ruc.irm.ui;

import java.awt.Container;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import ruc.irm.similarity.sentence.SegmentProxy;
import ruc.irm.similarity.util.About;

/**
 * 相似度计算软件包演示启动类
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class Start extends JFrame {

	private static final long serialVersionUID = 85744461208L;

	public Start() {
		this.setTitle("相似度计算演示程序");
		this.setSize(420, 700);
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

		Container contentPane = this.getContentPane();
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("词语", WordSimlarityUI.createPanel());
		tabbedPane.add("短语", PhraseSimilarityUI.createPanel());
		tabbedPane.add("句子", SentenceSimilarityUI.createPanel());
		// tabbedPane.add("文本", WordSimlarityUI.createPanel());
		tabbedPane.add("词法分析", SegmentProxy.createPanel());
		tabbedPane.add("义原树", SememeTreeUI.createPanel());
		tabbedPane.add("情感分析", TendencyUI.createPanel());
		tabbedPane.add("关于", About.createPanel());
		JScrollPane scrollPane = new JScrollPane(tabbedPane);
		contentPane.add(scrollPane);
		
		this.pack();
		setExtendedState(MAXIMIZED_BOTH);
	}

	public static void InitGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}

	public static void main(String[] args) {
		//JFrame.setDefaultLookAndFeelDecorated(true);
		//解决字体在Ubuntu中显示有乱码的问题
		InitGlobalFont(new Font("Microsoft YaHei", Font.TRUETYPE_FONT, 12));
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				Start w = new Start();
				w.setVisible(true);
			}
		});
	}

}
