package net.gameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import net.codejava.GamePanel;

public class MenuState extends GameState {

	private String[] options = {"start", "help", "quit"};
	private int currentSelection = 0;
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		g.setColor(new Color(50, 150, 200));
		g.fillRect(0,0,GamePanel.WIDTH, GamePanel.HEIGHT);
		
		for (int i = 0; i < options.length; i++) {
			if (i == currentSelection)
			{
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.WHITE);
			}
			//g.drawLine(GamePanel.WIDTH / 2, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT);
			g.setFont(new Font("Arial", Font.PLAIN, 72));
			g.drawString(options[i], GamePanel.WIDTH / 2 - 75, 150 + i * 150);
			
			
			
		}
	}

	@Override
	public void keyPressed(int k) {
		// TODO Auto-generated method stub
		   
		/*
		Find a way to make this a switch statement
        k = KeyEvent;
		switch (KeyEvent) 
        {
        }
        */
		
		if (k == KeyEvent.VK_DOWN)
		{
			currentSelection++;
			if (currentSelection >= options.length) {
				currentSelection = 0;
			}
			
		} else if (k == KeyEvent.VK_UP) {
			currentSelection--;
			if (currentSelection < 0) {
				currentSelection = options.length - 1;
			}
		}
		if (k == KeyEvent.VK_ENTER) {
			if (currentSelection == 0) {
				gsm.states.push(new Level1State(gsm));
			} else if (currentSelection == 1) {
				//help
			} else if (currentSelection == 2){
				//quit
				System.exit(0);
			}
		}
	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub
		
	}

}
