package net.entities;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import net.maps.Dialogue;
import net.codejava.GamePanel;
import net.gameState.GameState;
import net.maps.Map.LineType;
import net.objects.CollisionLine;
import net.objects.MovingBlock;
import net.physics.HitBox;
import net.resources.SpriteForAnimation;

public class Player {

	public enum Animation {
		IDLERIGHT, IDLELEFT, RIGHT, LEFT, JUMPRIGHT, JUMPLEFT, FALLRIGHT, FALLLEFT, ATTACKINGRIGHT, ATTACKINGLEFT,
		ATTACKINGJUMPRIGHT, ATTACKINGJUMPLEFT
	}

	// Player values
	public static double x, y;
	private int width, height;
	private int rLPadding = 6;
	private int uDPadding = 12;

	// Movement
	private double moveSpeed = 5;
	private double currentMoveSpeed = 5;
	private int facing = 0;
	// Directional booleans
	private boolean right, left, down, jumping, falling, wallClingRight, wallClingLeft, wallJumpFromRight,
			wallJumpFromLeft,
			// Misc. booleans
			dropable, dontChangeMoveSpeedTest, bottomCollision, attacking, dropping = false;

	private double jumpSpeed = 5;
	private double currentJumpSpeed = jumpSpeed;
	private double maxFallSpeed = 5;
	private double currentFallSpeed = 0.2;
	private double wallFallSpeed = 0.1;
	private int wallJumpFrames = 10;
	private int clungWall = 0;
	private int dropFloor = -1;

	private HitBox hitBoxRight, hitBoxLeft, hitBoxTop, hitBoxBottom;
	private double tempx, tempy;
	private int attackTime = 36;

	private HitBox attackBoxRight, attackBoxLeft, attackBoxJump;

	// Animations
	private SpriteForAnimation[] playerAnimations, attackAnimations;
	private int currentPlayerAnimation;

