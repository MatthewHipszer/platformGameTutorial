package net.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	public static BufferedImage[] blocks;
	
	public Image () {
		blocks = new BufferedImage[1];
		try {
			blocks[0] = ImageIO.read(getClass().getResourceAsStream("/blocks/block_brick.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
