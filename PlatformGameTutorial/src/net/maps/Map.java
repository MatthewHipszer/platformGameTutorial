package net.maps;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.objects.CollisionLine;
import net.objects.MovingBlock;

public class Map {

	public enum LineType {
        WALL, FLOOR, SEMISOLIDFLOOR, DOWNWARDSLOPE, UPWARDSLOPE, CEILING
    }

	private ArrayList<MovingBlock> movingBlocks;
	private ArrayList<CollisionLine> collisionLines;

	private Level1Test lvl1Test;


	public Map(String loadPath) {
		loadMap(loadPath);
	}

	public void tick() {
		//for (int i = 0; i < movingBlocks.size(); i++) {
		//	movingBlocks.get(i).tick();
		//}
	}

	public void draw(Graphics g) {
		lvl1Test.draw(g);
		/*
		for (int i = 0; i < movingBlocks.size(); i++) {
			movingBlocks.get(i).draw(g);
		}
		*/
		for (int i = 0; i < collisionLines.size(); i++) {
			collisionLines.get(i).draw(g);
		}
	}

	public void loadMap(String loadPath) {

		// list walls first
		collisionLines = new ArrayList<CollisionLine>();

		//This is just an image right now
		//It would be nice if everything could be moved into this
		lvl1Test = new Level1Test(0, 0);

		InputStream is = this.getClass().getResourceAsStream(loadPath);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			//width = Integer.parseInt(br.readLine());
			//height = Integer.parseInt(br.readLine());
			//blocks = new Block[height][width];

			String line = br.readLine();
			while (!line.equals("end")) {
			line = br.readLine();

			   if (!line.equals("end")) {
				   String[] tokens = line.split(",");
				   LineType lineType = LineType.valueOf(tokens[4]);
				   collisionLines.add(new CollisionLine(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), lineType));
			   }
			}
			/*
			for (int y = 0; y < height; y++) {
				String line = br.readLine();
				String[] tokens = line.split(",");
				for (int x = 0; x < width; ++x) {
					blocks[y][x] = new Block(x * Block.blockSize, y * Block.blockSize, Integer.parseInt(tokens[x]));
				}
			}
			*/

			/*
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
			*/
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<MovingBlock> getMovingBlocks() {
		return movingBlocks;
	}

	public ArrayList<CollisionLine> getCollisionLines() {
		return collisionLines;
	}
}
