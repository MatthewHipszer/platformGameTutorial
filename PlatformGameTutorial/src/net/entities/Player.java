package net.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import net.codejava.GamePanel;
import net.gameState.GameState;
import net.objects.Block;
import net.objects.CollisionLine;
import net.objects.MovingBlock;

public class Player {

	private double x, y;
	private int width, height;

	// movement
	private double moveSpeed = 5;
	private boolean right = false;
	private boolean left = false;
	private boolean down = false;
	private boolean jumping = false;
	private boolean falling = false;
	private double jumpSpeed = 5;
	private double currentJumpSpeed = jumpSpeed;
	private double maxFallSpeed = 5;
	private double currentFallSpeed = 0.2;
	private double wallFallSpeed = 0.1;
	private boolean wallClingRight = false;
	private boolean wallClingLeft = false;
	private boolean wallJumpFromRight = false;
	private boolean wallJumpFromLeft = false;
	private int wallJumpFrames = 10;
	private int clungWall = 0;
	private boolean dropable = false;

	private boolean grounded = false;

	public Player(int width, int height) {
		x = GamePanel.WIDTH / 3;
		y = GamePanel.HEIGHT / 2;
		this.width = width;
		this.height = height;
	}

	public void tick(Block[][] b, ArrayList<MovingBlock> movingBlocks, ArrayList<CollisionLine> cL) {

		// Collisions
		/*
		 * for (int i = 0; i < b.length; i++) for (int j = 0; j < b[0].length; j++) {
		 *
		 * if (b[i][j].getID() != 0) { //right side if (Collision.playerBlock(new
		 * Point(testX, testY + 2), b[i][j]) || Collision.playerBlock(new Point(testX,
		 * testY + height - 1), b[i][j])) { right = false; } //left side if
		 * (Collision.playerBlock(new Point(testX - width - 1, testY + 2), b[i][j]) ||
		 * Collision.playerBlock(new Point(testX - width - 1, testY + height - 1),
		 * b[i][j])) { left = false; } //top if (Collision.playerBlock(new Point(testX -
		 * width + 1, testY), b[i][j]) || Collision.playerBlock(new Point(testX - 2,
		 * testY), b[i][j])) { jumping = false; falling = true; } //bottom if
		 * (Collision.playerBlock(new Point(iX + (int)GameState.xOffset + 2, iY +
		 * (int)GameState.yOffset + height + 1), b[i][j]) || Collision.playerBlock(new
		 * Point(iX + width + (int)GameState.xOffset - 2, iY + (int)GameState.yOffset +
		 * height + 1), b[i][j])) { falling = false; topCollision = true; y =
		 * b[i][j].getY() - height - GameState.yOffset; //System.out.println("boxY: " +
		 * y); } else { if (!topCollision && !jumping) { falling = true; } } } }
		 *
		 * for (int i = 0; i < movingBlocks.size(); i++) { if
		 * (movingBlocks.get(i).getID() != 0) { if (movingBlocks.get(i).getID() != 0) {
		 * //right side if (Collision.playerMovingBlock(new Point(testX, testY + 2),
		 * movingBlocks.get(i)) || Collision.playerMovingBlock(new Point(testX, testY +
		 * height - 1), movingBlocks.get(i))) { right = false; } //left side if
		 * (Collision.playerMovingBlock(new Point(testX - width - 1, testY + 2),
		 * movingBlocks.get(i)) || Collision.playerMovingBlock(new Point(testX - width -
		 * 1, testY + height - 1), movingBlocks.get(i))) { left = false; } //top if
		 * (Collision.playerMovingBlock(new Point(testX - width + 1, testY),
		 * movingBlocks.get(i)) || Collision.playerMovingBlock(new Point(testX - 2,
		 * testY), movingBlocks.get(i))) { jumping = false; falling = true; } //bottom
		 * if (Collision.playerMovingBlock(new Point(testX - width + 2, testY + height +
		 * 1), movingBlocks.get(i)) || Collision.playerMovingBlock(new Point(testX - 2,
		 * testY + height + 1), movingBlocks.get(i))) { falling = false; topCollision =
		 * true;
		 *
		 * GameState.xOffset += movingBlocks.get(i).getMove(); y =
		 * movingBlocks.get(i).getY() - height - GameState.yOffset;
		 *
		 *
		 * } else { if (!topCollision && !jumping) { falling = true; } } } } }
		 */

		/*
		 * intersectsLine(double x1, double y1, double x2, double y2) Tests if the line
		 * segment from (x1,y1) to (x2,y2) intersects this line segment.
		 */

		// find a way to check if the y value of player started above a line and then
		// was below without the x value of the player
		// being greater than the max x value of the line segment or being less than the
		// min x value of the line segment?

		// cL.getX1();
		// cL.getX2();
		// cL.getY1();

		// System.out.println("y: " + y);

		// System.out.println("yOffset: " + GameState.yOffset);

		// obtains bottom left corner value for player
		// player is 30 wide

		// System.out.println("test values:");
		// System.out.println(cL.x1);
		// 300
		// System.out.println(x);
		// 275
		// System.out.println(y);
		// 30
		// System.out.println(height);
		// 500
		// System.out.println(cL.y1);

		// System.out.println(cL.x2);
		// System.out.println(cL.y2);

		// this doesn't collide every frame that you are on the ground
		// this is causing jitters

		double prevLeftX = GameState.prevXOffset + x;
		double prevRightX = prevLeftX + width;
		double prevY = GameState.prevYOffset + y + height;
		double leftX = GameState.xOffset + x;
		double currentY = GameState.yOffset + y + height;
		double rightX = leftX + width;
		// boolean collided = false;

		for (int i = 0; i < cL.size(); i++) {
			// System.out.println(falling);
			// System.out.println(currentFallSpeed);
			// System.out.println("i: " + i);
			double slope = (cL.get(i).y2 - cL.get(i).y1) / (cL.get(i).x2 - cL.get(i).x1);
			double intercept = cL.get(i).y1 - (slope * cL.get(i).x1);
			double xx = leftX + (width / 2);
			double yy = slope * xx + intercept;

			// right collision
			// if (Collision.playerBlock(new Point(testX, testY + 2), b[i][j]) ||
			// Collision.playerBlock(new Point(testX, testY + height - 1), b[i][j])) {
			// right = false;
			// }
			// There is something pretty wrong here.
			// Seems like it is checking a huge area

			// System.out.println("prev left x: " + prevLeftX);
			// System.out.println("left x: " + leftX);
			// System.out.println("prev right x : " + prevRightX);
			// System.out.println("right x: " + rightX);
			// System.out.println("prev left y: " + prevY);
			// System.out.println("current y: " + currentY);
			// System.out.println("minX: " + cL.getMinX());
			// System.out.println("maxX: " + cL.getMaxX());
			// System.out.println("Y: " + cL.getY());

			// right

			// System.out.println("currentY: " + currentY);
			// System.out.println("currentY: " + (currentY - height));
			if (cL.get(i).intersectsLine(rightX - (width / 2), currentY, rightX + 5, currentY) || cL.get(i)
					.intersectsLine(rightX - (width / 2), currentY - height, rightX + 5, currentY - height)) {

				// System.out.println(i);
				// System.out.println("test right");
				System.out.println("slope? " + slope);

				// System.out.println(cL.get(i).y1);
				// I think this slope formula is wrong?
				// I think it also needs to check for greater than 1 slopes
				if (slope < -1) {
					if (right) {
						double test = moveSpeed + 2 * slope;
						falling = false;
						if (test >= 1) {
							// Seems good
							System.out.println("steep slope");
							moveSpeed = test;
							GameState.yOffset = yy - y - 31;
							// collided = true;
						}
						// Not quite working
						else if (slope < -99999) {
							System.out.println("wall");
							if ((right) && (!left)) {
								System.out.println("movespeed: " + moveSpeed);
								jumping = false;
								falling = false;
								GameState.yOffset--;
								GameState.xOffset -= moveSpeed;
								wallClingRight = true;
								clungWall = i;
								wallFallSpeed = 0.1;
								// collided = true;
								// probably want to unwallcling around height from y1
							}
						}
						// Pretty good (not sure if needed, may also have slight jitter)
						else {
							System.out.println("very steep slope");
							moveSpeed = 0;
							moveSpeed = 0.5;
							// GameState.xOffset = xx + x;
							GameState.yOffset = yy - y - 31 - 1;
							// collided = true;
						}
					}
				}
				// Downward angled slope collision
				// This isn't covering slopes that are downward, but shallow
				// The way slopes work when the y axis is reversed
				// are making calculations like this very annoying.
				// May want to check if using angles will work better.
				// Might be inefficient though.

				else if (slope >= 1) {
					System.out.println("slope greater than 1");
					if (right)
						GameState.xOffset -= moveSpeed;
				}

				// may need to move this
				// if (cL.get(clungWall).y1 - currentY <= 0)
				// {
				// System.out.println("uncling");
				// wallClingRight = false;
				// }
			}

			// left
			if (cL.get(i).intersectsLine(leftX, currentY, leftX - 5, currentY)
					|| cL.get(i).intersectsLine(leftX, currentY - height, leftX - 5, currentY - height)) {

				// System.out.println(i);
				// System.out.println("test left");
				// System.out.println("slope? " + slope);
				if (slope < -99999) {
					if (left)
						GameState.xOffset += moveSpeed;
					// System.out.println("slope test2");
				}

			}

			// Top collision
			if (cL.get(i).intersectsLine(leftX + (width / 2), prevY - height + 5, leftX + (width / 2),
					prevY - height - 5)) {
				// Check if id is semisolid floor
				if ((cL.get(i).getID() != 2)) {
					System.out.println("top collision: " + i);
					jumping = false;
					if (!wallClingLeft && !wallClingRight) {
						falling = true;
						jumping = false;
					} else if (wallFallSpeed < 0.2) {
						// TODO prevent walljumping through ceilings
						jumping = false;
						System.out.println("top collision test");
						GameState.yOffset = yy - y + 1;
					}
					if (wallJumpFromRight || wallJumpFromLeft) {
						wallJumpFrames = 0;
						GameState.yOffset = yy - y + 1;
					}
					// collided = true;
					// Makes roofs sticky
					// GameState.yOffset = yy - y;

				}
			}

			// Bottom collision
			if ((cL.get(i).intersectsLine(leftX + (width / 2), prevY - 5, leftX + (width / 2), prevY + 5)) && (!jumping)
					&& (!wallClingRight)) {
				// System.out.println("bottom collision");
				if (cL.get(i).x1 != cL.get(i).x2) {
					// System.out.println("test1");
					falling = false;
					grounded = true;
					GameState.yOffset = yy - y - 31;
					if (cL.get(i).getID() == 2)
						dropable = true;
				}
			}

			else {
				if (!grounded && !jumping && !wallClingRight) {
					// System.out.println("bottom collision");
					falling = true;
					dropable = false;
				}
			}
		}

		// System.out.println("prev left x: " + prevLeftX);
		// System.out.println("left x: " + leftX);
		// System.out.println("prev right x : " + prevRightX);
		// System.out.println("right x: " + rightX);
		// System.out.println("prev left y: " + prevY);
		// System.out.println("left y: " + currentY);
		// System.out.println("minX: " + cL.getMinX());
		// System.out.println("maxX: " + cL.getMaxX());
		// System.out.println("Y: " + cL.getY());

		grounded = false;

		GameState.prevXOffset = GameState.xOffset;
		GameState.prevYOffset = GameState.yOffset;

		if ((right) && (!wallJumpFromLeft || !wallJumpFromRight)) {
			GameState.xOffset += moveSpeed;
			moveSpeed = 5;
		}
		if ((left) && (!wallJumpFromLeft || !wallJumpFromRight)) {
			GameState.xOffset -= moveSpeed;
		}

		if (falling) {
			currentJumpSpeed = jumpSpeed;
			GameState.yOffset += currentFallSpeed;
			if (currentFallSpeed < maxFallSpeed) {
				currentFallSpeed += 0.2;
			}

		}
		if (!falling) {
			// increasing this may make downward collisions more consistent
			// assuming it isn't >= 1 (maybe not who knows)
			currentFallSpeed = 0.2;
		}
		if (wallClingLeft || wallClingRight) {
			System.out.println("wall test: " + wallFallSpeed);
			currentJumpSpeed = jumpSpeed;
			GameState.yOffset += wallFallSpeed;
			if (wallFallSpeed < (maxFallSpeed / 2)) {
				wallFallSpeed += 0.1;
			}

			if ((cL.get(clungWall).y1 - currentY <= 0) || (cL.get(clungWall).y2 - currentY > 0)) {
				System.out.println("uncling");
				wallClingRight = false;
			}

		}

		// wallJumping forcing an animation is cool for later visuals,
		// but it is currently a little jank if you are already hitting the roof
		if (wallJumpFromLeft) {
			GameState.yOffset -= currentJumpSpeed;
			GameState.xOffset += 5;
			wallJumpFrames--;
			if (wallJumpFrames <= 0) {
				wallJumpFrames = 10;
				wallJumpFromRight = false;
			}
			currentJumpSpeed -= 0.1;
			if (currentJumpSpeed <= 0) {
				currentJumpSpeed = jumpSpeed;
				jumping = false;
				System.out.println("wallJumpFromLeft");
				falling = true;
			}
		} else if (wallJumpFromRight) {
			GameState.yOffset -= currentJumpSpeed;
			GameState.xOffset -= 5;
			wallJumpFrames--;
			if (wallJumpFrames <= 0) {
				wallJumpFrames = 10;
				wallJumpFromRight = false;
			}
			currentJumpSpeed -= 0.1;
			if (currentJumpSpeed <= 0) {
				currentJumpSpeed = jumpSpeed;
				jumping = false;
				System.out.println("wallJumpFromRight");
				falling = true;
			}
		} else if (jumping) {
			GameState.yOffset -= currentJumpSpeed;
			currentJumpSpeed -= 0.1;
			if (currentJumpSpeed <= 0) {
				currentJumpSpeed = jumpSpeed;
				jumping = false;
				System.out.println("jumping");
				falling = true;
			}
		}
		// 5
		// 4.9
		// 4.8
		// ...
		// 0.2
		// 0.1
		// goes back to 5
		// starts falling

		// set cjs to 5 (which it already was)
		// falls at 0.1
		// 0.2

		/*
		 * if (falling ) { currentJumpSpeed = jumpSpeed; GameState.yOffset +=
		 * currentFallSpeed; if (currentFallSpeed < maxFallSpeed) { currentFallSpeed +=
		 * 0.1; } if (!falling) { //increasing this may make downward collisions more
		 * consistent //assuming it isn't >= 1 (maybe not who knows) currentFallSpeed =
		 * 0.1; }
		 *
		 */
		// System.out.println(wallClingRight);
	}

	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect((int) x, (int) y, width, height);

	}

	// If I hit right then left, I don't uncling from the wall
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_D) {

			System.out.println("d");
			right = true;
			wallClingLeft = false;
		}
		if (k == KeyEvent.VK_A) {
			System.out.println("a");

			left = true;
			wallClingRight = false;
			// System.out.println(wallClingRight);
		}
		if (k == KeyEvent.VK_S) {
			System.out.println("s");

			down = true;
			// wallClingRight = false;
			// wallClingLeft = false;
		}
		if (k == KeyEvent.VK_SPACE && !jumping && !falling) {
			jumping = true;
			if (wallClingLeft) {
				// TODO
			} else if (wallClingRight) {
				wallJumpFromRight = true;
			}
			wallClingLeft = false;
			wallClingRight = false;
			if ((dropable) && (down)) {
				System.out.println("testing");
				currentJumpSpeed = jumpSpeed;
				GameState.yOffset += currentFallSpeed;
				if (currentFallSpeed < maxFallSpeed) {
					currentFallSpeed += 0.2;
				}
				// I'd like to make these smaller, but it currently
				// interferes with the don't fall through the floor code.
				// Mostly effects positive slopes (downward)
				GameState.prevYOffset = GameState.yOffset + 6;
				GameState.yOffset += 6;
				falling = true;
				jumping = false;
				dropable = false;
			}
		}
	}

	public void keyReleased(int k) {
		if (k == KeyEvent.VK_D) {
			right = false;
		}
		if (k == KeyEvent.VK_A) {
			left = false;
		}
		if (k == KeyEvent.VK_S) {
			down = false;
		}
		if (k == KeyEvent.VK_SPACE) {
			jumping = false;
		}
	}
}
