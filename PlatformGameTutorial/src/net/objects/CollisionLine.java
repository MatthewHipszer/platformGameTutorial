package net.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.gameState.GameState;

public class CollisionLine extends Line2D.Double {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	
	public CollisionLine (double x1, double y1, double x2, double y2, int id) {
		super(x1,y1,x2,y2);
		this.id = id;
	}
	
	public void tick () {

	}
	
	public void draw (Graphics g) {
		if (id == 2)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.RED);
		g.drawLine((int)(x1 - GameState.xOffset),(int)(y1 - GameState.yOffset), 
				   (int)(x2 - GameState.xOffset), (int)(y2 - GameState.yOffset)); 
	}
	
	public void setID (int id)
	{
		this.id = id;
	}
	
	public int getID ()
	{
		return id;
	}
}