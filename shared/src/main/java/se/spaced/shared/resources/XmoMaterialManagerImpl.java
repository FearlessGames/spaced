package se.spaced.shared.resources;

import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.model.xmo.Blending;
import se.spaced.shared.model.xmo.Material;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

@Singleton
public class XmoMaterialManagerImpl implements XmoMaterialManager {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Cache<String, Material> materialCache;

	@Inject
	public XmoMaterialManagerImpl(final XmlIO xmlIO, @Named("xmoCachedManager") CacheManager xmoCacheManager) {
		materialCache = new ThreadSafeCache<String, Material>(new CacheLoader<String, Material>() {
			@Override
			public Material load(String path) {
				try {
					return xmlIO.load(Material.class, path);
				} catch (XmlIOException e) {
					log.warn("Could not find material {}", path);
					return null;
				}
			}
		});
		xmoCacheManager.addManagedCache(materialCache);
	}

	private Material getMaterial(final String materialFile) {
		return materialCache.get(materialFile);
	}

	@Override
	public void applyMaterial(final String materialFile, final Spatial spatial) {
		final Material material = getMaterial(materialFile);
		if (material == null) {
			return;
		}

		final MaterialState ms = getMaterialState(material);
		final BlendState bs = getBlendState(material);
		final RenderBucketType rbt = getRenderQueue(material);
		final CullState cs = getCullState(material);
		final ZBufferState zbs = getZBufferState(material);

		spatial.acceptVisitor(new Visitor() {
			@Override
			public void visit(Spatial spatial) {
				if (spatial instanceof Mesh) {
					spatial.setRenderState(ms);
					spatial.setRenderState(bs);
					spatial.setRenderState(cs);
					spatial.getSceneHints().setRenderBucketType(rbt);
					if (zbs != null) {
						spatial.setRenderState(zbs);
					}
				}
			}
		}, true);
	}

	private RenderBucketType getRenderQueue(final Material material) {
		String name = material.getRenderBucket();
		if (name == null || name.isEmpty()) {
			return RenderBucketType.Opaque;

		}

		return RenderBucketType.getRenderBucketType(name);
	}

	private MaterialState getMaterialState(final Material material) {
		final MaterialState ms = new MaterialState();
		if (material.getDiffuse() != null) {
			ms.setDiffuse(material.getDiffuse());
		} else {
			log.debug("no diffuse found in material");
		}

		if (material.getSpecular() != null) {
			ms.setSpecular(material.getSpecular());
		} else {
			log.debug("No Specular found in material");
		}

		if (material.getAmbient() != null) {
			ms.setAmbient(material.getAmbient());
		} else {
			log.debug("No ambient found in material");
		}

		if (material.getEmissive() != null) {
			ms.setEmissive(material.getEmissive());
		} else {
			log.debug("No emissive found in material");
		}

		ms.setShininess(material.getShininess());

		ms.setEnabled(true);
		return ms;
	}

	private BlendState getBlendState(final Material material) {
		final BlendState bs = new BlendState();
		final Blending blending = material.getBlending();

		if (blending != null) {
			if (blending.getSourceFunction() != null && blending.getDestinationFunction() != null) {
				bs.setSourceFunction(blending.getSourceFunction());
				bs.setDestinationFunction(blending.getDestinationFunction());
				bs.setBlendEnabled(true);
			}

			bs.setTestEnabled(blending.isAlphaTesting());
			if (blending.isAlphaTesting()) {
				bs.setReference(blending.getAlphaTestReference());
			}
		} else {
			bs.setBlendEnabled(false);
		}

		bs.setEnabled(true);
		return bs;
	}

	private CullState getCullState(final Material material) {
		final CullState cullState = new CullState();
		CullState.Face face = material.getCull();
		if (face == null) {
			face = CullState.Face.None;
		}
		cullState.setCullFace(face);
		return cullState;
	}

	private ZBufferState getZBufferState(final Material material) {
		final boolean depthRead = material.isDepthRead();
		final boolean depthWrite = material.isDepthWrite();

		if (depthRead || depthWrite) {
			ZBufferState depthBuffer = new ZBufferState();
			depthBuffer.setWritable(depthWrite);
			depthBuffer.setEnabled(depthRead);

			return depthBuffer;
		}

		return null;
	}

	@Override
	public void invalidateCache(String materialFile) {
		materialCache.invalidate(materialFile);
	}

	@Override
	public void invalidateEntireCache() {
		materialCache.invalidateAll();
	}
}
