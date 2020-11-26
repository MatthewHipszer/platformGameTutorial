package net.entities;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import net.codejava.GamePanel;
import net.gameState.GameState;
import net.maps.Map.LineType;
import net.objects.CollisionLine;
import net.objects.MovingBlock;
import net.physics.HitBox;
import net.resources.SpriteForAnimation;

public class Player {

	public enum Animation {
		IDLERIGHT, IDLELEFT, RIGHT, LEFT, JUMPRIGHT, JUMPLEFT, FALLRIGHT, FALLLEFT, ATTACKINGRIGHT, ATTACKINGLEFT
	}

	// Player values
	public static double x;
	public static double y;
	private int width, height;
	private int rLPadding = 6;
	private int uDPadding = 12;

	// movement
	private double moveSpeed = 5;
	private int facing = 0;
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
	private boolean dontChangeMoveSpeedTest = false;

	private boolean bottomCollision = false;

	private HitBox hitBoxRight, hitBoxLeft, hitBoxTop, hitBoxBottom;
	private double tempx, tempy;
	private boolean attacking = false;
	private int attackTime = 36;

	private HitBox attackBoxRight;
	private HitBox attackBoxLeft;

	// These can play the incorrect animation if you press a button while
	// holding another button.

	// Animations
	private SpriteForAnimation[] playerAnimations;
	private SpriteForAnimation[] attackAnimations;
	private int currentPlayerAnimation;

	public Player(int width, int height) {
		// Place player 1/3 of the screen to the right
		x = GamePanel.WIDTH / 3;
		// Place player in the middle of the screen vertically
		// Changed to 1/3 for map testing
		y = GamePanel.HEIGHT / 3;
		// Set player dimensions
		this.width = width;
		this.height = height;

		//Has a slope to floor issue which is caused by the same problem that
		//was causing the other slope to floor issue. Should be solvable with
		//very similar code.

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

		// TODO consider changing the left and right hitBoxes to have more
		// dead space on the bottom. This could allow for easier right/left
		// collision detection for slopes and simplify the code for right/left
		// collisions in general. For example, if all right/left collision code
		// just prevented you from colliding with them (Except walls), you could
		// then use just the bottom hit detection for slopes. I think this will
		// work much better than the current method.

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

		// Create animations
		playerAnimations = new SpriteForAnimation[10];
		attackAnimations = new SpriteForAnimation[2];
		// This won't work if the animations are different length
		// but for now this will work
		for (int i = 0; i < playerAnimations.length; i++) {
			playerAnimations[i] = new SpriteForAnimation((int) x, (int) y, i, 6, 6, "/blocks/spriteTest.png");
		}
		attackAnimations[0] = new SpriteForAnimation((int) x + width, (int) y, 0, 6, 6,
				"/blocks/attackAnimationsTest.png");
		attackAnimations[1] = new SpriteForAnimation((int) x - width, (int) y, 1, 6, 6,
				"/blocks/attackAnimationsTest.png");
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
		dontChangeMoveSpeedTest = false;
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

		// Draw the current animation
		playerAnimations[currentPlayerAnimation].draw(g);
		if (attacking)
		{
			if (facing == 0)
				attackAnimations[0].draw(g);
			else
				attackAnimations[1].draw(g);
			System.out.println("attackTime: " + attackTime);
			System.out.println("frame: " + attackAnimations[0].getFrame());
			System.out.println("currentAnimation: " + currentPlayerAnimation);
		}

		// Draw the hitBoxes
		hitBoxRight.draw(g);
		hitBoxLeft.draw(g);
		hitBoxTop.draw(g);
		hitBoxBottom.draw(g);

		attackBoxRight.draw(g);
		attackBoxLeft.draw(g);

	}

