package net.maps;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.gameState.GameState;
import net.objects.Block;
import net.objects.CollisionLine;
import net.objects.MovingBlock;

public class Map {

	private String path;
	private String line;
	private int width, height;

	private Block[][] blocks;
	private ArrayList<MovingBlock> movingBlocks;
	private ArrayList<CollisionLine> collisionLines;

	public Map(String loadPath) {
		path = loadPath;
		loadMap(loadPath);
	}

	public void tick() {
		for (int i = 0; i < movingBlocks.size(); i++) {
			movingBlocks.get(i).tick();
		}
	}

	public void draw(Graphics g) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//blocks[i][j].draw(g);
			}
		}
		for (int i = 0; i < movingBlocks.size(); i++) {
			movingBlocks.get(i).draw(g);
		}
		for (int i = 0; i < collisionLines.size(); i++) {
			collisionLines.get(i).draw(g);
		}
	}

	public void loadMap(String loadPath) {

		//list walls first
		//make sure walls are bottom(y1) to top(y2)
		//you could code around that with a minimum check, but that seems less efficient
		//0 = wall
		//1 = floor
		//2 = semisolid floor
		//3 = downward slope
		//4 = upward slope
		//5 = ceiling
		collisionLines = new ArrayList<CollisionLine>();
		collisionLines.add(new CollisionLine(400, 100, 400, -200, 0));
		collisionLines.add(new CollisionLine(3500, 700, 3500, 000, 0));
		collisionLines.add(new CollisionLine(3750, 250, 3750, 000, 0));
		collisionLines.add(new CollisionLine(4000, -490, 4000, -800, 0));
		collisionLines.add(new CollisionLine(3500, 000, 3750, 000, 1));
		collisionLines.add(new CollisionLine(400, 100, 600, 100, 1));
		collisionLines.add(new CollisionLine(0, 300, 1000, 300, 1));
		collisionLines.add(new CollisionLine(100, 200, 500, 200, 2));
		collisionLines.add(new CollisionLine(000, 100, 400, 100, 2));
		collisionLines.add(new CollisionLine(400, 100, 500, 200, 3));
		collisionLines.add(new CollisionLine(1000, 300, 2000, 500, 3));
		collisionLines.add(new CollisionLine(2000, 500, 2500, 700, 3));
		collisionLines.add(new CollisionLine(2500, 700, 2750, 900, 3));
		collisionLines.add(new CollisionLine(200, 200, 400, 100, 4));
		collisionLines.add(new CollisionLine(2750, 900, 3500, 700, 4));
		collisionLines.add(new CollisionLine(3500, 700, 3750, 200, 4));
		collisionLines.add(new CollisionLine(3750, 200, 4000, -500, 4));
		collisionLines.add(new CollisionLine(0, -100, 3000, -100, 5));

		InputStream is = this.getClass().getResourceAsStream(loadPath);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			width = Integer.parseInt(br.readLine());
			height = Integer.parseInt(br.readLine());
			blocks = new Block[height][width];


			for (int y = 0; y < height; y++) {
				String line = br.readLine();
				String[] tokens = line.split("\\s+");
				for (int x = 0; x < width; ++x) {
					blocks[y][x] = new Block(x * Block.blockSize, y * Block.blockSize, Integer.parseInt(tokens[x]));
				}
			}

			line = br.readLine();
			line = br.readLine();
			int length = Integer.parseInt(line);

			movingBlocks = new ArrayList<MovingBlock>();

			for (int i = 0; i < length; i++) {
				line = br.readLine();
				String[] tokens = line.split("\\s+");

				movingBlocks.add(new MovingBlock(Integer.parseInt(tokens[0]) * Block.blockSize,
												 Integer.parseInt(tokens[1]) * Block.blockSize,
												 Integer.parseInt(tokens[2]),
												 Integer.parseInt(tokens[3]) * Block.blockSize,
												 Integer.parseInt(tokens[4]) * Block.blockSize));
			}

		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

	}

	public Block[][] getBlocks() {
		return blocks;
	}

	public ArrayList<MovingBlock> getMovingBlocks() {
		return movingBlocks;
	}

	public ArrayList<CollisionLine> getCollisionLines() {
		return collisionLines;
	}
}
