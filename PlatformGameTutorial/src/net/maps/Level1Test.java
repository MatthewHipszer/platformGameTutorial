package net.maps;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.gameState.GameState;



public class Level1Test {

	public static BufferedImage map, mapBackground, mapForeground;

	public int x, y;
	public int width = 2240;
	public int height = 1484;

	public Level1Test (int x, int y) {
		//System.out.println("Map x and y: " + x + ", " + y);
		//x and y are 0
		this.x = x;
		this.y = y;
		try {
			map = ImageIO.read(getClass().getResourceAsStream("/maps/map1NoBackground.png"));
			mapBackground = ImageIO.read(getClass().getResourceAsStream("/maps/map1TestBackground1.png"));
			//mapForeground = ImageIO.read(getClass().getResourceAsStream("/maps/map1TestForeground1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick () {

	}

	public void draw (Graphics g) {

		g.drawImage(mapBackground, x - ((int)GameState.xOffset/10), y - ((int)GameState.yOffset/10), width, height, null);
		g.drawImage(map, x - (int)GameState.xOffset, y - (int)GameState.yOffset, width, height, null);
		//g.drawImage(mapForeground, x - (int)GameState.xOffset, y - (int)GameState.yOffset, width, height, null);
	}
}
