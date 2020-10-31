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
    BufferedImage sprites[];

    //Current frame the animation is
    private int currentFrame = 0;

    //Final frame of the animation
    private int finalFrame;

    //The amount of times to repeat each frame
    //Prevents animations with less frames from looking awful
    private int repeatFrames;


    //Currently only ever does the top row of an animation
    public SpriteForAnimation(int x, int y, int value, int frames, int repeatFrames)
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
        	sprites[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, value * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }


    //Draws the image on the current frame
	public void draw(Graphics g) {
		g.drawImage(sprites[currentFrame / repeatFrames], 300, 275, SPRITE_WIDTH, SPRITE_HEIGHT, null);
		if (currentFrame < (finalFrame * repeatFrames) - 1)
			currentFrame++;
		else
			currentFrame = 0;
	}

}
