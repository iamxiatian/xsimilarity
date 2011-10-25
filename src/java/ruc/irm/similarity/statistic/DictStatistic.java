package ruc.irm.similarity.statistic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;

/**
 * 用于统计分词词典文件中的概念出现数量
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class DictStatistic {
	/**
	 * 从指定的xml文件加载词典文件
	 * @param xmlFile
	 * @param gzCompressed 是否再用gz格式对词典进行了压缩
	 * @return
	 */
	public void testFromXml(String xmlFile, boolean gzCompressed) {
		File file = new File(xmlFile);
		if (!file.canRead()){
			System.out.println("无法读取文件:" + xmlFile);
			return;// fail while opening the file
		}
		int count = 0, conceptCount=0;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream input = null;
		try {			
			if(gzCompressed){
				input = new GZIPInputStream(new FileInputStream(file));
			}else{
				input = new FileInputStream(file);
			}			
			XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(input);
			while (xmlEventReader.hasNext()) {
				XMLEvent event = xmlEventReader.nextEvent();
				
				if (event.isStartElement()) {					
					StartElement startElement = event.asStartElement();					
					if(startElement.getName().toString().equals("table")){
						String head = startElement.getAttributeByName(QName.valueOf("head")).getValue();						
						while (xmlEventReader.hasNext()) {
							XMLEvent itemEvent = xmlEventReader.nextEvent();
							if(itemEvent.isStartElement()){
								StartElement itemStartElement = itemEvent.asStartElement();
								if(!itemStartElement.getName().toString().equals("item")) continue;
								String word = itemStartElement.getAttributeByName(QName.valueOf("word")).getValue();
								word = head + word;
								if(XiaConceptParser.getInstance().isConcept(word)){
									conceptCount++;
								}
								count++;
								if(count%1000==0){
									System.out.println("process words " + count + "...");
								}
							}
						}
					}					
				}
			}
			input.close();
			System.out.println(count + "\t" + conceptCount);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
