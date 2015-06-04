package se.spaced.shared.model.xmo;


public class AnimationData {
	private String skeletonFile;
	private String animationMapping;

	public String getSkeletonFile() {
		return skeletonFile;
	}

	public void setSkeletonFile(String skeletonFile) {
		this.skeletonFile = skeletonFile;
	}

	public String getAnimationMapping() {
		return animationMapping;
	}

	public void setAnimationMapping(String animationMapping) {
		this.animationMapping = animationMapping;
	}

	@Override
	public String toString() {
		return "AnimationData{" +
				"skeletonFile='" + skeletonFile + '\'' +
				", animationMapping='" + animationMapping + '\'' +
				'}';
	}
}