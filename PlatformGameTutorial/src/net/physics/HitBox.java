package net.physics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;


public class HitBox extends Rectangle2D.Double {

	private static final long serialVersionUID = 1L;
	private int id;
	//private double x, y, width, height;

	public HitBox (double x, double y, double width, double height, int id) {
		super(x, y, width, height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.id = id;
	}

	public void tick()
	{

	}

	public void draw (Graphics g)
	{
		if (id == 1)
			g.setColor(Color.RED);
		else
			g.setColor(Color.GREEN);
		g.drawRect((int)x, (int)y, (int)width, (int)height);
	}

	public int getID()
	{
		return id;
	}
}
