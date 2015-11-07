/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.gameobject.entities.ActionState;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.collision.LineInteractiveCollision;
import game.gameobject.interactive.activator.UpdateBasedActivator;
import game.gameobject.stats.MobStats;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import sprites.Animation;
import sprites.SpriteSheet;

import static game.logic.navmeshpathfinding.PathData.OBSTACLE_BETWEEN;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Tongub extends Mob {

	private final static byte ATTACK_NORMAL = 0;
	private final Animation animation;
	private int seconds = 0, max = 5;
	private ActionState idle, run_away, hide, attack, wander;
	private Delay attack_delay = Delay.createInMilliseconds(1500);           //TODO - te wartości losowe i zależne od poziomu trudności
	private Delay rest = Delay.createInMilliseconds(250);            //TODO - te wartości losowe i zależne od poziomu trudności
	private boolean attacking, undig, side;
	private RandomGenerator random = RandomGenerator.create((int) System.currentTimeMillis());

	{
		idle = new ActionState() {
			@Override
			public void update() {
//                System.out.println("IDLE");
				if (rest.isOver()) {
					brake(2);
					lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
					if (closeEnemies.size() * 2 <= closeFriends.size()) {
						GameObject closerEnemy = getCloserEnemy();
						if (closerEnemy != null && isInHalfHearingRange(closerEnemy)) {
							state = attack;
							target = closerEnemy;
							attack_delay.start();
						}
					} else {
						calculateDestinationsForEscape();
						GameObject closerEnemy = getCloserEnemy();
						if (closerEnemy != null) {
							state = hide;
							destination.set(-1, -1);
							stats.setProtectionState(true);
						} else if (destination.getX() > 0) {
							state = run_away;
							destination.set(-1, -1);
						} else {
							state = wander;
							destination.set(getX(), getY());
							seconds = 0;
						}
					}
				}
			}

		};
		run_away = new ActionState() {
			@Override
			public void update() {
//                System.out.println(RUN_AWAY);
				if (destination.getX() > 0) {
					secondaryDestination.set(destination.getX(), destination.getY());
				}
				lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
				calculateDestinationsForEscape();
				goTo(destination.getX() > 0 ? destination : secondaryDestination);
				GameObject closerEnemy = getCloserEnemy();
				if (closerEnemy != null) {
					state = hide;
					destination.set(-1, -1);
					secondaryDestination.set(-1, -1);
					stats.setProtectionState(true);
				} else if (destination.getX() < 0 && (secondaryDestination.getX() < 0 || Methods.pointDistanceSimple2(getX(), getY(), secondaryDestination
						.getX(), secondaryDestination.getY()) < 4 * hearRange2 / 9)) {
					state = idle;
					secondaryDestination.set(-1, -1);
					destination.set(-1, -1);
				}
			}
		};
		hide = new ActionState() {
			@Override
			public void update() {
//                System.out.println("HIDE");
				brake(2);
				if (animation.getDirectionalFrameIndex() == 16) {
					collision.setCollide(false);
					collision.setHitable(false);
					lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
					GameObject closerEnemy = getCloserEnemy();
					if (closerEnemy == null) {
						stats.setProtectionState(false);
						undig = true;
						collision.setCollide(true);
						collision.setHitable(true);
					}
				} else if (!stats.isProtectionState() && animation.getDirectionalFrameIndex() == 22) {
					state = idle;
				}
			}
		};
		attack = new ActionState() {
			@Override
			public void update() {
//                System.out.println("ATTACK");
				if (rest.isOver()) {
					if (attack_delay.isOver()) {
						if (attacking || side) {
							rest.start();
							attacking = false;
							side = false;
							brake(2);
							getAttackActivator(ATTACK_NORMAL).setActivated(false);
						} else {
							attack_delay.setFrameLengthInMilliseconds(1000 + random.next(9));
							attack_delay.start();
							attacking = true;
							side = false;
							getAttackActivator(ATTACK_NORMAL).setActivated(true);
						}
					} else {
						if (side) {
							chargeToPoint(destination);
						} else {
							if (getPathData().isTrue(OBSTACLE_BETWEEN)) {
								goTo(target.getX(), target.getY());
							} else {
								charge();
							}
							if (getInteractive(ATTACK_NORMAL).isActivated()) {
								getAttackActivator(ATTACK_NORMAL).setActivated(false);
								maxSpeed = 3;
								side = true;
								attacking = false;
								closeRandomDestination(getX(), getY());
								brake(2);
							} else {
								maxSpeed = 5;
								getAttackActivator(ATTACK_NORMAL).setActivated(true);
							}
						}

					}
					lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
					if (!attacking && (!isInRange(target) || target.getMap() != map || closeFriends.size() < 2)) {
						state = idle;
						target = null;
						brake(2);
					}
				} else {
					brake(2);
				}
			}
		};
		wander = new ActionState() {
			@Override
			public void update() {
//                System.out.println("WANDER");
				if (rest.isOver()) {
					if (Methods.pointDistanceSimple2(getX(), getY(), destination.getX(), destination.getY()) <= sightRange2 / 16) {
						closeRandomDestination(homePosition.getX(), homePosition.getY());
//                        System.out.println(destination);
					}
					seconds++;
					if (seconds > max) {
						seconds = 0;
						max = random.next(4);
					}
					rest.start();
				}
				lookForCloseEntities(place.players, map.getArea(area).getNearSolidMobs());
				if (!closeEnemies.isEmpty() || (!alpha && !closeFriends.isEmpty())) {
					state = idle;
					destination.set(-1, -1);
				}
				goTo(destination);
			}
		};
	}

	public Tongub(int x, int y, Place place, short ID) {
		super(x, y, 3, 768, "Tongub", place, "tongub", true, ID);
		setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
		animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 23);
		setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
		appearance = animation;
		collision.setMobile(true);
		stats = new MobStats(this);
		stats.setStrength(10);
		stats.setDefence(1);
		stats.setWeight(20);
		stats.setMaxHealth(100);
		stats.setHealth(100);
		rest.start();
		attack_delay.start();
		state = idle;
		homePosition.set(getX(), getY());
		addInteractive(Interactive.createNotWeapon(this, new UpdateBasedActivator(), new LineInteractiveCollision(0, 64, 0, 16, 16), Interactive.STRENGTH_HURT, ATTACK_NORMAL, 0.5f));
	}

	private void closeRandomDestination(int xD, int yD) {
		int sign = random.next(1) == 1 ? 1 : -1;
		int shift = (sightRange + random.next(9)) * sign;
		destination.setX(xD + shift);
		sign = random.next(1) == 1 ? 1 : -1;
		shift = (sightRange + random.next(9)) * sign;
		destination.setY(yD + shift);
		if (destination.getX() < sightRange / 2) {
			destination.setX(sightRange / 2);
		}
		if (destination.getX() > map.getWidth()) {
			destination.setX(map.getWidth() - sightRange / 2);
		}
		if (destination.getY() < collision.getHeight()) {
			destination.setY(sightRange / 2);
		}
		if (destination.getY() > map.getHeight()) {
			destination.setY(map.getHeight() - sightRange / 2);
		}
	}

	private GameObject getCloserEnemy() {
		for (GameObject object : closeEnemies) {
			if (isInHalfHearingRange(object)) {
				return object;
			}
		}
		return null;
	}

	@Override
	public void update() {
		if (isHurt()) {
			updateGettingHurt();
			runWhenHurt();
		} else {
			state.update();
			normalizeSpeed();
			updateAnimation();
		}
		updateChangers();
		updateWithGravity();
		moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
		brakeOthers();
	}

	@Override
	public void getHurt(int knockBackPower, double jumpPower, GameObject attacker) {
		super.getHurt(knockBackPower, jumpPower, attacker);
	}

	private void updateGettingHurt() {
		setDirection8way(Methods.pointAngle8Directions(knockBack.getXSpeed(), knockBack.getYSpeed(), 0, 0));
		animation.animateSingleInDirection(getDirection8Way(), 1);
		brake(2);
	}

	private void runWhenHurt() {
		if (closeEnemies.isEmpty()) {
			state = run_away;
			destination.set(-1, -1);
			secondaryDestination.set(-1, -1);
			stats.setProtectionState(false);
			setPathStrategy(PathFindingModule.GET_CLOSE, sightRange / 4);
			destination.set(getX() + (int) Methods.xRadius(knockBack.getAttackerDirection(), sightRange),
					getY() - (int) Methods.yRadius(knockBack.getAttackerDirection(), sightRange));
		}
	}


	/*
	 <('-^<) PRZEMKO-SCIAGA (>^-')>
	 animation.animateSingleInDirection(getDirection8Way(), 0); - poza neutralna
	 animation.animateSingleInDirection(getDirection8Way(), 1); - zranienie
    
	 ~30fps?
	 animation.animateIntervalInDirection(getDirection8Way(), 2, 6); - chód
    
	 animation.animateIntervalInDirection(getDirection8Way(), 7, 10); - atak
    
	 animation.animateIntervalInDirection(getDirection8Way(), 11, 16); - wkopanie
	 animation.animateIntervalInDirection(getDirection8Way(), 17, 22); - wykopanie
	 */
	private void updateAnimation() {
		if (Math.abs(xSpeed) >= 0.1 || Math.abs(ySpeed) >= 0.1) {
			pastDirections[currentPastDirection++] = Methods.pointAngle8Directions(0, 0, xSpeed, ySpeed);
			if (currentPastDirection > 1) {
				currentPastDirection = 0;
			}
			if (pastDirections[0] == pastDirections[1]) {
				setDirection(pastDirections[0] * 45);
			}
			if (attacking) {
				animation.setFPS(30);
				animation.animateIntervalInDirection(getDirection8Way(), 7, 10);
			} else {
				animation.setFPS(15);
				animation.animateIntervalInDirection(getDirection8Way(), 2, 6);
			}
		} else {
			if (stats.isProtectionState()) {
				animation.setFPS(15);
				animation.animateIntervalInDirection(getDirection8Way(), 11, 16);
				animation.setStopAtEnd(true);
			} else if (undig) {
				animation.setFPS(15);
				animation.animateIntervalInDirection(getDirection8Way(), 17, 22);
				animation.setStopAtEnd(true);
				if (animation.getDirectionalFrameIndex() == 22) {
					undig = false;
				}
			} else {
				animation.animateSingleInDirection(getDirection8Way(), 0);
			}
		}
	}

	@Override
	public void render(int xEffect, int yEffect) {
		if (appearance != null) {
			glPushMatrix();
			glTranslatef((int) (getX() * Place.getCurrentScale() + xEffect), (int) (getY() * Place.getCurrentScale() + yEffect), 0);
			Drawer.setColor(JUMP_SHADOW_COLOR);
			Drawer.drawEllipse(0, 0, Methods.roundDouble(collision.getWidth() * Place.getCurrentScale() / 2f), Methods.roundDouble(collision.getHeight()
					* Place.getCurrentScale() / 2f), 24);
			glTranslatef(0, -(int) (floatHeight * Place.getCurrentScale()), 0);
			Drawer.refreshColor();
//			Drawer.renderStringCentered(name, 0, -(((appearance.getActualHeight()) * Place.getCurrentScale()) / 2), place.standardFont, map.getLightColor());
			glPopMatrix();

			if (Main.SHOW_INTERACTIVE_COLLISION) {
				interactiveObjects.stream().forEach((interactive) -> {
					interactive.render(xEffect, yEffect);
				});
			}

			glPushMatrix();
			glTranslatef(xEffect, yEffect, 0);
			glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
			glTranslatef(getX(), (int) (getY() - floatHeight), 0);
			appearance.render();
			appearance.updateFrame();
			Drawer.refreshColor();
			glPopMatrix();
		}
	}
}
