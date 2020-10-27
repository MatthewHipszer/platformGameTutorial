package net.physics;

import java.awt.Point;

import net.objects.Block;
import net.objects.CollisionLine;
import net.objects.MovingBlock;

public class Collision {

	public static boolean playerBlock(Point p, Block b) {
		return b.contains(p);
	}
	
	public static boolean playerMovingBlock(Point p, MovingBlock b) {
		return b.contains(p);
	}
}
