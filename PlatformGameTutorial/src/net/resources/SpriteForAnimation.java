package net.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteForAnimation {

	//Create the spriteSheet
    private static BufferedImage spriteSheet;
    private int width;
    private int height;
    private int x;
    private int y;
    BufferedImage sprites[];

    //Current frame the animation is
    private int currentFrame = 0;

    //Final frame of the animation
    private int finalFrame;

    //The amount of times to repeat each frame
    //Prevents animations with less frames from looking awful
    private int repeatFrames;


    //Currently only ever does the top row of an animation
    public SpriteForAnimation(int x, int y, int width, int height, int value, int frames, int repeatFrames, String path)
    {
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    	this.repeatFrames = repeatFrames;
    	finalFrame = frames;
    	sprites = new BufferedImage[frames];
    	spriteSheet = null;
        try {
        	spriteSheet = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < frames; i++)
        {
        	sprites[i] = spriteSheet.getSubimage(i * width, value * height, width, height);
        }
    }


    //Draws the image on the current frame
	public void draw(Graphics g) {
		g.drawImage(sprites[currentFrame / repeatFrames], x, y, width, height, null);
		if (currentFrame < (finalFrame * repeatFrames) - 2)
			currentFrame++;
		else
			currentFrame = 0;
	}

	public int getFrame() {
		return currentFrame;
	}

}
