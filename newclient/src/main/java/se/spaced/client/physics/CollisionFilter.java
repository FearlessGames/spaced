package se.spaced.client.physics;

public class CollisionFilter {
	// Collision groups 
	public static final short NOTHING =	0; // Disable collision
	public static final short PLAYER_GROUP =	1; // The player sphere
	public static final short STATIC_GROUP =	2; // Static level geometry
	public static final short ALL =  -1; // Everything

	// Collision masks
	public static final short PLAYER_MASK = ALL;
	public static final short STATIC_MASK = PLAYER_GROUP;
	public static final short CAMERA_RAYCAST_MASK = STATIC_GROUP;
}
