package ruc.irm.similarity.statistic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import ruc.irm.similarity.word.hownet2.concept.XiaConceptParser;



public class LCMC {
	
	public void countUnConceptWords(File xmlFile) throws Exception{
		int totalCount = 0, conceptCount = 0;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream input = null;
		input = new FileInputStream(xmlFile);
		XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(input);
		while (xmlEventReader.hasNext()) {
			XMLEvent event = xmlEventReader.nextEvent();
			
			if (event.isStartElement()) {					
				StartElement startElement = event.asStartElement();
				//如果是word开始
				if(startElement.getName().toString().equals("w")){
					String word = xmlEventReader.getElementText();
					totalCount++;
					if(XiaConceptParser.getInstance().isConcept(word)){
						conceptCount++;
					}
				}					
			}
		}//
		input.close();
		System.out.println(totalCount + "\t" + conceptCount);
	}
	
	public static void main(String[] args) throws Exception {
		LCMC lcmc = new LCMC();
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_A.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_B.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_C.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_D.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_E.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_F.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_G.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_H.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_J.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_K.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_L.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_M.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_N.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_P.XML"));
		lcmc.countUnConceptWords(new File("./db/lcmc/LCMC_R.XML"));
	}
}
