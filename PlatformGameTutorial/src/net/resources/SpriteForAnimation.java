package net.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteForAnimation {

	//Create the spriteSheet
    private static BufferedImage spriteSheet;
    private static final int SPRITE_WIDTH = 30;
    private static final int SPRITE_HEIGHT = 50;
    BufferedImage Sprite1, Sprite2, Sprite3, Sprite4, Sprite5, Sprite6;
    BufferedImage sprites[];
    private int currentFrame = 0;
    private int finalFrame;
    private int repeatFrames;


    //Currently only ever does the top row of an animation
    public SpriteForAnimation(int x, int y, int frames, int repeatFrames)
    {
    	this.repeatFrames = repeatFrames;
    	finalFrame = frames;
    	sprites = new BufferedImage[frames];

    	spriteSheet = null;

        try {
        	spriteSheet = ImageIO.read(getClass().getResource("/blocks/spriteTest.png"));
            System.out.println("SpriteSheetLoaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < frames; i++)
        {
        	sprites[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }


	public void draw(Graphics g) {
		g.drawImage(sprites[currentFrame / repeatFrames], 300, 275, SPRITE_WIDTH, SPRITE_HEIGHT, null);
		if (currentFrame == 5)
		{
			System.out.println("frame test");
		}
		if (currentFrame < (finalFrame * repeatFrames) - 1)
			currentFrame++;
		else
			currentFrame = 0;
	}

}