	public Player(int width, int height) {

		// Place player 1/3 of the screen to the right
		x = GamePanel.WIDTH / 3;
		// Place player in the middle of the screen vertically
		// Changed to 1/3 for map testing
		// Should probably be changed back after making a real map.
		y = GamePanel.HEIGHT / 3;
		// Set player dimensions
		this.width = width;
		this.height = height;

		// Has a slope to floor issue which is caused by the same problem that ___
		// was causing the other slope to floor issue. Should be solvable with /
		// very similar code. /

		// If you start falling during an attack it becomes a jump attack.

		// Need to find a way to make animations accurate when they are different
		// lengths.
		// Could use more than one variable for attackTime, or set the attack time on
		// attack press.
		// Would be nice to find a way to make it simpler though, like a generic formula
		// based
		// on some variable from the animation itself (such as how many frames the
		// animation has)

		// Can fall off wall and have the wall fall speed instead
		// of normal fall speed. This only seems to happen with left walls.
		// This is likely because I haven't made the left wall cling code snippet.
		// Can't jump while holding walking into the left wall.
		// This is likely the same problem with the left wall cling code.
		// All of the left code should be easy enough to fix when I decide to do it.
		// Not necessarily a problem, but you appear to run very fast down hill.
		// You don't actually run faster horizontally, but the way the slope is
		// you traverse it very fast vertically.

		// I wonder if it would be good to consider different ways to break out
		// of the collision loops. It could improve efficiency and with good
		// map design it shouldn't be a problem. And example of something that
		// may be good to break is the ledge grab idea below.
		// Or the drop through platform code. It may require more IDs such as
		// wall with ledge vs wall that connects to ceiling, but I think it might
		// be better. Maybe also move all the collision code into methods so that
		// this code is nicer and more readable.

		// TODO I'd like to add a way to climb the ledge of a wall/platform.
		// Walls would probably be pretty easy. I think a check for the top
		// of the wall colliding with the right hitBox would work. Might want it
		// to only apply if it collides near the top of the hitBox to prevent the
		// character from warping too far on the grab.

		// TODO if you must jump into a wall to wall cling, then you can make it
		// so that hitting the bottom of anything removes wall clinging

		// Create hitBoxes
		hitBoxLeft = new HitBox(x, y + rLPadding, rLPadding, height - uDPadding, 0);
		hitBoxRight = new HitBox(x + width - rLPadding, y + rLPadding, rLPadding, height - uDPadding, 0);
		hitBoxBottom = new HitBox(x + rLPadding, y + height - (uDPadding + 1), width - uDPadding, (uDPadding + 2), 0);
		hitBoxTop = new HitBox(x + rLPadding, y, width - uDPadding, (uDPadding + 1), 0);

		// TODO
		// Create hitboxes for attacking
		// For ground attack, I think I just need in front, although putting back
		// as well would probably be fine. Need to think if it would be efficient
		// I think I want air attacking to be a circle around the player.
		// example hitBox:
		attackBoxRight = new HitBox(x + width, y, width, height, 1);
		attackBoxLeft = new HitBox(x - width, y, width, height, 1);
		attackBoxJump = new HitBox(x - width - 7, y - width, 104, 104, 1);

		// Create animations
		playerAnimations = new SpriteForAnimation[12];
		attackAnimations = new SpriteForAnimation[4];
		// This won't work if the animations are different length
		// but for now this will work
		for (int i = 0; i < playerAnimations.length; i++) {
			playerAnimations[i] = new SpriteForAnimation((int) x, (int) y, width, height, i, 6, 6,
					"/blocks/spriteTest.png");
		}
		attackAnimations[0] = new SpriteForAnimation((int) x + width, (int) y, width, height, 0, 6, 6,
				"/blocks/attackAnimationsTest.png");
		attackAnimations[1] = new SpriteForAnimation((int) x - width, (int) y, width, height, 1, 6, 6,
				"/blocks/attackAnimationsTest.png");
		// TODO Either this or attackTime needs to be looked at to see if I can make
		// this work better.
		attackAnimations[2] = new SpriteForAnimation((int) x - width - 7, (int) y - width, 104, 104, 0, 8, 8,
				"/blocks/attackAnimationsTest2.png");
		attackAnimations[3] = new SpriteForAnimation((int) x - width - 7, (int) y - width, 104, 104, 1, 8, 8,
				"/blocks/attackAnimationsTest2.png");
		currentPlayerAnimation = 0;





		// Display values once for checking stuff purposes
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("left box: " + hitBoxLeft.getBounds());
		System.out.println("right box: " + hitBoxRight.getBounds());
		System.out.println("bottom box: " + hitBoxBottom.getBounds());
		System.out.println("top box: " + hitBoxTop.getBounds());
	}

	public void tick(ArrayList<MovingBlock> movingBlocks, ArrayList<CollisionLine> cL) {

		double leftX = GameState.xOffset + x;
		double currentY = GameState.yOffset + y + height;

		// Check Collision detection
		checkLineCollisions(leftX, currentY, cL);

		// Reset booleans
		//dontChangeMoveSpeedTest = false;
		bottomCollision = false;

		// Replaces the previous frames offsets with this one before moving this
		// offset to a new location
		GameState.prevXOffset = GameState.xOffset;
		GameState.prevYOffset = GameState.yOffset;

		// Move things around
		changePosition(currentY, cL);

		// check if attackTime is done
		if (attacking) {
			attackTime--;
			if (attackTime == 0) {
				attacking = false;
				attackTime = 36;

				if (facing == 0) {
					if (right) {
						setAnimation(Animation.RIGHT);
					} else {
						setAnimation(Animation.IDLERIGHT);
					}
				} else {
					if (left) {
						setAnimation(Animation.LEFT);
					} else {
						setAnimation(Animation.IDLELEFT);
					}
				}
			}
		}
	}

