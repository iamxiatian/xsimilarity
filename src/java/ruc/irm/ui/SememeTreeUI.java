package ruc.irm.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import ruc.irm.similarity.word.hownet2.sememe.Sememe;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * 用于显示义原层次树的Tree组件，仅仅为了管理和查看方便，与相似度计算算法无关。 所有内容读自sememe.xml.gz压缩文件。
 * 整个面板由两大部分构成，上面（North）为输入查询的文本框和查询按钮，下面（Center）为JSplitPane，JSplitPane又由
 * JTextArea和JTree组成，JTextArea用于显示查询结果，JTree用于显示层次关系
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class SememeTreeUI extends JFrame {
    private static final long serialVersionUID = 3270193057395104087L;

    public static JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        try {
            final DefaultMutableTreeNode root = load();
            final JTree jtree = new JTree(root);
            JScrollPane treeScrollPane = new JScrollPane(jtree);
            // mainPanel.add(treeScrollPane, BorderLayout.SOUTH);

            JPanel queryPanel = new JPanel();
            // TODO加入查询义原路径的处理
            final JTextField queryField = new JTextField();
            queryField.setColumns(50);
            queryPanel.add(new JLabel("输入义原:"));
            queryPanel.add(queryField);
            JButton queryButton = new JButton("查询");
            queryPanel.add(queryButton);
            mainPanel.add(queryPanel, BorderLayout.NORTH);

            final JTextArea editor = new JTextArea();
            editor.setLineWrap(true);
            editor.setForeground(Color.RED);
            editor.setRows(3);
            JScrollPane editorScrollPane = new JScrollPane(editor);

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScrollPane, treeScrollPane);
            splitPane.setDividerSize(2);
            mainPanel.add(splitPane, BorderLayout.CENTER);
            // mainPanel.add(editorScrollPane, BorderLayout.CENTER);

            queryButton.addActionListener(new ActionListener() {

                /**
                 * 递归查询符合条件的结果，并把所有结果存入selectedNodes中
                 * 
                 * @param node
                 * @param selectedNodes
                 */
                private void query(DefaultMutableTreeNode node, Collection<DefaultMutableTreeNode> selectedNodes) {
                    String queryString = queryField.getText();
                    String text = node.getUserObject().toString();
                    if (text.startsWith(queryString) || text.endsWith(queryString)) {
                        selectedNodes.add(node);
                    }

                    if (node.getChildCount() == 0)
                        return;

                    for (DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getFirstChild(); child != null; child = child
                            .getNextSibling()) {
                        query(child, selectedNodes);
                    }
                }

                /**
                 * 关闭所有树的节点
                 * 
                 * @param parent
                 */
                @SuppressWarnings("unchecked")
                private void collapseAll(TreePath parent) {
                    TreeNode node = (TreeNode) parent.getLastPathComponent();

                    if (node.getChildCount() > 0) {
                        for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
                            TreeNode child = e.nextElement();
                            TreePath path = parent.pathByAddingChild(child);
                            collapseAll(path);

                        }
                    }
                    jtree.collapsePath(parent);
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    String queryString = queryField.getText();
                    if (queryString.trim().equals("")) {
                        editor.setText("请输入查询义原");
                        return;
                    }

                    Collection<DefaultMutableTreeNode> selectedNodes = new ArrayList<DefaultMutableTreeNode>();
                    query(root, selectedNodes);
                    if (selectedNodes.size() == 0) {
                        editor.setText("查询结果：无匹配记录");
                        return;
                    }

                    jtree.setSelectionPath(null);
                    collapseAll(new TreePath(((DefaultTreeModel) jtree.getModel()).getPathToRoot(root)));

                    StringBuilder sb = new StringBuilder("共有" + selectedNodes.size() + "条匹配义原：\n");

                    for (DefaultMutableTreeNode node : selectedNodes) {
                        TreePath path = new TreePath(((DefaultTreeModel) jtree.getModel()).getPathToRoot(node));
                        jtree.expandPath(path);
                        jtree.addSelectionPath(path);
                        Object[] objs = path.getPath();
                        for (int i = objs.length - 1; i > 0; i--) {
                            if (i < objs.length - 1) {
                                sb.append(" -> ");
                            }
                            sb.append(objs[i].toString());
                        }
                        sb.append("\n\n");
                    }
                    editor.setText(sb.toString());
                    editor.setCaretPosition(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainPanel;
    }

    private static void createNodes(Multimap<String, Sememe> sememes, DefaultMutableTreeNode top, String parentId) {
        Collection<Sememe> children = sememes.get(parentId);
        for (Sememe child : children) {
            String text = child.getEnWord() + "|" + child.getCnWord();
            if (child.getDefine() != null) {
                text += " " + child.getDefine();
            }
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(text);
            top.add(childNode);
            createNodes(sememes, childNode, child.getId());
        }
    }

    /**
     * 加载义原到Multimap中，并创建树的节点
     * 
     * @return
     * @throws IOException
     */
    private static DefaultMutableTreeNode load() throws IOException {
        /**
         * 存放parentId和该parentId所隶属的义原，即Key为parentId，value为子义原
         */
        Multimap<String, Sememe> sememes = ArrayListMultimap.create();

        String sememeFile = Sememe.class.getPackage().getName().replaceAll("\\.", "/") + "/sememe.xml.gz";
        InputStream input = Sememe.class.getClassLoader().getResourceAsStream(sememeFile);
        input = new GZIPInputStream(input);

        System.out.println("[" + SememeTreeUI.class.getSimpleName() + "]loading sememes into sememe tree...");
        long time = System.currentTimeMillis();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(input);

            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if (startElement.getName().toString().equals("sememe")) {
                        String en = startElement.getAttributeByName(QName.valueOf("en")).getValue();
                        String cn = startElement.getAttributeByName(QName.valueOf("cn")).getValue();
                        String id = startElement.getAttributeByName(QName.valueOf("id")).getValue();
                        Attribute attr = startElement.getAttributeByName(QName.valueOf("define"));
                        String define = (attr == null ? null : attr.getValue());

                        Sememe sememe = new Sememe(id, en, cn, define);
                        int pos = id.lastIndexOf("-");
                        String parentId = "root";
                        if (pos > 0) {
                            parentId = id.substring(0, pos);
                        }
                        sememes.put(parentId, sememe);
                    }
                }
            }
            input.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
        time = System.currentTimeMillis() - time;
        System.out.println("complete. time elapsed: " + (time / 1000) + "s");

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("知网义原层次关系树");
        createNodes(sememes, top, "root");
        return top;
    }

    public SememeTreeUI() {
        this.setTitle("义原树演示程序");
        this.setLocationRelativeTo(null);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.getContentPane().add(createPanel());
        this.pack();
        setExtendedState(MAXIMIZED_BOTH);
    }

    public static void main(String[] args) {
        new SememeTreeUI().setVisible(true);
    }

}
