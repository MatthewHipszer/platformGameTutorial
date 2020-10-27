package net.gameState;

import java.awt.Graphics;

import net.entities.Player;
import net.maps.Map;
import net.objects.Block;

public class Level1State extends GameState {

	private Player player;
	private Map map;
	
	public Level1State(GameStateManager gsm) {
		super(gsm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		player = new Player(30,30);	
		map = new Map("/maps/map1.map");
		
		xOffset = 0;
		yOffset = 0;
	}

	@Override
	public void tick() {
		player.tick(map.getBlocks(), map.getMovingBlocks(), map.getCollisionLines());
		map.tick();
	}

	@Override
	public void draw(Graphics g) {
		player.draw(g);
		map.draw(g);
	}

	@Override
	public void keyPressed(int k) {
		player.keyPressed(k);
	}

	@Override
	public void keyReleased(int k) {
		player.keyReleased(k);
	}

}