	// Draw method
	public void draw(Graphics g) {
		// Draw the playerBox
		// g.setColor(Color.BLACK);

		// g.fillRect((int) x, (int) y, width, height);

		if (attacking) {
			if (facing == 0) {
				if (jumping || falling) {
					attackAnimations[2].draw(g);
				} else {
					attackAnimations[0].draw(g);
				}
			} else {
				if (jumping || falling) {
					attackAnimations[3].draw(g);
				} else {
					attackAnimations[1].draw(g);
				}
			}
			System.out.println("attackTime: " + attackTime);
			System.out.println("frame: " + attackAnimations[0].getFrame());
			System.out.println("currentAnimation: " + currentPlayerAnimation);


		}

		// Draw the current animation
		playerAnimations[currentPlayerAnimation].draw(g);

		// Draw the hitBoxes
		hitBoxRight.draw(g);
		hitBoxLeft.draw(g);
		hitBoxTop.draw(g);
		hitBoxBottom.draw(g);

		attackBoxRight.draw(g);
		attackBoxLeft.draw(g);
		attackBoxJump.draw(g);
	}

	private void checkLineCollisions(double leftX, double currentY, ArrayList<CollisionLine> cL) {
		// Obtain calculations of where the player actually
		// appears because they do not actually move
		// Also helps with the hitBoxes which also do not move

		// Loop through all collision lines
		// Can probably put a check at the front to avoid checking lines
		// that are further away than a certain distance
		for (int i = 0; i < cL.size(); i++) {
			// Obtain slope of the line. Java doesn't care if it is undefined or NaN
			// Therefore this does not need special checks for those here


			double angleInDegrees = cL.get(i).getAngle();

			switch (cL.get(i).getID()) {
			case WALL: {
				//You will need something like this if you want climbable over arching walls.
				//Still of the opinion that I probably don't want these though...
				//topHitBoxCode(cL, i, leftX, currentY);
				rightHitBoxCode(cL, i, leftX, currentY, angleInDegrees, true, false);
				leftHitBoxCode(cL, i, leftX, currentY, angleInDegrees, true, false);
				break;
			}
			case FLOOR: {
				bottomHitBoxCode(cL, i, leftX, currentY, angleInDegrees, 0);
				break;
			}
			case SEMISOLIDFLOOR: {
				bottomHitBoxCode(cL, i, leftX, currentY, angleInDegrees, 2);
				break;
			}
			case DOWNWARDSLOPE: {
				bottomHitBoxCode(cL, i, leftX, currentY, angleInDegrees, 0);
				break;
			}
			case UPWARDSLOPE: {
				bottomHitBoxCode(cL, i, leftX, currentY, angleInDegrees, 1);
				break;
			}
			case CEILING: {
				rightHitBoxCode(cL, i, leftX, currentY, angleInDegrees, false, true);
				leftHitBoxCode(cL, i, leftX, currentY, angleInDegrees, false, true);
				topHitBoxCode(cL, i, leftX, currentY);
				break;
			}
			}
		} // End of collisionLine for loop

	}

	private void topHitBoxCode(ArrayList<CollisionLine> cL, int i, double leftX, double currentY) {
		// ------------------------------------------------------------------------------------------------------
		// | TOP HIT BOX |
		// ------------------------------------------------------------------------------------------------------

		// Top collision
		// this one also seems to just work correctly
		// it won't let me jump when on the very steep slope
		// but that is because the very steep slope is so steep
		// that it goes through the top hitBox.
		// Taller model has made this irrelevant for all but the
		// steepest slopes
		// Moves the hitBox temporarily so it can check for collisions
		tempx = hitBoxTop.x;
		tempy = hitBoxTop.y;
		hitBoxTop.x = leftX + rLPadding;
		hitBoxTop.y = currentY - height;
		// Checks current line for collision
		if (cL.get(i).intersects(hitBoxTop)) {
			System.out.println("top collision: " + i);
			jumping = false;
			// If you aren't wall clinging
			if (!wallClingLeft && !wallClingRight) {
				// Start falling and stop jumping on collision
				falling = true;
				jumping = false;
			}
			// This will enter here if you are not sliding down a wall
			else if (wallFallSpeed < 0.2) {
				// Don't allow them to climb through the wall
				GameState.yOffset = GameState.prevYOffset;
			}
			// If you are wall jumping
			if (wallJumpFromRight || wallJumpFromLeft) {
				// You are no longer wall jumping
				wallJumpFrames = 0;
				// Prevent you from jumping through the wall
				GameState.yOffset += currentJumpSpeed;
				// The current way that wall jumping works allows you to
				// very slightly clip through the roof at a set distance
				// This should be changed
			}
			// collided = true;
			// Makes roofs sticky
			// GameState.yOffset = yy - y;
		}
		hitBoxTop.x = tempx;
		hitBoxTop.y = tempy;
		// Reverts hitBox location
		// End of top collision

	}

