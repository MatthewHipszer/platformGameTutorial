package net.maps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

import net.codejava.GamePanel;
import net.gameState.GameState;



public class Dialogue {

	public static int x = 3;
	public static boolean dialogueOpen = false;
	private static int dialogueValue = 0;
	private static BufferedImage dialogueBoxImg;
	//private Node dialogueBox;
	private static JTextArea dialogueTextArea;
	//private String dialogue;
	private static String[] dialoguePieces;
	private static int dialogueMargin = 20;
	private static Font dialogueFont = new Font ("TimesRoman", Font.PLAIN, 24);
	
	public Dialogue (int x, int y) {
		try {
			dialogueBoxImg = ImageIO.read(getClass().getResourceAsStream("/textboxes/dialogueBox.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//createDialogue("This is a test string. Just testing stuff.~Still testing stuff.");
	}
	
	public void tick () {

	}
	
	public static void createDialogue(String dialogueStr) 
    {
    	dialogueOpen = true;
    	dialogueValue = 0;	
    	dialoguePieces = dialogueStr.split("~");
    	dialogueTextArea = new JTextArea(dialoguePieces[dialogueValue]);
    	dialogueTextArea.setLineWrap(true);
    	dialogueTextArea.setWrapStyleWord(true);
    	dialogueTextArea.setBounds(0, 0, dialogueBoxImg.getWidth() - (dialogueMargin * 2), 
                                         dialogueBoxImg.getHeight() - (dialogueMargin * 2));
    	dialogueTextArea.setForeground(Color.BLACK);
    	dialogueTextArea.setFont(dialogueFont);
    }
	
	public void draw (Graphics g) {
	
		if (dialogueOpen)
		{
			g.drawImage(dialogueBoxImg, (GamePanel.WIDTH - dialogueBoxImg.getWidth()) / 2, 
                    (GamePanel.HEIGHT - dialogueBoxImg.getHeight()), 
                    dialogueBoxImg.getWidth(), dialogueBoxImg.getHeight(), null);
			paintDialogue(g, (GamePanel.WIDTH - dialogueBoxImg.getWidth()) / 2 + dialogueMargin, 
					           (GamePanel.HEIGHT - dialogueBoxImg.getHeight()) + dialogueMargin, 
					            dialogueBoxImg.getWidth() - (dialogueMargin * 2), 
					            dialogueBoxImg.getHeight() - (dialogueMargin * 2));
		}
	}
	
	public static void changeBoolean()
	{
		System.out.println("dialogue: " + dialogueOpen);
		dialogueOpen = !dialogueOpen;
		System.out.println("dialogue: " + dialogueOpen);
		
	}
	
	 static void paintDialogue(Graphics g, int x, int y, int w, int h) { 
	        Graphics g2 = g.create(x, y, w, h); // Use new graphics to leave original graphics state unchanged
	        dialogueTextArea.paint(g2);
	    }
	 
	 public static void advanceDialogue()
	    {
	    	if (++dialogueValue < dialoguePieces.length)
	    		dialogueTextArea.setText(dialoguePieces[dialogueValue]);
	    	else
	    		dialogueOpen = false;
	    }
}