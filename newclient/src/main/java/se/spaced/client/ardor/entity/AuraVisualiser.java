package se.spaced.client.ardor.entity;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.view.entity.VisualEntity;

import java.util.Map;

public class AuraVisualiser {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Map<AuraVisualiserEvent, AuraTrigger> mapping = Maps.newEnumMap(AuraVisualiserEvent.class);

	public void addMapping(AuraVisualiserEvent event, AuraTrigger auraTrigger) {
		mapping.put(event, auraTrigger);
	}

	public void fireEvent(AuraVisualiserEvent event, VisualEntity visualEntity) {
		final AuraTrigger auraTrigger = mapping.get(event);
		if (auraTrigger != null) {
			auraTrigger.trigger(visualEntity);
		} else {
			log.warn("Fired event with no mapping: {}", event);
		}
	}
}