	private void checkLineCollisions(double leftX, double currentY, ArrayList<CollisionLine> cL) {
		// TODO Auto-generated method stub

		// Obtain calculations of where the player actually
		// appears because they do not actually move
		// Also helps with the hitBoxes which also do not move

		// Loop through all collision lines
		// Can probably put a check at the front to avoid checking lines
		// that are further away than a certain distance
		for (int i = 0; i < cL.size(); i++) {
			// Obtain slope of the line. Java doesn't care if it is undefined or NaN
			// Therefore this does not need special checks for those here
			double xx = leftX + (width / 2);
			double yy = cL.get(i).getSlope() * xx + cL.get(i).getYIntercept();

			// Obtains the angle of the line
			// May be needed for left/right code depending on how I feel
			// about coding slopes
			// System.out.println("i: " + i);
			// System.out.println("deltax: " + deltaX);
			// System.out.println("deltay: " + deltaY);
			// System.out.println("angleInDegrees: " + angleInDegrees);

			// Collisions start here

			// Right collision
			// TODO box dimmensions changed need to check placement |
			// It doesn't handle corners that look like this well |
			// /
			// /
			// Launches at an angle when jumping next to wall.
			// Should be caused by the wall jump code
			// maybe check something with right to fix this.
			// or just have wall jumps not be different to normal jumps
			// that would save a decent amount of code

			// Moves the hitBox temporarily so it can check for collisions

			double angleInDegrees = cL.get(i).getAngle();

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

				// If this is a wall (you can also use (id == 0) here instead)
				if (angleInDegrees == -90) {
					System.out.println("wall");
					// The no left is only important for keyboards or
					// controllers that can hit left and right at the same time
					// If they do that without the check they can move too fast in
					// the wrong direction
					if ((right) && (!left)) {
						System.out.println("movespeed: " + moveSpeed);
						jumping = false;
						falling = false;
						// Move up the wall
						GameState.yOffset--;
						// Counteracts right movement
						moveSpeed = 0;
						dontChangeMoveSpeedTest = true;
						// GameState.xOffset -= moveSpeed;
						wallClingRight = true;
						// Sets which wall is clung to
						clungWall = i;
						wallFallSpeed = 0.1;
						// collided = true;
					}
				}
				// if this is a very steep slope
				else if (angleInDegrees < -45) {
					System.out.println("angle < -45");
					if (right) {
						moveSpeed = 1;
					}
				}
				// else if it is an upward slope
				else if (angleInDegrees < 0) {
					if ((right) && (!dontChangeMoveSpeedTest)) {
						// Move slower up slopes (Need to check if recent
						// changes have broken this)
						double test = moveSpeed + 2 * cL.get(i).getSlope();
						falling = false;
						// If the speed is above a minimum
						if (test >= 1) {
							// Seems good
							System.out.println("steep slope");
							moveSpeed = test;
						}
						// If the speed is not above a minimum use the minimum
						else if (!dontChangeMoveSpeedTest) {
							System.out.println("very steep slope");
							moveSpeed = 1;
						}
					}
				}
				// If this is the back of a downward slope
				// This should pretty much never happen, but it could happen
				// for downward angled roofs
				else if (angleInDegrees > 0) {
					System.out.println(">0");
					if (right) {
						// This may be too harsh to stop movement completely
						// Although the chance of this happening instead of topCollision
						// should be very low. It is probably ok because Only stalactites or
						// similar shapes should cause this to happen and you will resume
						// normal movement after falling below the tip
						moveSpeed = 0;
					}
				}
			}
			// Revert values
			hitBoxRight.x = tempx;
			hitBoxRight.y = tempy;
			// End of right collision

			// Left collision
			// TODO box dimmensions changed need to check placement|
			// This is being worked on. |
			// Can phase through walls if slopes are like this: \

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
				if (angleInDegrees == -90) {
					System.out.println("wall");
					// The no left is only important for keyboards or
					// controllers that can hit left and right at the same time
					// If they do that without the check they can move too fast in
					// the wrong direction
					if ((left) && (!right)) {
						System.out.println("movespeed: " + moveSpeed);
						jumping = false;
						falling = false;
						// Move up the wall
						GameState.yOffset--;
						// Counteracts right movement
						// GameState.xOffset += moveSpeed;
						moveSpeed = 0;
						wallClingLeft = true;
						dontChangeMoveSpeedTest = true;
						// Sets which wall is clung to
						clungWall = i;
						wallFallSpeed = 0.1;
						// collided = true;
					}
				}
				// TODO
				// This should be a slightly slower than normal speed.
				else if (angleInDegrees > 45 || angleInDegrees < 0) {
					if ((left) && (!dontChangeMoveSpeedTest)) {
						// Move slower up slopes (Need to check if recent
						// changes have broken this)
						double test = moveSpeed + -2 * cL.get(i).getSlope();
						falling = false;
						// If the speed is above a minimum
						if (test >= 1) {
							// Seems good
							System.out.println("steep slope");
							moveSpeed = test;
						}
						// If the speed is not above a minimum use the minimum
						else if (!dontChangeMoveSpeedTest) {
							System.out.println("very steep slope");
							moveSpeed = -1;
						}
					}
				}
				// These are downward slopes
				// Need to work out what to do here.
				// I think the reverse of this on right slopes stops movement.
				// It is possible this is wrong for right slopes as well.
				else if (angleInDegrees < 0) {
					System.out.println("angle < -45");
					if (left) {
						moveSpeed = 0;
						// GameState.xOffset += moveSpeed;
					}
				}
			}
			// Revert values
			hitBoxLeft.x = tempx;
			hitBoxLeft.y = tempy;

