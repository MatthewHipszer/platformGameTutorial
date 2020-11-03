package net.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;

import net.gameState.GameState;
import net.maps.Map.LineType;

public class CollisionLine extends Line2D.Double {

	private static final long serialVersionUID = 1L;
	private LineType id;
	private double slope;
	private double yIntercept;
	private double angleInDegrees;

	public CollisionLine (double x1, double y1, double x2, double y2, LineType id) {
		super(x1,y1,x2,y2);
		this.id = id;

		slope = (y2 - y1) / (x2 - x1);
		yIntercept = y1 - (slope * x1);

		// Obtains the angle of the line
		// May be needed for left/right code depending on how I feel
		// about coding slopes
		// System.out.println("i: " + i);
		double deltaY = y2 - y1;
		double deltaX = x2 - x1;
		// System.out.println("deltax: " + deltaX);
		// System.out.println("deltay: " + deltaY);
		angleInDegrees = Math.atan(deltaY / deltaX) * 180 / Math.PI;

		System.out.println("angle of slope in constructor: " + angleInDegrees);

	}

	public void tick () {
	}

	public void draw (Graphics g) {
		if (id == LineType.SEMISOLIDFLOOR)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.RED);
		g.drawLine((int)(x1 - GameState.xOffset),(int)(y1 - GameState.yOffset),
				   (int)(x2 - GameState.xOffset), (int)(y2 - GameState.yOffset));
	}

	public void setID (LineType id)
	{
		this.id = id;
	}

	public LineType getID ()
	{
		return id;
	}

	public double getAngle()
	{
		return angleInDegrees;
	}

	public double getYIntercept()
	{
		return yIntercept;
	}

	public double getSlope() {
		return slope;
	}
}
