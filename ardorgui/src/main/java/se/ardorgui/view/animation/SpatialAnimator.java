package se.ardorgui.view.animation;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.ComplexSpatialController;
import com.ardor3d.scenegraph.controller.SpatialController;
import org.apache.commons.math.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class animates spatials by interpolating between various
 * transformations. The user defines objects to be transformed and what
 * rotation/translation/scale to give each object at various points in time. The
 * user must call interpolateMissing() before using the controller in order to
 * interpolate unspecified translation/rotation/scale.
 */
public class SpatialAnimator extends ComplexSpatialController<Spatial> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SpatialAnimator.class);
	private static final Vector3 unSyncbeginPos = new Vector3();
	private static final Vector3 unSyncendPos = new Vector3();
	private static final Matrix3 unSyncbeginRot = new Matrix3();
	private static final Matrix3 unSyncendRot = new Matrix3();
	public Spatial toChange;
	private Transform basePivot;
	private final Transform pivots;
	public ArrayList<PointInTime> keyframes;
	private double curTime;
	private PointInTime beginPointTime;
	private PointInTime endPointTime;
	private boolean haveChanged; // Used internally in update to flag that a pivot has been updated
	private double delta;
	private AnimatorListener listener;

	public SpatialAnimator(Spatial toChange, AnimatorListener listener) {
		this(toChange);
		this.listener = listener;
	}

	public SpatialAnimator(Spatial toChange) {
		synchronized (toChange.getControllers()) {
			for (int i = 0; i < toChange.getControllers().size(); i++) {
				SpatialController<?> controller = toChange.getController(i);
				if (controller instanceof SpatialAnimator) {
					SpatialAnimator spatialAnim = (SpatialAnimator) controller;
					toChange.setTranslation(spatialAnim.getBasePivot().getTranslation());
					toChange.setScale(spatialAnim.getBasePivot().getScale());
					toChange.setRotation(spatialAnim.getBasePivot().getMatrix());

					if (spatialAnim.listener != null) {
						spatialAnim.listener.animationAborted(spatialAnim);
					}

					toChange.removeController(controller);
					i--;
				}
			}
		}

		toChange.addController(this);
		this.toChange = toChange;
		pivots = new Transform();
		pivots.setTranslation(toChange.getTranslation());
		pivots.setScale(toChange.getScale());
		pivots.setRotation(toChange.getRotation());

		basePivot = new Transform();
		basePivot.setTranslation(toChange.getTranslation());
		basePivot.setScale(toChange.getScale());
		basePivot.setRotation(toChange.getRotation());

		keyframes = new ArrayList<PointInTime>();

		setRepeatType(RepeatType.CLAMP);
	}

	@Override
	public void update(double time, Spatial spatial) {
		if (!isActive()) {
			return;
		}
		curTime += time * getSpeed();

		boolean keepAlive = setBeginAndEnd();
		if (keepAlive) {
			haveChanged = false;
			delta = endPointTime.time - beginPointTime.time;
			if (delta != 0f) {
				delta = (curTime - beginPointTime.time) / delta;
			}
			updatePivot();
			applyToSpatial(pivots);
		} else {
			// finished
			toChange.setTranslation(basePivot.getTranslation());
			toChange.setScale(basePivot.getScale());
			toChange.setRotation(basePivot.getMatrix());
			toChange.removeController(this);

			if (listener != null) {
				listener.animationFinished(this);
			}
		}
	}

	private void applyToSpatial(Transform pivots) {
//		pivots.applyToSpatial(toChange);
		toChange.setTranslation(pivots.getTranslation());
		toChange.setRotation(pivots.getMatrix());
		toChange.setScale(pivots.getScale());
	}

	public void abort() {
		logger.debug("Aborting controller");
		if (listener != null) {
			listener.animationAborted(this);
		}
	}

	/**
	 * Called by update, and itself recursively. Will, when completed, change
	 * toChange[objIndex] by pivots[objIndex]
	 */
	private void updatePivot() {
		if (haveChanged) {
			return;
		}

		haveChanged = true;
		interpolateTransforms(beginPointTime.look, endPointTime.look, (float)delta, pivots);

//		pivots.combineWithParent(basePivot); // pivots.getTranslation().addLocal( basePivot.getTranslation() );
		Vector3 tempVec = new Vector3();
		pivots.getTranslation().add(basePivot.getTranslation(), tempVec);
		pivots.setTranslation(tempVec);
	}

	private void interpolateTransforms(Transform begin, Transform end, float delta, Transform store) {
//		pivots.interpolateTransforms(beginPointTime.look, endPointTime.look, delta);
		Vector3 translation = new Vector3();
		Vector3.lerp(begin.getTranslation(), end.getTranslation(), delta, translation);
		pivots.setTranslation(translation);

		Vector3 scale = new Vector3();
		Vector3.lerp(begin.getScale(), end.getScale(), delta, scale);
		pivots.setScale(scale);

		Quaternion rotation = new Quaternion();
		Quaternion startRotation = new Quaternion();
		Quaternion endRotation = new Quaternion();
		startRotation.fromRotationMatrix(begin.getMatrix());
		endRotation.fromRotationMatrix(end.getMatrix());
		Quaternion.slerp(startRotation, endRotation, delta, rotation);
		pivots.setRotation(rotation);
	}

	/**
	 * overridden by SpatialAnimator to always set a time inside the first and
	 * the last keyframe's time in the animation.
	 */
	@Override
	public void setMinTime(double minTime) {
		if (keyframes != null && !keyframes.isEmpty()) {
			float firstFrame = keyframes.get(0).time;
			float lastFrame = keyframes.get(keyframes.size() - 1).time;
			if (minTime < firstFrame) {
				minTime = firstFrame;
			}
			if (minTime > lastFrame) {
				minTime = lastFrame;
			}
		}

		curTime = minTime;
		super.setMinTime(minTime);
	}

	/**
	 * overridden by SpatialAnimator to always set a time inside the first and
	 * the last keyframe's time in the animation
	 */
	@Override
	public void setMaxTime(double maxTime) {
		if (keyframes != null && !keyframes.isEmpty()) {
			float firstFrame = keyframes.get(0).time;
			float lastFrame = keyframes.get(keyframes.size() - 1).time;
			if (maxTime < firstFrame) {
				maxTime = firstFrame;
			}
			if (maxTime > lastFrame) {
				maxTime = lastFrame;
			}
		}
		super.setMaxTime(maxTime);
	}

	/**
	 * Sets the new animation boundaries for this controller. This will start at
	 * newBeginTime and proceed in the direction of newEndTime (either forwards
	 * or backwards). If both are the same, then the animation is set to their
	 * time and turned off, otherwise the animation is turned on to start the
	 * animation according to the repeat type. If either BeginTime or EndTime are
	 * invalid times (less than 0 or greater than the maximum set keyframe time)
	 * then a warning is set and nothing happens. <br>
	 * It is suggested that this function be called if new animation boundaries
	 * need to be set, instead of setMinTime and setMaxTime directly.
	 *
	 * @param newBeginTime
	 *            The starting time
	 * @param newEndTime
	 *            The ending time
	 */
	public void setNewAnimationTimes(float newBeginTime, float newEndTime) {
		if (newBeginTime < 0 || newBeginTime > keyframes.get(keyframes.size() - 1).time) {
			logger.warn("Attempt to set invalid begintime:" + newBeginTime);
			return;
		}
		if (newEndTime < 0 || newEndTime > keyframes.get(keyframes.size() - 1).time) {
			logger.warn("Attempt to set invalid endtime:" + newEndTime);
			return;
		}
		setMinTime(newBeginTime);
		setMaxTime(newEndTime);
		setActive(true);
		if (newBeginTime <= newEndTime) { // Moving forward
			curTime = newBeginTime;
			if (MathUtils.equals(newBeginTime, newEndTime, 1)) {
				update(0, toChange);
				setActive(false);
			}
		} else { // Moving backwards
			curTime = newEndTime;
		}
	}

	public double getCurTime() {
		return curTime;
	}

	public void setCurTime(float time) {
		curTime = time;
	}

	/**
	 * Called in update for calculating the correct beginPointTime and
	 * endPointTime, and changing curTime if necessary.
	 */
	private boolean setBeginAndEnd() {
		double minTime = getMinTime();
		double maxTime = getMaxTime();

		if (getSpeed() > 0) {
			if (curTime >= maxTime) {
				if (getRepeatType() == RepeatType.CYCLE) {
					int[] is = findIndicesBeforeAfter(maxTime);
					int beginIndex = is[0];
					int endIndex = is[1];
					beginPointTime = keyframes.get(beginIndex);
					endPointTime = keyframes.get(endIndex);
					double overshoot = curTime - maxTime;
					curTime = maxTime - overshoot;
					setSpeed(-getSpeed());

					return true;
				} else if (getRepeatType() == RepeatType.CLAMP) {
					int[] is = findIndicesBeforeAfter(maxTime);
					int beginIndex = is[1];
					beginPointTime = keyframes.get(beginIndex);
					endPointTime = beginPointTime;
					curTime = maxTime;

					return true;
				}
				return false;
			} else if (curTime <= minTime) {
				int[] is = findIndicesBeforeAfter(minTime);
				int beginIndex = is[0];
				int endIndex = is[1];
				beginPointTime = keyframes.get(beginIndex);
				endPointTime = keyframes.get(endIndex);
				curTime = minTime;
			} else {// curTime is inside minTime and maxTime
				int[] is = findIndicesBeforeAfter(curTime);
				int beginIndex = is[0];
				int endIndex = is[1];
				beginPointTime = keyframes.get(beginIndex);
				endPointTime = keyframes.get(endIndex);
			}
		} else if (getSpeed() < 0) {
			if (curTime <= minTime) {
				if (getRepeatType() == RepeatType.WRAP) {
					int[] is = findIndicesBeforeAfter(maxTime);
					int beginIndex = is[1];
					int endIndex = is[0];
					beginPointTime = keyframes.get(beginIndex);
					endPointTime = keyframes.get(endIndex);
					double overshoot = minTime - curTime;
					curTime = maxTime - overshoot;
				} else if (getRepeatType() == RepeatType.CLAMP) {
					int[] is = findIndicesBeforeAfter(minTime);
					int beginIndex = is[1];
					beginPointTime = keyframes.get(beginIndex);
					endPointTime = beginPointTime;
					curTime = minTime;
				} else if (getRepeatType() == RepeatType.CYCLE) {
					int[] is = findIndicesBeforeAfter(minTime);
					int beginIndex = is[1];
					int endIndex = is[0];
					beginPointTime = keyframes.get(beginIndex);
					endPointTime = keyframes.get(endIndex);
					double overshoot = minTime - curTime;
					curTime = minTime + overshoot;
					setSpeed(-getSpeed());
				}
			} else if (curTime >= maxTime) {
				int[] is = findIndicesBeforeAfter(maxTime);
				int beginIndex = is[1];
				int endIndex = is[0];
				beginPointTime = keyframes.get(beginIndex);
				endPointTime = keyframes.get(endIndex);
				curTime = maxTime;
			} else {// curTime is inside minTime and maxTime
				int[] is = findIndicesBeforeAfter(curTime);
				int beginIndex = is[1];
				int endIndex = is[0];
				beginPointTime = keyframes.get(beginIndex);
				endPointTime = keyframes.get(endIndex);
			}
		} else {
			beginPointTime = keyframes.get(0);
			endPointTime = keyframes.get(0);
		}

		return true;
	}

	/**
	 * Finds indices i in keyframes such that <code>
	 * keyframes.get(i[0]).time < giventime <= keyframes.get(i[1]).time </code>
	 * if no keyframe was found before or after <code>giventime</code>, the
	 * corresponding value will clamp to <code>0</code> resp.
	 * <code>keyframes.size() - 1</code>
	 */
	int[] findIndicesBeforeAfter(double giventime) {
		int[] ret = new int[] { 0, keyframes.size() - 1 };
		for (int i = 0; i < keyframes.size(); i++) {
			float curFrameTime = keyframes.get(i).time;
			if (curFrameTime >= giventime) {
				ret[1] = i;
				return ret;
			}
			ret[0] = i;
		}
		return ret;
	}

	/**
	 * Returns the keyframe for <code>time</code>. If one doens't exist, a new
	 * one is created, and <code>getMaxTime()</code> will be set to
	 * <code>Math.max(time, getMaxTime())</code>.
	 *
	 * @param time
	 *            The time to look for.
	 * @return The keyframe referencing <code>time</code>.
	 */
	private PointInTime findTime(float time) {
		for (int i = 0; i < keyframes.size(); i++) {
			if (keyframes.get(i).time == time) {
				setMinTime(Math.min(time, getMinTime()));
				setMaxTime(Math.max(time, getMaxTime()));
				return keyframes.get(i);
			}
			if (keyframes.get(i).time > time) {
				PointInTime t = new PointInTime(time);
				keyframes.add(i, t);
				setMinTime(Math.min(time, getMinTime()));
				setMaxTime(Math.max(time, getMaxTime()));
				return t;
			}
		}
		PointInTime t = new PointInTime(time);
		keyframes.add(t);
		setMinTime(Math.min(time, getMinTime()));
		setMaxTime(Math.max(time, getMaxTime()));
		return t;
	}

	/**
	 * Sets object with index <code>indexInST</code> to rotate by
	 * <code>rot</code> at time <code>time</code>.
	 *
	 * @param time
	 *            The time for the spatial to take this rotation
	 * @param rot
	 *            The rotation to take
	 */
	public void setRotation(float time, Quaternion rot) {
		PointInTime toAdd = findTime(time);
		toAdd.setRotation(rot);
	}

	/**
	 * Sets object with index <code>indexInST</code> to translate by
	 * <code>position</code> at time <code>time</code>.
	 *
	 * @param time
	 *            The time for the spatial to take this translation
	 * @param position
	 *            The position to take
	 */
	public void setPosition(float time, Vector3 position) {
		PointInTime toAdd = findTime(time);
		toAdd.setTranslation(position);
	}

	/**
	 * Sets object with index <code>indexInST</code> to scale by
	 * <code>scale</code> at time <code>time</code>.
	 *
	 * @param time
	 *            The time for the spatial to take this scale
	 * @param scale
	 *            The scale to take
	 */
	public void setScale(float time, Vector3 scale) {
		PointInTime toAdd = findTime(time);
		toAdd.setScale(scale);
	}

	/**
	 * This must be called one time, once all translations/rotations/scales have
	 * been set. It will interpolate unset values to make the animation look
	 * correct. Tail and head values are assumed to be the identity.
	 */
	public void interpolateMissing() {
		if (keyframes.size() != 1) {
			fillTrans();
			fillRots();
			fillScales();
		}
		applyToSpatial(pivots);
	}

	/**
	 * Called by interpolateMissing(), it will interpolate missing scale values.
	 */
	private void fillScales() {
		// 1) Find first non-null scale of objIndex <code>objIndex</code>
		int start;
		for (start = 0; start < keyframes.size(); start++) {
			if (keyframes.get(start).usedScale) {
				break;
			}
		}
		if (start == keyframes.size()) { // if they are all null then fill
			// with identity
			for (PointInTime keyframe : keyframes) {
				keyframe.look.setScale(pivots.getScale());
			}
			return; // we're done so lets break
		}

		if (start != 0) { // if there -are- null elements at the beginning,
			// then fill with first non-null
			unSyncbeginPos.set(keyframes.get(start).look.getScale());
			for (int i = 0; i < start; i++) {
				keyframes.get(i).look.setScale(unSyncbeginPos);
			}
		}
		int lastgood = start;
		for (int i = start + 1; i < keyframes.size(); i++) {
			if (keyframes.get(i).usedScale) {
				fillScale(0, lastgood, i); // fills gaps
				lastgood = i;
			}
		}
		if (lastgood != keyframes.size() - 1) { // Make last ones equal to
			// last good
			keyframes.get(keyframes.size() - 1).look.setScale(keyframes.get(lastgood).look.getScale());
		}
		unSyncbeginPos.set(keyframes.get(lastgood).look.getScale());

		for (int i = lastgood + 1; i < keyframes.size(); i++) {
			keyframes.get(i).look.setScale(unSyncbeginPos);
		}
	}

	/**
	 * Interpolates unspecified scale values for objectIndex from start to end.
	 */
	private void fillScale(int objectIndex, int startScaleIndex, int endScaleIndex) {
		unSyncbeginPos.set(keyframes.get(startScaleIndex).look.getScale());
		unSyncendPos.set(keyframes.get(endScaleIndex).look.getScale());
		float startTime = keyframes.get(startScaleIndex).time;
		float endTime = keyframes.get(endScaleIndex).time;
		float delta = endTime - startTime;
		Vector3 tempVec = new Vector3();

		for (int i = startScaleIndex + 1; i < endScaleIndex; i++) {
			float thisTime = keyframes.get(i).time;
			Vector3.lerp(unSyncbeginPos, unSyncendPos, (thisTime - startTime) / delta, tempVec);
			keyframes.get(i).look.setScale(tempVec);
		}
	}

	/**
	 * Called by interpolateMissing(), it will interpolate missing rotation values.
	 */
	private void fillRots() {
		// 1) Find first non-null rotation of joint <code>joint</code>
		int start;
		for (start = 0; start < keyframes.size(); start++) {
			if (keyframes.get(start).usedRot) {
				break;
			}
		}
		if (start == keyframes.size()) { // if they are all null then fill
			// with identity
			for (PointInTime keyframe : keyframes) {
				keyframe.look.setRotation(pivots.getMatrix());
			}

			return; // we're done so lets break
		}
		if (start != 0) { // if there -are- null elements at the begining,
			// then fill with first non-null

			unSyncbeginRot.set(keyframes.get(start).look.getMatrix());
			for (int i = 0; i < start; i++) {
				keyframes.get(i).look.setRotation(unSyncbeginRot);
			}
		}
		int lastgood = start;
		for (int i = start + 1; i < keyframes.size(); i++) {
			if (keyframes.get(i).usedRot) {
				fillQuats(0, lastgood, i); // fills gaps
				lastgood = i;
			}
		}
		// fillQuats(joint,lastgood,keyframes.size()-1); // fills tail
		unSyncbeginRot.set(keyframes.get(lastgood).look.getMatrix());

		for (int i = lastgood + 1; i < keyframes.size(); i++) {
			keyframes.get(i).look.setRotation(unSyncbeginRot);
		}
	}

	/**
	 * Interpolates unspecified rot values for objectIndex from start to end.
	 */
	private void fillQuats(int objectIndex, int startRotIndex, int endRotIndex) {
		unSyncbeginRot.set(keyframes.get(startRotIndex).look.getMatrix());
		unSyncendRot.set(keyframes.get(endRotIndex).look.getMatrix());
		float startTime = keyframes.get(startRotIndex).time;
		float endTime = keyframes.get(endRotIndex).time;
		float delta = endTime - startTime;
		Quaternion result = new Quaternion();
		Quaternion start = new Quaternion();
		Quaternion end = new Quaternion();

		for (int i = startRotIndex + 1; i < endRotIndex; i++) {
			float thisTime = keyframes.get(i).time;
			start.fromRotationMatrix(unSyncbeginRot);
			end.fromRotationMatrix(unSyncendRot);
			Quaternion.slerp(start, end, (thisTime - startTime) / delta, result);
			keyframes.get(i).look.setRotation(result);
		}
	}

	/**
	 * Called by interpolateMissing(), it will interpolate missing translation values.
	 */
	private void fillTrans() {
		// 1) Find first non-null translation of objIndex <code>objIndex</code>
		int start;
		for (start = 0; start < keyframes.size(); start++) {
			if (keyframes.get(start).usedTrans) {
				break;
			}
		}
		if (start == keyframes.size()) { // if they are all null then fill
			// with identity
			for (PointInTime keyframe : keyframes) {
				keyframe.look.setTranslation(pivots.getTranslation());
			}
			return; // we're done so lets break
		}

		if (start != 0) { // if there -are- null elements at the begining,
			// then fill with first non-null
			unSyncbeginPos.set(keyframes.get(start).look.getTranslation());
			for (int i = 0; i < start; i++) {
				keyframes.get(i).look.setTranslation(unSyncbeginPos);
			}
		}
		int lastgood = start;
		for (int i = start + 1; i < keyframes.size(); i++) {
			if (keyframes.get(i).usedTrans) {
				fillVecs(0, lastgood, i); // fills gaps
				lastgood = i;
			}
		}
		if (lastgood != keyframes.size() - 1) { // Make last ones equal to
			// last good
			keyframes.get(keyframes.size() - 1).look.setTranslation(keyframes.get(lastgood).look.getTranslation());
		}
		unSyncbeginPos.set(keyframes.get(lastgood).look.getTranslation());

		for (int i = lastgood + 1; i < keyframes.size(); i++) {
			keyframes.get(i).look.setTranslation(unSyncbeginPos);
		}
	}

	/**
	 * Interpolates unspecified translation values for objectIndex from start to end.
	 */
	private void fillVecs(int objectIndex, int startPosIndex, int endPosIndex) {
		unSyncbeginPos.set(keyframes.get(startPosIndex).look.getTranslation());
		unSyncendPos.set(keyframes.get(endPosIndex).look.getTranslation());
		float startTime = keyframes.get(startPosIndex).time;
		float endTime = keyframes.get(endPosIndex).time;
		float delta = endTime - startTime;
		Vector3 tempVec = new Vector3();

		for (int i = startPosIndex + 1; i < endPosIndex; i++) {
			float thisTime = keyframes.get(i).time;
			Vector3.lerp(unSyncbeginPos, unSyncendPos, (thisTime - startTime) / delta, tempVec);
			keyframes.get(i).look.setTranslation(tempVec);
		}
	}

	public Transform getBasePivot() {
		return basePivot;
	}

	public void setBasePivot(Transform basePivot) {
		this.basePivot = basePivot;
	}

	/**
	 * Defines a point in time where at time <code>time</code>, object
	 * <code>toChange[i]</code> will assume transformation <code>look[i]</code>.
	 * BitSet's used* specify if the transformation value was specified by the
	 * user, or interpolated
	 */
	public static class PointInTime implements Serializable {
		private static final long serialVersionUID = 1L;
		public boolean usedRot;		// Bit i is true if look[i].rotation was user defined.
		public boolean usedTrans;	// Bit i is true if look[i].translation was user defined.
		public boolean usedScale;	// Bit i is true if look[i].scale was user defined.
		public float time;			// The time of this TransformationMatrix.
		public Transform look;		// toChange[i] looks like look[i] at time.

		public PointInTime() {}
		public PointInTime(float time) {
			look = new Transform();
			this.time = time;
		}

		void setRotation(Quaternion rot) {
			look.setRotation(rot);
			usedRot = true;
		}

		void setTranslation(Vector3 trans) {
			look.setTranslation(trans);
			usedTrans = true;
		}

		void setScale(Vector3 scale) {
			look.setScale(scale);
			usedScale = true;
		}
	}
}