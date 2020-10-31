package net.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import net.codejava.GamePanel;
import net.gameState.GameState;
import net.objects.CollisionLine;
import net.objects.MovingBlock;
import net.physics.HitBox;
import net.resources.SpriteForAnimation;

public class Player {

	//Player values
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
	private int dropFloor = -1;
	private boolean dropable = false;

	private boolean grounded = false;

	private HitBox hitBoxRight, hitBoxLeft, hitBoxTop, hitBoxBottom;
	private double tempx, tempy;

	//These can play the incorrect animation if you press a button while
	//holding another button.

	//Animations
	private SpriteForAnimation[] animations;
	private int currentAnimation;

	public Player(int width, int height) {
		//Place player 1/3 of the screen to the right
		x = GamePanel.WIDTH / 3;
		//Place player in the middle of the screen vertically
		y = GamePanel.HEIGHT / 2;
		//Set player dimensions
		this.width = width;
		this.height = height;

		//Could potentially put most of this code in the collision class.
		//It would look a lot nicer, although I'm not sure if it would be
		//very practical.

		//The class may not really work, but it could still be good to at least
		//put all of the collisions into a separate class? It might make the break
		//idea bad or difficult though

		//I wonder if it would be good to consider different ways to break out
		//of the collision loops. It could improve efficiency and with good
		//map design it shouldn't be a problem. And example of something that
		//may be good to break is the ledge grab idea below.
		//Or the drop through platform code. It may require more IDs such as
		//wall with ledge vs wall that connects to ceiling, but I think it might
		//be better. Maybe also move all the collision code into methods so that
		//this code is nicer and more readable.


		//TODO I'd like to add a way to climb the ledge of a wall/plateform.
		//Walls would probably be pretty easy. I think a check for the top
		//of the wall colliding with the right hitbox would work. Might want it
		//to only apply if it collides near the top of the hitbox to prevent the
		//character from warping too far on the grab.

		//TODO consider changing the left and right hitBoxes to have more
		//dead space on the bottom. This could allow for easier right/left
		//collision detection for slopes and simplify the code for right/left
		//collisions in general. For example, if all right/left collision code
		//just prevented you from colliding with them (Except walls), you could
		//then use just the bottom hit detection for slopes. I think this will
		//work much better than the current method.

		//TODO if you must jump into a wall to wall cling, then you can make it
		//so that hitting the bottom of anything removes wall clinging
		// Create hitBoxes
		hitBoxLeft = new HitBox(x - 6 + 6, y + 6, 6, height - 12, 0);
		hitBoxRight = new HitBox(x + width - 6, y + 6, 6, height - 12, 0);
		hitBoxBottom = new HitBox(x + 6, y + height - 13, width - 12, 14, 0);
		hitBoxTop = new HitBox(x + 6, y, width - 12, 13, 0);

		//Create animations
		animations = new SpriteForAnimation[4];
		//This won't work if the animations are different length
		//but for now this will work
		for (int i = 0; i < 4; i++)
		{
			animations[i] = new SpriteForAnimation(300, 275, i, 6, 6);
		}
		currentAnimation = 0;

		//Display values once for checking stuff purposes
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("left box: " + hitBoxLeft.getBounds());
		System.out.println("right box: " + hitBoxRight.getBounds());
		System.out.println("bottom box: " + hitBoxBottom.getBounds());
		System.out.println("top box: " + hitBoxTop.getBounds());
	}

