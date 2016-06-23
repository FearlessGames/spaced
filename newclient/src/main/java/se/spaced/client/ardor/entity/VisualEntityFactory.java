package se.spaced.client.ardor.entity;

import com.ardor3d.extension.ui.UILabel;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.ui.text.BMFont;
import com.ardor3d.ui.text.BMText;
import com.ardor3d.util.resource.ResourceSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.SpacedResource;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.io.StreamLocator;
import se.spaced.client.ardor.ui.api.FontApi;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.Relation;
import se.spaced.client.model.animation.AnimationClipCache;
import se.spaced.client.model.animation.AnimationModel;
import se.spaced.client.view.entity.VisualEntity;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.playback.BufferedMovementPlayer;
import se.spaced.shared.xml.XmlIOException;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

@Singleton
public class VisualEntityFactory {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final XmoEntityFactory xmoEntityFactory;
	private final InteractionColors interactionColors;
	private final AnimationClipCache animationCache;
	private BMFont font;
	private final AuraVisualiser auraVisualiser;
	private final StreamLocator streamLocator;
	private FontApi fontApi;

	@Inject
	public VisualEntityFactory(
			XmoEntityFactory xmoEntityFactory,
			AnimationClipCache animationCache,
			AuraVisualiser auraVisualiser, StreamLocator streamLocator, FontApi fontApi) {
		this.xmoEntityFactory = xmoEntityFactory;
		this.animationCache = animationCache;
		this.auraVisualiser = auraVisualiser;
		this.streamLocator = streamLocator;
		this.fontApi = fontApi;
		interactionColors = new InteractionColors();
	}

	public VisualEntity create(ClientEntity entity, Relation relation) {
		Node entityNode = new Node(entity.getName());
		entityNode.setUserData(entity.getPk());
		AppearanceData appearanceData = entity.getAppearanceData();
		entityNode.setScale(appearanceData.getScale());

		XmoEntity xmoEntity = createXmoEntity(entity.getName(), appearanceData);

		Spatial namePlate = createBMTextNamePlate(entity.getName(), xmoEntity.getMetaNode("nameplate").getPosition(), relation);
		//Spatial namePlate = createUITextNamePlate(entity.getName(), xmoEntity.getMetaNode("nameplate").getPosition(), relation);

		entityNode.attachChild(namePlate);

		BufferedMovementPlayer<AnimationState> movementPlayer = entity.getMovementPlayer();

		BasicVisualEntity visualEntity = new BasicVisualEntity(
				interactionColors,
				entityNode,
				xmoEntity,
				auraVisualiser,
				entity);

		if (xmoEntity.getSkin() != null) {
			AnimationState state = movementPlayer != null ? movementPlayer.state() : AnimationState.IDLE;
			visualEntity.setAnimationModel(new AnimationModel(animationCache,
					xmoEntity.getModel(),
					xmoEntity.getSkin(),
					xmoEntity.getAnimationMappingKey(),
					state));
			visualEntity.setupAttachmentPoints();
		} else {
			entityNode.attachChild(xmoEntity.getModel());
		}
		visualEntity.setPositionData(entity.getPosition(), entity.getRotation());
		return visualEntity;
	}

	private Spatial createUITextNamePlate(String name, SpacedVector3 position, Relation relation) {
		UILabel uiLabel = new UILabel(name);
		uiLabel.setFontStyles(fontApi.createFontStyle("eras", 16, false, false));

		if (relation == Relation.HOSTILE) {
			uiLabel.setForegroundColor(new ColorRGBA(1, 0, 0, 1));
		} else {
			uiLabel.setForegroundColor(new ColorRGBA(0, 1, 0, 1));
		}
		uiLabel.setTranslation(position);
		return uiLabel;
	}


	public XmoEntity createXmoEntity(String name, AppearanceData appearanceData) {
		try {
			String modelName = appearanceData.getModelName();
			if (!modelName.contains(".")) {
				log.warn("Implicit .xmo extension for model with name " + modelName);
				modelName += ".xmo";
			}
			return xmoEntityFactory.create(modelName, name);
		} catch (XmlIOException e) {
			log.error("Failed to load appearanceData for model " + appearanceData.getModelName(), e);
			return null;
		}
	}

	private Spatial createBMTextNamePlate(String name, SpacedVector3 offset, Relation relation) {
		BMText nameplate = createText(name, offset);
		if (relation == Relation.HOSTILE) {
			nameplate.setTextColor(1, 0, 0, 1);
		} else {
			nameplate.setTextColor(0, 1, 0, 1);
		}
		return nameplate;
	}

	private BMText createText(String text, SpacedVector3 offset) {
		BMFont font = getFont();
		if (font == null) {
			return null;
		}

		BMText textMesh = new BMText(text, text, font, BMText.Align.Center, BMText.Justify.Center);
		textMesh.setFontScale(1.0);
		textMesh.setAutoFade(BMText.AutoFade.Off);
		textMesh.setAutoScale(BMText.AutoScale.CapScreenSize);
		textMesh.setAutoRotate(true);
		textMesh.setTranslation(offset);
		textMesh.updateModelBound();
		textMesh.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);
		return textMesh;
	}

	private BMFont getFont() {
		if (font == null) {
			Supplier<InputStream> inputSupplier = streamLocator.getInputStreamSupplier("fonts/Eras.fnt");
			ResourceSource resource = new SpacedResource("fonts/Eras.fnt", inputSupplier, "fnt") {
				@Override
				public ResourceSource getRelativeSource(String s) {
					return new SpacedResource("Eras_0.png",
							streamLocator.getInputStreamSupplier("textures/fonts/Eras_0.png"),
							".png");
				}
			};

			try {
				font = new BMFont(resource, true);
			} catch (IOException e) {
				log.warn("Failed to load font resource for nameplates", e);
			}
		}
		return font;
	}
}