package net.maps;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.gameState.GameState;



public class Level1Test {

	public static BufferedImage map;

	public int x, y;
	public int width = 2240;
	public int height = 1484;

	public Level1Test (int x, int y) {
		this.x = x;
		this.y = y;
		try {
			map = ImageIO.read(getClass().getResourceAsStream("/maps/testMap1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick () {

	}

	public void draw (Graphics g) {

			g.drawImage(map, x - (int)GameState.xOffset, y - (int)GameState.yOffset, width, height, null);

	}
}