	private void bottomHitBoxCode(ArrayList<CollisionLine> cL, int i, double leftX, double currentY,
			double angleInDegrees, int type) {
		// ------------------------------------------------------------------------------------------------------
		// | BOTTOM HIT BOX |
		// ------------------------------------------------------------------------------------------------------

		//Skip collision check if this is a floor that you are currently dropping through
		if (!(i == dropFloor && dropping)) {

			//This is the midway point of the player sprite
			double xx = leftX + (width / 2);
			//This is the y intercept of the sprite and the current line
			double yy = cL.get(i).getSlope() * xx + cL.get(i).getYIntercept();


			// Bottom collision
			// Moves the hitBox temporarily so it can check for collisions
			tempx = hitBoxBottom.x;
			tempy = hitBoxBottom.y;
			hitBoxBottom.x = leftX + rLPadding;
			hitBoxBottom.y = currentY - (uDPadding + 1);
			// Checks current line for collision
			// Also makes sure that you aren't jumping so that
			// it doesn't just suck you to the ground when you try to jump
			if (cL.get(i).intersects(hitBoxBottom) && !jumping) {

				dropping = false;
				dropable = false;
				dropFloor = -1;

				// prevents top right collision issue with upward slopes
				if (((yy - y - height - 1) < cL.get(i).y2 - y - height - 1) && type == 1) {
					// Reverts jump speed to normal so you can jump properly
					currentJumpSpeed = jumpSpeed;
					groundCollisionCode(cL, i);
					GameState.yOffset = cL.get(i).y2 - y - height - 1;
				}
				// prevents upper left collision issue with downward slopes
				else if (((yy - y - height - 1) < cL.get(i).y1 - y - height - 1) && type == 0) {
					// Reverts jump speed to normal so you can jump properly
					currentJumpSpeed = jumpSpeed;
					groundCollisionCode(cL, i);
					GameState.yOffset = cL.get(i).y1 - y - height - 1;
				}
				// prevents bottom right collision issue with downward slopes
				else if (((yy - y - height - 1) > cL.get(i).y2 - y - height - 1) && type == 0) {
					// Reverts jump speed to normal so you can jump properly
					currentJumpSpeed = jumpSpeed;
					groundCollisionCode(cL, i);
					GameState.yOffset = cL.get(i).y2 - y - height - 1;
				}
				// prevents bottom left collision issue with upward slopes
				else if (((yy - y - height - 1) > cL.get(i).y1 - y - height - 1) && type == 1) {
					// Reverts jump speed to normal so you can jump properly
					currentJumpSpeed = jumpSpeed;
					groundCollisionCode(cL, i);
					GameState.yOffset = cL.get(i).y1 - y - height - 1;
				} else {
					currentJumpSpeed = jumpSpeed;
					groundCollisionCode(cL, i);
					GameState.yOffset = yy - y - height - 1;
				}
			}
			// If you aren't currently standing somewhere, jumping, or clinging to
			// a wall this will turn on gravity
			else {
				if (!bottomCollision && !jumping && !wallClingRight && !wallClingLeft) {
					falling = true;
					bottomCollision = false;
					if (!attacking) {
						if (facing == 0)
							setAnimation(Animation.FALLRIGHT);
						else
							setAnimation(Animation.FALLLEFT);
					}
					// currentAnimation = 5;
				}
			}
			hitBoxBottom.x = tempx;
			hitBoxBottom.y = tempy;
			// Reverts hitBox location
			// End of bottom collision
		}
	}