	public void tick(ArrayList<MovingBlock> movingBlocks, ArrayList<CollisionLine> cL) {

		//Obtain calculations of where the player actually
		//appears because they do not actually move
		//Also helps with the hitBoxes which also do not move
		double leftX = GameState.xOffset + x;
		double currentY = GameState.yOffset + y + height;


		//Loop through all collision lines
		//Can probably put a check at the front to avoid checking lines
		//that are further away than a certain distance
		for (int i = 0; i < cL.size(); i++) {
			//Obtain slope of the line. Java doesn't care if it is undefined or NaN
			//Therefore this does not need special checks for those here
			double slope = (cL.get(i).y2 - cL.get(i).y1) / (cL.get(i).x2 - cL.get(i).x1);
			double intercept = cL.get(i).y1 - (slope * cL.get(i).x1);
			double xx = leftX + (width / 2);
			double yy = slope * xx + intercept;

			//Obtains the angle of the line
			//May be needed for left/right code depending on how I feel
			//about coding slopes
			//System.out.println("i: " + i);
			double deltaY = cL.get(i).y2 - cL.get(i).y1;
			double deltaX = cL.get(i).x2 - cL.get(i).x1;
			//System.out.println("deltax: " + deltaX);
			//System.out.println("deltay: " + deltaY);
			double angleInDegrees = Math.atan(deltaY / deltaX) * 180 / Math.PI;
			//System.out.println("angleInDegrees: " + angleInDegrees);

			//Collisions start here

			//Right collision
			// TODO box dimmensions changed need to check placement 		 |
			// It doesn't handle corners that look like this well 			 |
			// 													 			/
			// 															   /
			//Launches at an angle when jumping next to wall.
			//Should be caused by the wall jump code
			//maybe check something with right to fix this.
			//or just have wall jumps not be different to normal jumps
			//that would save a decent amount of code



			//Moves the hitBox temporarily so it can check for collisions
			tempx = hitBoxRight.x;
			tempy = hitBoxRight.y;
			hitBoxRight.x = leftX + width - 6;
			hitBoxRight.y = currentY - height + 6;
			//Checks current line for collision
			if (cL.get(i).intersects(hitBoxRight)) {
				System.out.println("in hitbox right");

				// -90 degrees for upward wall
				// 0 for floor
				// negative for upward slope
				// positive for downward slope

				//If this is a wall (you can also use (id == 0) here instead)
				if (angleInDegrees == -90) {
					System.out.println("wall");
					//The no left is only important for keyboards or
					//controllers that can hit left and right at the same time
					//If they do that without the check they can move too fast in
					//the wrong direction
					if ((right) && (!left)) {
						System.out.println("movespeed: " + moveSpeed);
						jumping = false;
						falling = false;
						//Move up the wall
						GameState.yOffset--;
						//Counteracts right movement
						GameState.xOffset -= moveSpeed;
						wallClingRight = true;
						//Sets which wall is clung to
						clungWall = i;
						wallFallSpeed = 0.1;
						// collided = true;
					}
				}
				//else if it is an upward slope
				else if (angleInDegrees < 0) {
					if (right) {
						//Move slower up slopes (Need to check if recent
						//changes have broken this)
						double test = moveSpeed + 2 * slope;
						falling = false;
						//If the speed is above a minimum
						if (test >= 1) {
							// Seems good
							System.out.println("steep slope");
							moveSpeed = test;
						}
						//If the speed is not above a minimum use the minimum
						else {
							System.out.println("very steep slope");
							moveSpeed = 1;
						}
					}
				}
				//if this is a normal slope
				else if (angleInDegrees < -45){
					System.out.println("angle < -45");
					if (right) {
						GameState.xOffset -= moveSpeed;
					}
				}
				//if this is the back of a downward slope
				//This should pretty much never happen, but it could happen
				//for downward angled roofs
				else if (angleInDegrees > 0)
				{
					System.out.println(">0");
					if (right) {
						//This may be too harsh to stop movement completely
						//Although the chance of this happening instead of topCollision
						//should be very low. It is probably ok because Only stalactites or
						//similar shapes should cause this to happen and you will resume
						//normal movement after falling below the tip
						GameState.xOffset -= moveSpeed;
					}
				}
			}
			//Revert values
			hitBoxRight.x = tempx;
			hitBoxRight.y = tempy;
			//End of right collision


			//Left collision
			// TODO box dimmensions changed need to check placement
			/*
			 * //Moves the hitBox temporarily so it can check for collisions
			 * tempx = hitBoxLeft.x;
			 * tempy = hitBoxLeft.y;
			 * hitBoxLeft.x = leftX;
			 * hitBoxLeft.y = currentY - height + 6;

			 * hitBoxLeft.x = tempx;
			 * hitBoxLeft.y = tempy;
			 */
			// End of left collision

			//Top collision
			// TODO box dimmensions changed need to check placement
			// this one also seems to just work correctly
			// it won't let me jump when on the very steep slope
			// but that is because the very steep slope is so steep
			// that it goes through the top hitBox.
			// Taller model has made this irrelevant for all but the
			// steepest slopes
			//Moves the hitBox temporarily so it can check for collisions
			tempx = hitBoxTop.x;
			tempy = hitBoxTop.y;
			hitBoxTop.x = leftX + 6;
			hitBoxTop.y = currentY - height;
			//Checks current line for collision
			if (cL.get(i).intersects(hitBoxTop)) {
				// Check if id is semi-solid floor
				if ((cL.get(i).getID() != 2)) {
					System.out.println("top collision: " + i);
					jumping = false;
					//If you aren't wall clinging
					if (!wallClingLeft && !wallClingRight) {
						//Start falling and stop jumping on collision
						falling = true;
						jumping = false;
					}
					//This will enter here if you are not sliding down a wall
					else if (wallFallSpeed < 0.2) {
						//Don't allow them to climb through the wall
						GameState.yOffset = GameState.prevYOffset;
					}
					//If you are wall jumping
					if (wallJumpFromRight || wallJumpFromLeft) {
						//You are no longer wall jumping
						wallJumpFrames = 0;
						//Prevent you from jumping through the wall
						GameState.yOffset += currentJumpSpeed;
						//The current way that wall jumping works allows you to
						//very slightly clip through the roof at a set distance
						//This should be changed

					}
					// collided = true;
					// Makes roofs sticky
					// GameState.yOffset = yy - y;
				}
			}
			hitBoxTop.x = tempx;
			hitBoxTop.y = tempy;
			//Reverts hitBox location
			// End of top collision

			// TODO box dimmensions changed need to check placement
			// Bottom collision
			//Moves the hitBox temporarily so it can check for collisions
			tempx = hitBoxBottom.x;
			tempy = hitBoxBottom.y;
			hitBoxBottom.x = leftX + 6;
			hitBoxBottom.y = currentY - 13;
			//Checks current line for collision
			//Also makes sure that you aren't jumping so that
			//it doesn't just suck you to the ground when you try to jump
			if (cL.get(i).intersects(hitBoxBottom) && !jumping) {
				//Reverts jump speed to normal so you can jump properly
				currentJumpSpeed = jumpSpeed;
				// Makes sure the collision isn't a wall
				if (cL.get(i).x1 != cL.get(i).x2) {
					//Says you aren't falling
					falling = false;
					grounded = true;
					//Places you 1 pixel above the ground
					GameState.yOffset = yy - y - height - 1;
					// Allows you to drop through semi-solid floors
					// Currently doesn't work with the new larger hitBox
					if (cL.get(i).getID() == 2) {
						dropable = true;
						dropFloor = i;
					}
				}
			}
			//If you aren't currently standing somewhere, jumping, or clinging to
			//a wall this will turn on gravity
			else {
				if (!grounded && !jumping && !wallClingRight && !wallClingLeft) {
					falling = true;
					dropable = false;
				}
			}
			hitBoxBottom.x = tempx;
			hitBoxBottom.y = tempy;
			//Reverts hitBox location
			// End of bottom collision
		} // End of collisionLine for loop

		//switches grounded to false so
		//that it doesn't break next frames collision checks
		grounded = false;

	//Replaces the previous frames offsets with this one before moving this
	//offset to a new location
	GameState.prevXOffset = GameState.xOffset;
	GameState.prevYOffset = GameState.yOffset;

	//If you are moving right and not in a wallJump
		if ((right) && (!wallJumpFromLeft || !wallJumpFromRight)) {
			//Simply move to the right
			GameState.xOffset += moveSpeed;
			//revert moveSpeed in case of slopes or speed change
			moveSpeed = 5;
		}
		//If you are moving left and not in a wallJump
		if ((left) && (!wallJumpFromLeft || !wallJumpFromRight)) {
			//Simply move to the left
			GameState.xOffset -= moveSpeed;
			//revert moveSpeed in case of slopes or speed change
			moveSpeed = 5;
		}

		//If you are falling
		if (falling) {
			//Reset jump speed
			currentJumpSpeed = jumpSpeed;
			//Fall at current rate
			GameState.yOffset += currentFallSpeed;
			//Increase fall speed up to a maximum
			if (currentFallSpeed < maxFallSpeed) {
				currentFallSpeed += 0.2;
			}

		}
		//If you are not falling
		if (!falling) {
			//Reset fall speed
			//TODO change this to a variable
			currentFallSpeed = 0.2;
		}
		//If you are wallClinging
		if (wallClingLeft || wallClingRight) {
			System.out.println("wall test: " + wallFallSpeed);
			//Reset jump speed
			currentJumpSpeed = jumpSpeed;
			//Fall at wallFallSpeed
			GameState.yOffset += wallFallSpeed;
			//Increase fall speed to a maximum
			if (wallFallSpeed < (maxFallSpeed / 2)) {
				wallFallSpeed += 0.1;
			}

			//If you are clinging to the bottom of the wall or the top of the wall
			//let go of the wall
			if ((cL.get(clungWall).y1 - currentY < 0) || (cL.get(clungWall).y2 - currentY > 0)) {
				System.out.println("uncling");
				wallClingRight = false;
			}
		}

		// wallJumping forcing an animation is cool for later visuals,
		// but it is currently lets you clip the roof slightly if
		// you are already hitting the roof
		if (wallJumpFromLeft) {
			//This is what causes the clipping
			GameState.yOffset -= currentJumpSpeed;
			//Rebounds from the wall
			GameState.xOffset += 5;
			//Decrements a set amount of frames
			wallJumpFrames--;
			//If the frames end the wall jump ends
			if (wallJumpFrames <= 0) {
				wallJumpFrames = 10;
				wallJumpFromRight = false;
			}
			//Slows the speed of the jump
			currentJumpSpeed -= 0.1;
			//If you are at the top of your jump
			if (currentJumpSpeed <= 0) {
				//Reverts jumping speed
				currentJumpSpeed = jumpSpeed;
				//No longer jumping
				jumping = false;
				System.out.println("wallJumpFromLeft");
				//You are now falling
				falling = true;
			}
		} else if (wallJumpFromRight) {
			//This is what causes the clipping
			GameState.yOffset -= currentJumpSpeed;
			//Rebounds from the wall
			GameState.xOffset -= 5;
			//Decrements a set amount of frames
			wallJumpFrames--;
			//If the frames end the wall jump ends
			if (wallJumpFrames <= 0) {
				wallJumpFrames = 10;
				wallJumpFromRight = false;
			}
			//Slows the speed of the jump
			currentJumpSpeed -= 0.1;
			//If you are at the top of your jump
			if (currentJumpSpeed <= 0) {
				//Reverts jumping speed
				currentJumpSpeed = jumpSpeed;
				//No longer jumping
				jumping = false;
				System.out.println("wallJumpFromRight");
				//You are now falling
				falling = true;
			}
		}
		else if (jumping) {
			//Raise current jump speed
			GameState.yOffset -= currentJumpSpeed;
			//Decrease jump speed
			currentJumpSpeed -= 0.1;
			//If jump speed has hit 0 or less
			if (currentJumpSpeed <= 0) {
				//Revert jump speed
				currentJumpSpeed = jumpSpeed;
				//You are no longer jumping
				jumping = false;
				//You are now falling
				falling = true;
			}
		}
	}

