package se.ardortech.math;

/**
 * This class is a utility representing a rotation order specification
 * for Cardan or Euler angles specification.
 *
 * This class cannot be instanciated by the user. He can only use one
 * of the twelve predefined supported orders as an argument to either
 * the {@link SpacedRotation#SpacedRotation(SpacedRotationOrder ,double,double,double)}
 * constructor or the {@link SpacedRotation#getAngles} method.
 *
 * @version $Revision: 811827 $ $Date: 2009-09-06 11:32:50 -0400 (Sun, 06 Sep 2009) $
 * @since 1.2
 */
public enum SpacedRotationOrder {
	
    /** Set of Cardan angles.
     * this ordered set of rotations is around X, then around Y, then
     * around Z
     */
    XYZ("XYZ", SpacedVector3.PLUS_I, SpacedVector3.PLUS_J, SpacedVector3.PLUS_K),

    /** Set of Cardan angles.
     * this ordered set of rotations is around X, then around Z, then
     * around Y
     */
    XZY("XZY", SpacedVector3.PLUS_I, SpacedVector3.PLUS_K, SpacedVector3.PLUS_J),

    /** Set of Cardan angles.
     * this ordered set of rotations is around Y, then around X, then
     * around Z
     */
    YXZ("YXZ", SpacedVector3.PLUS_J, SpacedVector3.PLUS_I, SpacedVector3.PLUS_K),

    /** Set of Cardan angles.
     * this ordered set of rotations is around Y, then around Z, then
     * around X
     */
    YZX("YZX", SpacedVector3.PLUS_J, SpacedVector3.PLUS_K, SpacedVector3.PLUS_I),

    /** Set of Cardan angles.
     * this ordered set of rotations is around Z, then around X, then
     * around Y
     */
	 ZXY("ZXY", SpacedVector3.PLUS_K, SpacedVector3.PLUS_I, SpacedVector3.PLUS_J),

    /** Set of Cardan angles.
     * this ordered set of rotations is around Z, then around Y, then
     * around X
     */
    ZYX("ZYX", SpacedVector3.PLUS_K, SpacedVector3.PLUS_J, SpacedVector3.PLUS_I),

    /** Set of Euler angles.
     * this ordered set of rotations is around X, then around Y, then
     * around X
     */
    XYX("XYX", SpacedVector3.PLUS_I, SpacedVector3.PLUS_J, SpacedVector3.PLUS_I),

    /** Set of Euler angles.
     * this ordered set of rotations is around X, then around Z, then
     * around X
     */
    XZX("XZX", SpacedVector3.PLUS_I, SpacedVector3.PLUS_K, SpacedVector3.PLUS_I),

    /** Set of Euler angles.
     * this ordered set of rotations is around Y, then around X, then
     * around Y
     */
   YXY("YXY", SpacedVector3.PLUS_J, SpacedVector3.PLUS_I, SpacedVector3.PLUS_J),

    /** Set of Euler angles.
     * this ordered set of rotations is around Y, then around Z, then
     * around Y
     */
    YZY("YZY", SpacedVector3.PLUS_J, SpacedVector3.PLUS_K, SpacedVector3.PLUS_J),

    /** Set of Euler angles.
     * this ordered set of rotations is around Z, then around X, then
     * around Z
     */
    ZXZ("ZXZ", SpacedVector3.PLUS_K, SpacedVector3.PLUS_I, SpacedVector3.PLUS_K),

    /** Set of Euler angles.
     * this ordered set of rotations is around Z, then around Y, then
     * around Z
     */
    ZYZ("ZYZ", SpacedVector3.PLUS_K, SpacedVector3.PLUS_J, SpacedVector3.PLUS_K);

    /** Name of the rotations order. */
    private final String name;

    /** Axis of the first rotation. */
    private final SpacedVector3 a1;

    /** Axis of the second rotation. */
    private final SpacedVector3 a2;

    /** Axis of the third rotation. */
    private final SpacedVector3 a3;

    /** Private constructor.
     * This is a utility class that cannot be instantiated by the user,
     * so its only constructor is private.
     * @param name name of the rotation order
     * @param a1 axis of the first rotation
     * @param a2 axis of the second rotation
     * @param a3 axis of the third rotation
     */
    private SpacedRotationOrder(final String name,
                          final SpacedVector3 a1, final SpacedVector3 a2, final SpacedVector3 a3) {
        this.name = name;
        this.a1   = a1;
        this.a2   = a2;
        this.a3   = a3;
    }

    /** Get a string representation of the instance.
     * @return a string representation of the instance (in fact, its name)
     */
    @Override
    public String toString() {
        return name;
    }

    /** Get the axis of the first rotation.
     * @return axis of the first rotation
     */
    public SpacedVector3 getA1() {
        return a1;
    }

    /** Get the axis of the second rotation.
     * @return axis of the second rotation
     */
    public SpacedVector3 getA2() {
        return a2;
    }

    /** Get the axis of the second rotation.
     * @return axis of the second rotation
     */
    public SpacedVector3 getA3() {
        return a3;
    }

}