			// End of left collision

			// Top collision
			// TODO box dimmensions changed need to check placement
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
				// Check if id is semi-solid floor
				if ((cL.get(i).getID() != LineType.SEMISOLIDFLOOR)) {
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
			}
			hitBoxTop.x = tempx;
			hitBoxTop.y = tempy;
			// Reverts hitBox location
			// End of top collision

			// TODO box dimmensions changed need to check placement
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
				// Reverts jump speed to normal so you can jump properly
				currentJumpSpeed = jumpSpeed;
				// Makes sure the collision isn't a wall
				if (cL.get(i).x1 != cL.get(i).x2) {
					// Says you aren't falling
					falling = false;
					bottomCollision = true;

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

					// System.out.println("equation: " + (yy - y - height - 1));
					// System.out.println("offset: " + GameState.yOffset);
					// System.out.println("cLTest: " + (cL.get(i).y1 - y - height - 1));
					// Places you 1 pixel above the ground
					if ((yy - y - height - 1) > cL.get(i).y1 - y - height - 1) {
						// System.out.println("cLTest2");
						GameState.yOffset = cL.get(i).y1 - y - height - 1;
					} else {
						// System.out.println("cLTest3");
						GameState.yOffset = yy - y - height - 1;
					}
					// GameState.yOffset = yy - y - height - 1;
					// Allows you to drop through semi-solid floors
					// Currently doesn't work with the new larger hitBox
					if (cL.get(i).getID() == LineType.SEMISOLIDFLOOR) {
						dropable = true;
						dropFloor = i;
					}
				}
			}
			// If you aren't currently standing somewhere, jumping, or clinging to
			// a wall this will turn on gravity
			else {
				if (!bottomCollision && !jumping && !wallClingRight && !wallClingLeft) {
					falling = true;
					bottomCollision = false;
					if (!attacking)
					{
					if (facing == 0)
						setAnimation(Animation.FALLRIGHT);
					else
						setAnimation(Animation.FALLLEFT);
					}
					dropable = false;
					// currentAnimation = 5;
				}
			}
			hitBoxBottom.x = tempx;
			hitBoxBottom.y = tempy;
			// Reverts hitBox location
			// End of bottom collision
		} // End of collisionLine for loop

	}

	private void changePosition(double currentY, ArrayList<CollisionLine> cL) {
		// TODO Auto-generated method stub
		// If you are moving right and not in a wallJump
		if ((right) && (!wallJumpFromLeft || !wallJumpFromRight)) {

			// Simply move to the right
			GameState.xOffset += moveSpeed;

			// revert moveSpeed in case of slopes or speed change
			moveSpeed = 5;
		}
		// If you are moving left and not in a wallJump
		if ((left) && (!wallJumpFromLeft || !wallJumpFromRight)) {

			// Simply move to the left
			GameState.xOffset -= moveSpeed;
			// revert moveSpeed in case of slopes or speed change
			moveSpeed = 5;
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
			if (attackTime == 36)
			{
				if (!jumping && !falling) {
				System.out.println("Play animation");
				attacking = true;
				if (facing == 0)
				setAnimation(Animation.ATTACKINGRIGHT);
				else
				setAnimation(Animation.ATTACKINGLEFT);
				}
				else
				{
					//TODO
					//Jump attack animation
				}
			}
		}
		if (k == KeyEvent.VK_D) {
			// System.out.println("d pressed");
			right = true;
			// setAnimation(2);
			// Let go of left wall
			wallClingLeft = false;
		}
		if (k == KeyEvent.VK_A) {
			// System.out.println("a pressed");
			left = true;
			// setAnimation(3);
			// Let go of right wall
			wallClingRight = false;
		}
		if (k == KeyEvent.VK_S) {
			down = true;
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

	// Key listener
	public void keyReleased(int k) {
		if (k == KeyEvent.VK_D) {
			// System.out.println("d released");
			right = false;
			// setAnimation(0);
		}
		if (k == KeyEvent.VK_A) {
			// System.out.println("a released");
			left = false;
			// setAnimation(1);
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
		}
	}

}
