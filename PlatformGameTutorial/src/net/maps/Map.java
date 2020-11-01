package net.maps;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.objects.Block;
import net.objects.CollisionLine;
import net.objects.MovingBlock;

public class Map {

	public enum LineType {
        WALL, FLOOR, SEMISOLIDFLOOR, DOWNWARDSLOPE, UPWARDSLOPE, CEILING
    }


	private String line;
	private int width, height;

	private Block[][] blocks;
	private ArrayList<MovingBlock> movingBlocks;
	private ArrayList<CollisionLine> collisionLines;


	public Map(String loadPath) {
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
				// blocks[i][j].draw(g);
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

		// list walls first
		// make sure walls are bottom(y1) to top(y2)
		collisionLines = new ArrayList<CollisionLine>();
		collisionLines.add(new CollisionLine(400, 100, 400, -200, LineType.WALL));				//0
		collisionLines.add(new CollisionLine(3500, 700, 3500, 000, LineType.WALL));				//1
		collisionLines.add(new CollisionLine(3750, 250, 3750, 000, LineType.WALL));				//2
		collisionLines.add(new CollisionLine(4000, -490, 4000, -800, LineType.WALL));			//3
		collisionLines.add(new CollisionLine(3500, 100, 3750, 100, LineType.FLOOR));			//4
		collisionLines.add(new CollisionLine(0, 320, 1100, 320, LineType.FLOOR));				//5
		collisionLines.add(new CollisionLine(100, 200, 500, 200, LineType.SEMISOLIDFLOOR));		//6
		collisionLines.add(new CollisionLine(000, 100, 400, 100, LineType.SEMISOLIDFLOOR));		//7
		collisionLines.add(new CollisionLine(400, 100, 600, 200, LineType.DOWNWARDSLOPE));		//8
		collisionLines.add(new CollisionLine(400, 100, 500, 200, LineType.DOWNWARDSLOPE));		//9
		collisionLines.add(new CollisionLine(800, 250, 2000, 500, LineType.DOWNWARDSLOPE));		//10
		collisionLines.add(new CollisionLine(2000, 500, 2500, 700, LineType.DOWNWARDSLOPE));	//11
		collisionLines.add(new CollisionLine(2500, 700, 2750, 900, LineType.DOWNWARDSLOPE));	//12
		collisionLines.add(new CollisionLine(200, 200, 400, 100, LineType.UPWARDSLOPE));		//13
		collisionLines.add(new CollisionLine(2750, 900, 3500, 700, LineType.UPWARDSLOPE));		//14
		collisionLines.add(new CollisionLine(3500, 700, 3750, 200, LineType.UPWARDSLOPE));		//15
		collisionLines.add(new CollisionLine(3750, 200, 4000, -500, LineType.UPWARDSLOPE));		//16
		collisionLines.add(new CollisionLine(0, -100, 3000, -100, LineType.CEILING));			//17

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
						Integer.parseInt(tokens[1]) * Block.blockSize, Integer.parseInt(tokens[2]),
						Integer.parseInt(tokens[3]) * Block.blockSize, Integer.parseInt(tokens[4]) * Block.blockSize));
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