	private void leftHitBoxCode(ArrayList<CollisionLine> cL, int i, double leftX, double currentY,
			double angleInDegrees, boolean wall, boolean ceiling) {
		// ------------------------------------------------------------------------------------------------------
		// | LEFT HIT BOX |
		// ------------------------------------------------------------------------------------------------------

		// Left collision



		// Moves the hitBox temporarily so it can check for collisions
		tempx = hitBoxLeft.x;
		tempy = hitBoxLeft.y;
		hitBoxLeft.x = leftX;
		hitBoxLeft.y = currentY - height + rLPadding;

		// Checks current line for collision
		if (cL.get(i).intersects(hitBoxLeft)) {
			System.out.println("in hitbox left");

			// -90 degrees for upward wall
			// 0 for floor
			// negative for upward slope
			// positive for downward slope

			// If this is a wall (you can also use (id == 0) here instead)
			if (wall) {

				//TODO
				//This is the midway point of the player sprite
				//This may need to be related to offset to get anything to work here
				GameState.yOffset--;
				//double yy = (currentY + (height / 2));
				//This is the x intercept of the sprite and the current line?
				//y = mx + b
				//y - b = mx
				//x = (y-b)/m
				double xx = ((currentY - 1 - (height/2)) - cL.get(i).getYIntercept()) / cL.get(i).getSlope();

				System.out.println("x1,y1: " + (cL.get(i).getX1()) + "," + (cL.get(i).getY1()));
				System.out.println("x2,y2: " + (cL.get(i).getX2()) + "," + (cL.get(i).getY2()));
				System.out.println("x1,y1: " + (GameState.xOffset - cL.get(i).getX1()) + "," + (GameState.yOffset - cL.get(i).getY1()));
				System.out.println("x2,y2: " + (GameState.xOffset - cL.get(i).getX2()) + "," + (GameState.yOffset - cL.get(i).getY2()));
				System.out.println("slope: " + (cL.get(i).getSlope()));
				System.out.println("y intercept: " + (cL.get(i).getYIntercept()));
				//leftX is where the player is.
				System.out.println("Left x: " + leftX);
				//currentY is also where the player is.
				System.out.println("current y: " + currentY);
				//System.out.println("yy: " + yy);
				System.out.println("xx: " + xx);
				//neither of these values look to matter
				//always 300
				//System.out.println("x: " + x);
				//always 183
				//System.out.println("y: " + y);
				System.out.println("yOffset: " + GameState.yOffset);
				System.out.println("xOffset: " + GameState.xOffset);
				//System.out.println("slope: " + cL.get(i).getSlope());
				//System.out.println("intercept: " + cL.get(i).getXIntercept());


				//System.out.println("y value: " + y);
				//System.out.println("x intercept value: " + xx);



				//Maybe only allow collision code using the middle height of the player if
				//the player has jumped or wall clung or something.


				// The no left is only important for keyboards or
				// controllers that can hit left and right at the same time
				// If they do that without the check they can move too fast in
				// the wrong direction
				if ((left) && (!right)) {

					if (cL.get(i).x1 != cL.get(i).x2)
					{
						//TODO
						//Still working on this
						if (cL.get(i).getSlope() < 0)
						{
							//System.out.println("Starting at: " + (GameState.xOffset));
							System.out.println("Changing too: " + (GameState.xOffset + (leftX - xx)));
							GameState.xOffset += (leftX - xx) + 1;
						}
					}

					System.out.println("movespeed: " + currentMoveSpeed);
					jumping = false;
					falling = false;
					// Move up the wall
					//GameState.yOffset--;






					// Counteracts right movement
					// GameState.xOffset += moveSpeed;
					currentMoveSpeed = 0;
					wallClingLeft = true;
					//dontChangeMoveSpeedTest = true;
					// Sets which wall is clung to
					clungWall = i;
					wallFallSpeed = 0.1;
					// collided = true;
				}
			} else if (ceiling) {
				GameState.yOffset++;
				// The no left is only important for keyboards or
				// controllers that can hit left and right at the same time
				// If they do that without the check they can move too fast in
				// the wrong direction
				if ((left) && (!right)) {
					System.out.println("movespeed: " + currentMoveSpeed);
					jumping = false;
					falling = true;
					// Counteracts right movement
					currentMoveSpeed = 0;
				}
			}
		}
		// Revert values
		hitBoxLeft.x = tempx;
		hitBoxLeft.y = tempy;

		// End of left collision

	}

