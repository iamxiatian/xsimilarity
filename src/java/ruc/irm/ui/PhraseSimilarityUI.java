package ruc.irm.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ruc.irm.similarity.phrase.PhraseSimilarity;

/**
 * 短语相似度的调用演示界面
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class PhraseSimilarityUI {

	/**
	 * 短语相似度的演示面板
	 * 
	 * @return
	 */
	public static JPanel createPanel() {
		// 声明总的大面板, fullPanel包括一个NorthPanel和一个centerPanel
		JPanel fullPanel = new JPanel();
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

		northPanel.setLayout(new GridLayout(1, 1));
		// northPanel.add(createWordPanel());
		// northPanel.add(createCilinPanel());

		// 以下加入northPanel中的第一个面板
		final JTextField field1 = new JTextField("");
		final JTextField field2 = new JTextField("");
		field1.setColumns(50);
		field2.setColumns(50);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(3, 1));

		JPanel linePanel = new JPanel();
		linePanel.add(new JLabel("短语1:"));
		linePanel.add(field1);
		mainPanel.add(linePanel);

		linePanel = new JPanel();
		linePanel.add(new JLabel("短语2:"));
		linePanel.add(field2);
		mainPanel.add(linePanel);

		linePanel = new JPanel();
		JButton goButton = new JButton("计算相似度");
		linePanel.add(goButton);
		mainPanel.add(linePanel);
		goButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String phrase1 = field1.getText();
				String phrase2 = field2.getText();
				String text = "[" + phrase1 + "]与[" + phrase2 + "]的相似度为:";
				text = text + new PhraseSimilarity().getSimilarity(phrase1, phrase2);
				// text = text + "\n\n" + result.getText();
				result.setText(text);
			}

		});
		mainPanel.setBorder(BorderFactory.createEtchedBorder());
		northPanel.add(mainPanel);

		return fullPanel;
	}
}