	//Draw method
	public void draw(Graphics g) {
		//Draw the playerBox
		g.setColor(Color.BLACK);

		g.fillRect((int) x, (int) y, width, height);
		//Draw the hitBoxes
		hitBoxRight.draw(g);
		hitBoxLeft.draw(g);
		hitBoxTop.draw(g);
		hitBoxBottom.draw(g);

		//Draw the current animation
		animations[currentAnimation].draw(g);
	}

	//Key listener
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_D) {
			//System.out.println("d pressed");
			right = true;
			currentAnimation = 2;
			//Let go of left wall
			wallClingLeft = false;
		}
		if (k == KeyEvent.VK_A) {
			//System.out.println("a pressed");
			left = true;
			currentAnimation = 3;
			//Let go of right wall
			wallClingRight = false;
		}
		if (k == KeyEvent.VK_S) {
			down = true;
		}
		//Wall jump code
		if (k == KeyEvent.VK_SPACE && !jumping && !falling) {
			jumping = true;
			if (wallClingLeft) {
				// TODO
			} else if (wallClingRight) {
				wallJumpFromRight = true;
			}
			//Let go of all walls
			wallClingLeft = false;
			wallClingRight = false;
			//No longer works due to increased bottom hitBox
			if ((dropable) && (down)) {
				currentJumpSpeed = jumpSpeed;
				GameState.yOffset += currentFallSpeed;
				if (currentFallSpeed < maxFallSpeed) {
					currentFallSpeed += 0.2;
				}
				GameState.prevYOffset = GameState.yOffset + 6;
				GameState.yOffset += 6;
				falling = true;
				jumping = false;
				dropable = false;
			}
		}
	}

	//Key listener
	public void keyReleased(int k) {
		if (k == KeyEvent.VK_D) {
			//System.out.println("d released");
			right = false;
			currentAnimation = 0;
		}
		if (k == KeyEvent.VK_A) {
			//System.out.println("a released");
			left = false;
			currentAnimation = 1;
		}
		if (k == KeyEvent.VK_S) {
			down = false;
		}
		if (k == KeyEvent.VK_SPACE) {
			jumping = false;
		}
	}
}