	private void rightHitBoxCode(ArrayList<CollisionLine> cL, int i, double leftX, double currentY,
			double angleInDegrees, boolean wall, boolean ceiling) {
		// ------------------------------------------------------------------------------------------------------
		// | RIGHT HIT BOX |
		// ------------------------------------------------------------------------------------------------------
		tempx = hitBoxRight.x;
		tempy = hitBoxRight.y;
		hitBoxRight.x = leftX + width - rLPadding;
		hitBoxRight.y = currentY - height + rLPadding;
		// Checks current line for collision
		if (cL.get(i).intersects(hitBoxRight)) {

			System.out.println("in hitbox right");

			// -90 degrees for upward wall
			// 0 for floor
			// negative for upward slope
			// positive for downward slope

			// If this is a wall or ceiling
			if (wall) {
				// The no left is only important for keyboards or
				// controllers that can hit left and right at the same time
				// If they do that without the check they can move too fast in
				// the wrong direction
				if ((right) && (!left)) {
					System.out.println("movespeed: " + currentMoveSpeed);
					jumping = false;
					falling = false;
					// Move up the wall
					GameState.yOffset--;
					// Counteracts right movement
					currentMoveSpeed = 0;
					//dontChangeMoveSpeedTest = true;
					// GameState.xOffset -= moveSpeed;
					wallClingRight = true;
					// Sets which wall is clung to
					clungWall = i;
					wallFallSpeed = 0.1;
					// collided = true;
				}
			} else if (ceiling) {
				// The no left is only important for keyboards or
				// controllers that can hit left and right at the same time
				// If they do that without the check they can move too fast in
				// the wrong direction
				if ((right) && (!left)) {
					System.out.println("movespeed: " + currentMoveSpeed);
					jumping = false;
					falling = true;
					// Counteracts right movement
					currentMoveSpeed = 0;
				}
			}
		}
		// Revert values
		hitBoxRight.x = tempx;
		hitBoxRight.y = tempy;
		// End of right collision
	}

	private void groundCollisionCode(ArrayList<CollisionLine> cL, int i) {
		// Says you aren't falling
		falling = false;
		bottomCollision = true;

		// GameState.yOffset = yy - y - height - 1;
		// Allows you to drop through semi-solid floors
		// Currently doesn't work with the new larger hitBox
		if (cL.get(i).getID() == LineType.SEMISOLIDFLOOR) {
			dropable = true;
			dropFloor = i;
		}

		if (!attacking) {
			if (facing == 0) {
				if (right) {
					setAnimation(Animation.RIGHT);
				} else {
					setAnimation(Animation.IDLERIGHT);
				}
			} else {
				if (left) {
					setAnimation(Animation.LEFT);
				} else {
					setAnimation(Animation.IDLELEFT);
				}
			}
		}
	}

