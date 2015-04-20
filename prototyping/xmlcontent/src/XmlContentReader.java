//import com.sun.xml.internal.ws.developer.JAXBContextFactory;
//import se.spaced.jaxb.Content;
//import se.spaced.jaxb.Itemdef;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBElement;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.bind.ValidationEvent;
//import javax.xml.bind.util.JAXBSource;
//import javax.xml.bind.util.ValidationEventCollector;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.InputStream;
//
//public class XmlContentReader {
//
//	public static void main(String[] args) throws JAXBException, FileNotFoundException {
//		JAXBContext jc = JAXBContext.newInstance("se.spaced.jaxb");
//		Unmarshaller unmarshaller = jc.createUnmarshaller();
//		ValidationEventCollector validationEventCollector = new ValidationEventCollector();
//		unmarshaller.setEventHandler(validationEventCollector);
//		InputStream resources = XmlContentReader.class.getResourceAsStream("/example.xml");
//		Content content = (Content) unmarshaller.unmarshal(resources);
//		for (ValidationEvent event : validationEventCollector.getEvents()) {
//			System.out.println("event: " + event);
//		}
//		for (Object o : content.getEffectOrSpell()) {
//			System.out.println("value: " + o);
//		}
//		System.out.println("" + content);
//	}
//}
