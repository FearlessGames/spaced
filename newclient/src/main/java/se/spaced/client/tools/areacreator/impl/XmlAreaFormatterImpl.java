package se.spaced.client.tools.areacreator.impl;

import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.spaced.client.tools.areacreator.XmlAreaFormatter;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.xml.SharedXStreamRegistry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Singleton
public class XmlAreaFormatterImpl implements XmlAreaFormatter {
	private final DecimalFormat formatter;
	private final XStream xStream = new XStream(new DomDriver());

	public XmlAreaFormatterImpl() {
		new SharedXStreamRegistry().registerDefaultsOn(xStream);
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		formatter = new DecimalFormat("#0.000", otherSymbols);
	}

	@Override
	public String getRotationXmlTag(AreaPoint areaPoint) {
		return String.format("<rotation x=\"%s\" y=\"%s\" z=\"%s\" w=\"%s\"/>",
				formatter.format(areaPoint.getRotation().getX()),
				formatter.format(areaPoint.getRotation().getY()),
				formatter.format(areaPoint.getRotation().getZ()),
				formatter.format(areaPoint.getRotation().getW()));
	}

	@Override
	public String getLocationXmlTag(AreaPoint areaPoint) {
		return String.format("<point x=\"%s\" y=\"%s\" z=\"%s\" />",
				formatter.format(areaPoint.getPoint().getX()),
				formatter.format(areaPoint.getPoint().getY()),
				formatter.format(areaPoint.getPoint().getZ()));
	}

	@Override
	public String getXml(Object object) {
		return xStream.toXML(object);
	}
}