	private void changePosition(double currentY, ArrayList<CollisionLine> cL) {
		// If you are moving right and not in a wallJump
		if ((right) && (!wallJumpFromLeft || !wallJumpFromRight)) {

			// Simply move to the right
			GameState.xOffset += currentMoveSpeed;

			// revert moveSpeed in case of slopes or speed change
			currentMoveSpeed = moveSpeed;
		}
		// If you are moving left and not in a wallJump
		if ((left) && (!wallJumpFromLeft || !wallJumpFromRight)) {

			// Simply move to the left
			GameState.xOffset -= currentMoveSpeed;
			// revert moveSpeed in case of slopes or speed change
			currentMoveSpeed = moveSpeed;
		}

		// If you are falling
		if (falling) {
			// Reset jump speed
			currentJumpSpeed = jumpSpeed;
			// Fall at current rate
			GameState.yOffset += currentFallSpeed;
			// Increase fall speed up to a maximum
			if (currentFallSpeed < maxFallSpeed) {
				currentFallSpeed += 0.2;
			}

		}
		// If you are not falling
		if (!falling) {
			// Reset fall speed
			// TODO change this to a variable
			currentFallSpeed = 0.2;
		}
		// If you are wallClinging
		if (wallClingLeft || wallClingRight) {
			System.out.println("wall test: " + wallFallSpeed);
			// Reset jump speed
			currentJumpSpeed = jumpSpeed;
			// Fall at wallFallSpeed
			GameState.yOffset += wallFallSpeed;
			// Increase fall speed to a maximum
			if (wallFallSpeed < (maxFallSpeed / 2)) {
				wallFallSpeed += 0.1;
			}

			// If you are clinging to the bottom of the wall or the top of the wall
			// let go of the wall
			if ((cL.get(clungWall).y1 - currentY < 0) || (cL.get(clungWall).y2 - currentY > 0)) {
				System.out.println("uncling");
				wallClingRight = false;
			}
		}

		// wallJumping forcing an animation is cool for later visuals,
		// but it is currently lets you clip the roof slightly if
		// you are already hitting the roof
		if (wallJumpFromLeft) {
			// This is what causes the clipping
			GameState.yOffset -= currentJumpSpeed;
			// Rebounds from the wall
			GameState.xOffset += 5;
			// Decrements a set amount of frames
			wallJumpFrames--;
			// If the frames end the wall jump ends
			if (wallJumpFrames <= 0) {
				wallJumpFrames = 10;
				wallJumpFromRight = false;
			}
			// Slows the speed of the jump
			currentJumpSpeed -= 0.1;
			// If you are at the top of your jump
			if (currentJumpSpeed <= 0) {
				// Reverts jumping speed
				currentJumpSpeed = jumpSpeed;
				// No longer jumping
				jumping = false;
				System.out.println("wallJumpFromLeft");
				// You are now falling
				falling = true;
			}
		} else if (wallJumpFromRight) {
			// This is what causes the clipping
			GameState.yOffset -= currentJumpSpeed;
			// Rebounds from the wall
			GameState.xOffset -= 5;
			// Decrements a set amount of frames
			wallJumpFrames--;
			// If the frames end the wall jump ends
			if (wallJumpFrames <= 0) {
				wallJumpFrames = 10;
				wallJumpFromRight = false;
			}
			// Slows the speed of the jump
			currentJumpSpeed -= 0.1;
			// If you are at the top of your jump
			if (currentJumpSpeed <= 0) {
				// Reverts jumping speed
				currentJumpSpeed = jumpSpeed;
				// No longer jumping
				jumping = false;
				System.out.println("wallJumpFromRight");
				// You are now falling
				falling = true;
			}
		} else if (jumping) {
			// Raise current jump speed
			GameState.yOffset -= currentJumpSpeed;
			// Decrease jump speed
			currentJumpSpeed -= 0.1;
			// If jump speed has hit 0 or less
			if (currentJumpSpeed <= 0) {
				// Revert jump speed
				currentJumpSpeed = jumpSpeed;
				// You are no longer jumping
				jumping = false;
				// You are now falling
				falling = true;
			}
		}

	}


