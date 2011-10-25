package ruc.irm.similarity.word.hownet.concept;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ruc.irm.similarity.util.TraverseEvent;

/**
 * 实现遍历加载概念信息到概念表中, 概念词典的组织以知网导出的格式为标准，格式如下：<br/>
 * 阿斗                	N    	human|人,ProperName|专,past|昔<br/>
 * 阿爸                	N    	human|人,family|家,male|男<br/>
 * 即： &lt;概念&gt; &lt;空格或者跳格&gt; &lt;词性&gt; &lt;空格或者跳格&gt; &lt;定义&gt;"
 * <br/>
 * 概念保存到数组中，没有保存到Map中，可以降低对内存空间的使用
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * @deprecated
 */
public class ConceptDictTraverseEvent implements TraverseEvent<String> {
	private List<Concept> conceptList = null;
	
	public ConceptDictTraverseEvent(){
		conceptList = new ArrayList<Concept>();
	}
	
	public Concept[] getConcepts(){
		Concept[] concepts = conceptList.toArray(new Concept[conceptList.size()]);
		Arrays.sort(concepts);
		return concepts;
	}
	
	/**
	 * 读取概念词典中的一行，并进行解析处理
	 */
	public boolean visit(String line) {
		String word = null;
		String pos = null;
		String define = "";
		char ch;
		
		//以符号//开始的是注释行
		if(line.startsWith("//")){
			return true;
		}
		
		int lastPosition = 0;	//最近一次处理内容的有意义的开始位置
		int processFlag = 0;	//当前处理部分的标志 0：处理word； 1：词性；2：定义
		//解析出一行中的概念各项数据		
		loop: for (int position = 0; position < line.length(); position++) {
			ch = line.charAt(position);
			
			if ((ch == ' ') || (ch == '\t') || (position==(line.length()-1))) {
				String item = line.substring(lastPosition, (position==(line.length()-1))?(position+1):position);
				switch(processFlag){				
				case 0:
					word = item;
					processFlag++;
					break;
				case 1:
					pos = item;
					processFlag++;
					break;
				case 2:					
					//define = item;
					//processFlag++;
					define = line.substring(lastPosition).trim();					
					break loop;
				case 3:
					System.out.println(line);
					break;
				}				
				
				for( ;(position < line.length()); position++){
					ch = line.charAt(position);
					if ((ch != ' ') && (ch != '\t')) {
						lastPosition = position;
						break;
					}
				}
					
			}
		}
		conceptList.add(new Concept(word, pos, define));
		return true;
	}
	
	public void saveToXML(File xmlFile) throws Exception{
		String conceptFile = getClass().getPackage().getName().replaceAll("\\.", "/") + "/concept.dat";
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(conceptFile);
		BufferedReader in = new BufferedReader(new InputStreamReader(input,	"utf8"));
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder=factory.newDocumentBuilder(); 
		Document document=builder.newDocument();
		Element root=document.createElement("concepts"); 
		document.appendChild(root); 
		
		String line = null;

		while ((line = in.readLine()) != null) {
			saveLineToXML(document, root, line);
		}

		input.close();
		in.close();
		
		TransformerFactory tf=TransformerFactory.newInstance(); 
		Transformer transformer=tf.newTransformer(); 
		DOMSource source=new DOMSource(document); 
		transformer.setOutputProperty(OutputKeys.ENCODING,"utf8"); 
		transformer.setOutputProperty(OutputKeys.INDENT,"yes"); 
		PrintWriter pw=new PrintWriter(new FileOutputStream(xmlFile)); 
		StreamResult result=new StreamResult(pw); 
		transformer.transform(source,result); 
	}
	
	
	/**
	 * 读取概念词典中的一行，并进行解析处理
	 */
	private boolean saveLineToXML(Document document, Element root, String line) {
		String word = null;
		String pos = null;
		String define = "";
		char ch;
		
		//以符号//开始的是注释行
		if(line.startsWith("//")){
			return true;
		}
		
		int lastPosition = 0;	//最近一次处理内容的有意义的开始位置
		int processFlag = 0;	//当前处理部分的标志 0：处理word； 1：词性；2：定义
		//解析出一行中的概念各项数据		
		loop: for (int position = 0; position < line.length(); position++) {
			ch = line.charAt(position);
			
			if ((ch == ' ') || (ch == '\t') || (position==(line.length()-1))) {
				String item = line.substring(lastPosition, (position==(line.length()-1))?(position+1):position);
				switch(processFlag){				
				case 0:
					word = item;
					processFlag++;
					break;
				case 1:
					pos = item;
					processFlag++;
					break;
				case 2:					
					//define = item;
					//processFlag++;
					define = line.substring(lastPosition).trim();					
					break loop;
				case 3:
					System.out.println(line);
					break;
				}				
				
				for( ;(position < line.length()); position++){
					ch = line.charAt(position);
					if ((ch != ' ') && (ch != '\t')) {
						lastPosition = position;
						break;
					}
				}
					
			}
		}
		
		Element e = document.createElement("c");
		e.setAttribute("w", word);
		e.setAttribute("p", pos);
		e.setAttribute("d", define);
		root.appendChild(e);
		return true;
	}
	
	public static void main(String[] args) throws Exception {
	  new ConceptDictTraverseEvent().saveToXML(new File("/home/xiatian/Desktop/concept.xml"));
  }
		
}