	// Key listener
	public void keyPressed(int k) {

		if (k == KeyEvent.VK_J) {
			if (attackTime == 36) {
				System.out.println("Play animation");
				attacking = true;
				if (facing == 0) {
					if (jumping || falling) {
						setAnimation(Animation.ATTACKINGJUMPRIGHT);
					} else {
						setAnimation(Animation.ATTACKINGRIGHT);
					}
				} else {
					if (jumping || falling) {
						setAnimation(Animation.ATTACKINGJUMPLEFT);
					} else {
						setAnimation(Animation.ATTACKINGLEFT);
					}
				}
			}
		}
		if (k == KeyEvent.VK_D) {
			// System.out.println("d pressed");
			right = true;
			// Let go of left wall
			wallClingLeft = false;
		}
		if (k == KeyEvent.VK_A) {
			// System.out.println("a pressed");
			left = true;
			// Let go of right wall
			wallClingRight = false;
		}
		if (k == KeyEvent.VK_S) {
			down = true;
		}
		if (k == KeyEvent.VK_L) {
			System.out.println("Dialogue: " + Dialogue.dialogueOpen);
			if (!Dialogue.dialogueOpen)
			{
				Dialogue.createDialogue("This is a test string. Just testing stuff.~Still testing stuff.");
				System.out.println(Dialogue.x);
				//createDialogue("Testing this ~Testing this again with a much longer string to see if the words wrap correctly.");
			}
			else
			{
				Dialogue.advanceDialogue();
				//advanceDialogue();
			}
		}
		// Wall jump code
		if (k == KeyEvent.VK_SPACE && !jumping && !falling) {
			jumping = true;
			if (facing == 0)
				setAnimation(Animation.JUMPRIGHT);
			else
				setAnimation(Animation.JUMPLEFT);
			if (wallClingLeft) {
				// TODO
			} else if (wallClingRight) {
				wallJumpFromRight = true;
			}
			// Let go of all walls
			wallClingLeft = false;
			wallClingRight = false;
			// No longer works due to increased bottom hitBox
			if ((dropable) && (down)) {
				dropping = true;
				currentJumpSpeed = jumpSpeed;
				GameState.yOffset += currentFallSpeed;
				if (currentFallSpeed < maxFallSpeed) {
					currentFallSpeed += 0.2;
				}
				GameState.prevYOffset = GameState.yOffset + 6;
				GameState.yOffset += 6;
				falling = true;
				jumping = false;
				// dropable = false;
			}
		}
	}

	// Key listener
	public void keyReleased(int k) {
		if (k == KeyEvent.VK_D) {
			// System.out.println("d released");
			right = false;
		}
		if (k == KeyEvent.VK_A) {
			// System.out.println("a released");
			left = false;
		}
		if (k == KeyEvent.VK_S) {
			down = false;
		}
		if (k == KeyEvent.VK_SPACE) {
			jumping = false;
		}
	}

	private void setAnimation(Animation animation) {

		switch (animation) {
		case IDLERIGHT: {
			if (left) {
				facing = 1;
				currentPlayerAnimation = 3;
			} else {
				facing = 0;
				currentPlayerAnimation = 0;
			}
			break;
		}
		case IDLELEFT: {
			if (right) {
				facing = 0;
				currentPlayerAnimation = 2;
			} else {
				facing = 1;
				currentPlayerAnimation = 1;
			}
			break;
		}
		case RIGHT: {
			if (left) {
				// facing = 1;
				currentPlayerAnimation = 1;
			} else {
				// facing = 0;
				currentPlayerAnimation = 2;
			}
			break;
		}
		case LEFT: {
			if (right) {
				// facing = 0;
				currentPlayerAnimation = 0;
			} else {
				// facing = 1;
				currentPlayerAnimation = 3;
			}
			break;
		}
		case FALLRIGHT: {
			currentPlayerAnimation = 4;
			break;
		}
		case FALLLEFT: {
			currentPlayerAnimation = 5;
			break;
		}
		case JUMPRIGHT: {
			currentPlayerAnimation = 6;
			break;
		}
		case JUMPLEFT: {
			currentPlayerAnimation = 7;
			break;
		}
		case ATTACKINGRIGHT: {
			currentPlayerAnimation = 8;
			break;
		}
		case ATTACKINGLEFT: {
			currentPlayerAnimation = 9;
			break;
		}
		case ATTACKINGJUMPRIGHT: {
			currentPlayerAnimation = 10;
			break;
		}
		case ATTACKINGJUMPLEFT: {
			currentPlayerAnimation = 11;
			break;
		}
		}
	}

}
