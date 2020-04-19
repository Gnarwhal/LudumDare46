package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.audio.Sound;
import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.display.Window;
import com.gnarwhal.ld46.engine.model.ColRect;
import com.gnarwhal.ld46.engine.model.Rect;
import com.gnarwhal.ld46.engine.shaders.Shader2e;
import com.gnarwhal.ld46.engine.shaders.Shader2t;
import com.gnarwhal.ld46.engine.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Vector;

import static com.gnarwhal.ld46.engine.display.Window.BUTTON_PRESSED;
import static com.gnarwhal.ld46.game.Main.adtime;
import static com.gnarwhal.ld46.game.Main.dtime;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Rect {

	private static final int
			STATE_REST  = 0x00,
			STATE_LEFT  = 0x02,
			STATE_RIGHT = 0x01,
			STATE_UP    = 0x03,
			STATE_DOWN  = 0x04;

	private static final Vector2f TEXTURE_DIMS = new Vector2f(0.5f, 0.2f);

	private static final Vector3f[] COLORS = new Vector3f[] {
		new Vector3f(1,    0, 0   ),
		new Vector3f(0.1f, 1, 0.5f),
		new Vector3f(0,    1, 1   ),
		new Vector3f(1,    0, 1   )
	};

	private static final float
		UP_ANGLE_DEVIATION   = (float) Math.PI / 12.0f,
		SIDE_ANGLE_DEVIATION = (float) Math.PI / 3.0f,
		DOWN_ANGLE_DEVIATION = (float) Math.PI / 12.0f;

	private static final float
			UP_ANGLE_RANGE   = (float) Math.PI * 2.0f / 3.0f,
			SIDE_ANGLE_RANGE = (float) Math.PI * 3.0f / 4.0f,
			DOWN_ANGLE_RANGE = (float) Math.PI * 2.0f / 3.0f;

	// sour spot power, sweet spot power
	private static final Vector2f
		UP_ATTACK_POWER   = new Vector2f(1024, 1600),
		SIDE_ATTACK_POWER = new Vector2f(1024, 1280),
		DOWN_ATTACK_POWER = new Vector2f(1024, 1600);

	private static final float
		LEFT_ANGLE_ORIGIN  = (float) Math.PI * -5.0f / 6.0f,
		RIGHT_ANGLE_ORIGIN = (float) Math.PI * -1.0f / 6.0f,
		UP_ANGLE_ORIGIN    = (float) Math.PI * -0.5f,
		DOWN_ANGLE_ORIGIN  = (float) Math.PI *  0.5f;


	private static final Vector4f COLLISION_OFFSETS = new Vector4f(6.0f, 6.0f,  5.0f, 3.5f).div(24.0f);
	                                                            // LEFT, RIGHT, TOP,  BOTTOM

	private static Sound sweet;
	private static Sound sour;

	private Vector4f scaledCollisionOffsets;

	private int state;
	private Vector2f sprite;

	private Texture[] textures;
	private Shader2t shader;

	private Window window;

	private Vector3f[] positions;
	private Vector3f offset;
	private float idleTime;

	private Vector3f velocity;

	private boolean grounded = false;

	private Shader2e effectShader;
	private float effectTime;

	private float attackTimer;
	private float attackDelay;
	private float attackAngle;
	private float attackAngleRange;
	private float angleDeviation;
	private Vector2f attackPower;
	private boolean hit;

	private float jumpTime;
	private boolean jumpReset;

	//private ColRect rect;

	public Player(Window window, Camera camera) {
		super(camera, 0, 0, -0.1f, 256, 256, 0, false);
		this.window = window;

		scaledCollisionOffsets = new Vector4f(COLLISION_OFFSETS).mul(width, width, height, height);

		textures = new Texture[] {
			new Texture("res/img/player/player_legs.png"),
			new Texture("res/img/player/player_head.png"),
			new Texture("res/img/player/player_arms.png"),
			new Texture("res/img/player/player_effect.png"),
		};

		positions = new Vector3f[] {
			new Vector3f(),
			new Vector3f(),
			new Vector3f()
		};
		offset = new Vector3f();
		idleTime = 0;

		shader = new Shader2t();
		effectShader = new Shader2e();

		velocity = new Vector3f();

		sprite = new Vector2f();
		attackTimer = 10000;
		hit = false;

		jumpTime  = 0;
		jumpReset = true;

		//rect = new ColRect(camera, 0, 0, 0, 100, 100, 1, 0, 0, 1, false);

		if (sour == null) {
			sweet = new Sound("res/audio/sweet.wav");
			sour  = new Sound("res/audio/sour.wav");
		}
	}

	public void update(Platform[] platforms, Egg egg) {
		final float ATTACK_RESET = 0.0f;
		if (state == STATE_REST && attackTimer >= ATTACK_RESET) {
			if (window.keyPressed(GLFW_KEY_LEFT) == BUTTON_PRESSED) {
				state            = STATE_LEFT;
				attackAngle      = LEFT_ANGLE_ORIGIN;
				angleDeviation   = SIDE_ANGLE_DEVIATION;
				attackPower      = SIDE_ATTACK_POWER;
				attackAngleRange = SIDE_ANGLE_RANGE;
				//rect.setColor(1, 0, 0);
			} else if (window.keyPressed(GLFW_KEY_RIGHT) == BUTTON_PRESSED) {
				state            = STATE_RIGHT;
				attackAngle      = RIGHT_ANGLE_ORIGIN;
				angleDeviation   = SIDE_ANGLE_DEVIATION;
				attackPower      = SIDE_ATTACK_POWER;
				attackAngleRange = SIDE_ANGLE_RANGE;
				//rect.setColor(1, 0, 0);
			} else if (window.keyPressed(GLFW_KEY_UP) == BUTTON_PRESSED) {
				state            = STATE_UP;
				attackAngle      = UP_ANGLE_ORIGIN;
				angleDeviation   = UP_ANGLE_DEVIATION;
				attackPower      = UP_ATTACK_POWER;
				attackAngleRange = UP_ANGLE_RANGE;
				//rect.setColor(1, 0, 0);
			} else if (window.keyPressed(GLFW_KEY_DOWN) == BUTTON_PRESSED) {
				state            = STATE_DOWN;
				attackAngle      = DOWN_ANGLE_ORIGIN;
				angleDeviation   = DOWN_ANGLE_DEVIATION;
				attackPower      = DOWN_ATTACK_POWER;
				attackAngleRange = DOWN_ANGLE_RANGE;
				//rect.setColor(1, 0, 0);
			}
			if (state != STATE_REST) {
				attackTimer = 0;
			}
		}
		final float ATTACK_BEGIN  = 0.05f;
		final float ATTACK_ACTIVE = 0.25f;
		final float ATTACK_HOLD   = 0.3f;
		final float ATTACK_END    = 0.4f;
		if (state != STATE_REST) {
			if (attackTimer < ATTACK_BEGIN) {
				sprite.set(0, 0.2f * state);
				effectTime = 0;
			} else if (attackTimer < ATTACK_ACTIVE) {
				sprite.set(0.5f, 0.2f * state);
				effectTime = Math.min((attackTimer - ATTACK_BEGIN) / (ATTACK_ACTIVE - ATTACK_BEGIN) * 4, 1);
			} else if (attackTimer < ATTACK_HOLD + attackDelay) {
				sprite.set(0.5f, 0.2f * state);
				effectTime = 1;
			} else if (attackTimer < ATTACK_END + attackDelay) {
				sprite.set(0.5f, 0.2f * state);
				effectTime = 1 + (attackTimer - attackDelay - ATTACK_HOLD) / (ATTACK_END - ATTACK_HOLD);
			} else {
				hit = false;
				state = STATE_REST;
				attackTimer = 0;
				idleTime = 0;
				attackDelay = 0;
			}
			offset.set(0, 0, 0);
		} else {
			sprite.set(0, 0);
		}
		attackTimer += adtime;

		final float DECELERATION             = 1024;
		final float DIRECTIONAL_ACCELERATION = 4096;
		final float HIGH_SPEED_DECELERATION  = 3120;
		final float TERMINAL_VELOCITY        = 1762;
		final float GRAVITY                  = 1762;
		final float SPEED                    = 1024;

		// R.I.P. STRONTCH_AND_SCRONTCH 2020-2020

		float friction = 0;
		float floor = 0;
		Vector3f acceleration = new Vector3f(0, GRAVITY, 0);

		if (window.keyPressed(GLFW_KEY_A) >= BUTTON_PRESSED) {
			floor = -SPEED;
			if (velocity.x > -SPEED) {
				velocity.x -= DIRECTIONAL_ACCELERATION * dtime;
				if (velocity.x < -SPEED) {
					velocity.x = -SPEED;
				}
			}
		} else if (window.keyPressed(GLFW_KEY_D) >= BUTTON_PRESSED) {
			floor = SPEED;
			if (velocity.x < SPEED) {
				velocity.x += DIRECTIONAL_ACCELERATION * dtime;
				if (velocity.x > SPEED) {
					velocity.x = SPEED;
				}
			}
		} else if (velocity.x != 0) {
			float absVelX = Math.abs(velocity.x);
			if (absVelX > SPEED) {
				friction = -HIGH_SPEED_DECELERATION * (velocity.x / absVelX);
			} else {
				friction = -DECELERATION * (velocity.x / absVelX);
			}
		}

		if (window.keyPressed(GLFW_KEY_S) == BUTTON_PRESSED) {
			velocity.y = TERMINAL_VELOCITY;
		} else {
			float absVelocityY = Math.abs(velocity.y());
			if (absVelocityY > TERMINAL_VELOCITY) {
				velocity.y = (velocity.y / absVelocityY) * TERMINAL_VELOCITY;
			}
		}

		final float FULL_HOP_TIME                  = 5.0f / 60.0f;
		final float SHORT_HOP_SPEED                = 694;
		final float FULL_HOP_SPEED                 = 1024;
		final float HORIZONTAL_FULL_HOP_INFLUENCE  = 512;
		final float HORIZONTAL_SHORT_HOP_INFLUENCE = 382;
		if ((window.keyPressed(GLFW_KEY_W) >= BUTTON_PRESSED
		  || window.keyPressed(GLFW_KEY_SPACE) >= BUTTON_PRESSED)) {
			if (jumpTime >= FULL_HOP_TIME && jumpReset) {
				jumpReset = false;
				velocity.y = -FULL_HOP_SPEED;
				if (window.keyPressed(GLFW_KEY_A) >= BUTTON_PRESSED) {
					velocity.x -= HORIZONTAL_FULL_HOP_INFLUENCE;
					if (velocity.x < -SPEED) {
						velocity.x = -SPEED;
					}
				} else if (window.keyPressed(GLFW_KEY_D) >= BUTTON_PRESSED) {
					velocity.x += HORIZONTAL_FULL_HOP_INFLUENCE;
					if (velocity.x > SPEED) {
						velocity.x = SPEED;
					}
				}
			}
			jumpTime += dtime;
		} else if (jumpTime > 0) {
			if (jumpTime < FULL_HOP_TIME) {
				velocity.y = -SHORT_HOP_SPEED;
				if (window.keyPressed(GLFW_KEY_A) >= BUTTON_PRESSED) {
					velocity.x -= HORIZONTAL_SHORT_HOP_INFLUENCE;
					if (velocity.x < -SPEED) {
						velocity.x = -SPEED;
					}
				} else if (window.keyPressed(GLFW_KEY_D) >= BUTTON_PRESSED) {
					velocity.x += HORIZONTAL_SHORT_HOP_INFLUENCE;
					if (velocity.x > SPEED) {
						velocity.x = SPEED;
					}
				}
			}
			jumpTime  = 0;
			jumpReset = true;
		}

		if (velocity.x == 0 && grounded) {
			final float SCALE = 15;
			final float RATE  = (float) (2 * Math.PI / 2.5); // Last number is seconds per loop
			offset.y = (float) (SCALE * Math.sin(RATE * idleTime));
			idleTime += dtime;
		} else {
			idleTime = 0;
			offset.y = 0;
		}

		velocity.add(acceleration.mul((float) dtime, new Vector3f()));
		float degradedVelocity = velocity.x() + friction * (float) dtime;
		// If the acceleration due to friction would accelerate it past the speed floor set it to the speed floor
		if ((degradedVelocity - floor) * (velocity.x() - floor) < 0) {
			velocity.x = floor;
			// otherwise just apply the friction
		} else {
			velocity.x = degradedVelocity;
		}

		/*if (state == STATE_GROUNDED) {
			final float MAX_HORZ_VELOCITY = 1024;
			final float HORZ_ACCELERATION = 4096;
			if (window.keyPressed(GLFW_KEY_A) >= BUTTON_PRESSED) {
				velocity.x -= HORZ_ACCELERATION * dtime;
				if (velocity.x < -MAX_HORZ_VELOCITY) {
					velocity.x = -MAX_HORZ_VELOCITY;
				}
			} else if (window.keyPressed(GLFW_KEY_D) >= BUTTON_PRESSED) {
				velocity.x += HORZ_ACCELERATION * dtime;
				if (velocity.x > MAX_HORZ_VELOCITY) {
					velocity.x = MAX_HORZ_VELOCITY;
				}
			} else {
				velocity.x *=
			}
		}

		velocity.y += (float) (GRAVITY * dtime);
		if (velocity.y > TERMINAL_VELOCITY) {
			velocity.y = TERMINAL_VELOCITY;
		}

		position.add(velocity.mul((float) Main.dtime, new Vector3f()));*/

		// --- C O L L I S I O N --- //

		Vector3f translation = velocity.mul((float) dtime, new Vector3f());
		Vector3f translationCopy = new Vector3f(translation);

		if (window.keyPressed(GLFW_KEY_LEFT_SHIFT) < BUTTON_PRESSED && window.keyPressed(GLFW_KEY_RIGHT_SHIFT) < BUTTON_PRESSED && translation.y > 0 && (window.keyPressed(GLFW_KEY_S) != BUTTON_PRESSED || !grounded)) {
			Vector3f bottomLeft  = new Vector3f(position).add(        scaledCollisionOffsets.x, height - scaledCollisionOffsets.w, 0);
			Vector3f bottomRight = new Vector3f(position).add(width - scaledCollisionOffsets.y, height - scaledCollisionOffsets.w, 0);
			for (int i = 0; i < platforms.length; ++i) {
				Vector3f porigin      = platforms[i].getOrigin();
				Vector3f ptranslation = platforms[i].getTranslation();
				float s1, t1, s2, t2;
				if (translation.x != 0) {
					s1 = ((porigin.y - bottomLeft.y)  / translation.y - (porigin.x - bottomLeft.x)  / translation.x) / (ptranslation.x / translation.x - ptranslation.y / translation.y);
					s2 = ((porigin.y - bottomRight.y) / translation.y - (porigin.x - bottomRight.x) / translation.x) / (ptranslation.x / translation.x - ptranslation.y / translation.y);
				} else {
					s1 = (bottomLeft.x  - porigin.x) / ptranslation.x;
					s2 = (bottomRight.x - porigin.x) / ptranslation.x;
				}
				t1 = (ptranslation.y * s1 + porigin.y - bottomLeft.y)  / translation.y;
				t2 = (ptranslation.y * s1 + porigin.y - bottomRight.y) / translation.y;
				if (s1 >= 0 && s1 <= 1 && t1 >= 0 && t1 <= 1) {
					if (t2 < t1 && s2 >= 0 && s2 <= 1 && t2 >= 0 && t2 <= 1) {
						translation.y *= t2;
					} else {
						translation.y *= t1;
					}
				} else if (s2 >= 0 && s2 <= 1 && t2 >= 0 && t2 <= 1) {
					translation.y *= t2;
				}
			}
		}

		grounded = false;
		if (!translation.equals(translationCopy, 0)) {
			velocity.y = 0;
			grounded = true;
		}

		position.add(translation);

		// Check y bounds
		if (position.y + height - scaledCollisionOffsets.w > camera.getHeight()) {
			position.y = camera.getHeight() - height + scaledCollisionOffsets.w;
			velocity.y = 0;
			grounded = true;
		} else {
			if (position.y + scaledCollisionOffsets.y < 0) {
				position.y = -scaledCollisionOffsets.y;
				velocity.y = 0;
			}
		}
		// Check X bounds
		if (position.x + scaledCollisionOffsets.x < 0) {
			position.x = -scaledCollisionOffsets.x;
			velocity.x = 0;
		} else if (position.x + width - scaledCollisionOffsets.z > camera.getWidth()) {
			position.x = camera.getWidth() - width + scaledCollisionOffsets.z;
			velocity.x = 0;
		}

		// --- E N D   C O L L I S I O N --- //

		if (state == STATE_REST) {
			// Stretch physics --- FRAME INDEPENDENT??? MAYBBBBE????????
			final float MAX_DISTANCE = 10;
			final float ELASTICITY = idleTime == 0 ? 0.6f : 0.15f;
			positions[0].set(position).add(offset);
			Vector3f displacement = positions[2].sub(positions[0], new Vector3f()).mul(1 - (ELASTICITY * (float) dtime * 60.0f));
			//displacement.x = 0;
			float length = displacement.length();
			if (length > MAX_DISTANCE) {
				displacement.div(length).mul(MAX_DISTANCE);
			}
			positions[2].set(positions[0]).add(displacement);
			displacement.set(positions[1]).sub(positions[2]).mul(1 - ELASTICITY);
			//displacement.x = 0;
			length = displacement.length();
			if (length > MAX_DISTANCE) {
				displacement.div(length).mul(MAX_DISTANCE);
			}
			positions[1].set(positions[2]).add(displacement);
		} else {
			for (int i = 0; i < 3; ++i) {
				positions[i].set(position);
			}
		}

		if (state != STATE_REST && attackTimer > ATTACK_BEGIN && attackTimer <= ATTACK_ACTIVE && !hit) {
			final float HITBOX_RADIUS     = 97.333f;
			final float SWEET_SPOT_RADIUS = 20.0f;

			Vector3f displacement = egg.getOrigin().sub(width / 2, height / 2, 0).sub(position);
			displacement.z = 0;
			//rect.setPosition(new Vector3f(position).add(displacement.mul(HITBOX_RADIUS / displacement.length(), new Vector3f())).add(width / 2, height / 2, 0));
			float theta = (float) Math.atan2(displacement.y, displacement.x);
			float difference = Math.abs(theta - attackAngle);
			if (difference > Math.PI) {
				difference = 2.0f * (float) Math.PI - difference;
			}
			if (difference < attackAngleRange) {
				float radius = egg.getRadius(theta);
				float distance = displacement.length();


				float launchVelocity = 0;
				float blah = distance - HITBOX_RADIUS;
				if (-SWEET_SPOT_RADIUS < blah && blah < SWEET_SPOT_RADIUS) {
					hit = true;
					launchVelocity = attackPower.y;
					sweet.play(false);

					final float SWEET_SPOT_FREEZE_DURATION = 0.35f;
					Main.freeze(SWEET_SPOT_FREEZE_DURATION);
					attackDelay = SWEET_SPOT_FREEZE_DURATION;
				} else if (-radius < blah && blah < radius) {
					hit = true;
					launchVelocity = attackPower.x;
					sour.play(false);
				}
				if (launchVelocity != 0) {
					//rect.setColor(0, 1, 0);
					float launchAngle = (theta - attackAngle) / (float) Math.PI * 3.0f * angleDeviation + attackAngle;
					egg.launch(new Vector3f(launchVelocity * (float) Math.cos(launchAngle), launchVelocity * (float) Math.sin(launchAngle), 0));
				}
			}
		}
	}

	public void render() {
		shader.enable();
		shader.setSubtexture(sprite, TEXTURE_DIMS);
		for (int i = 0; i < 3; ++i) {
			textures[i].bind();
			shader.setMVP(camera.getMatrix().translate(positions[i].add(width * scale / 2, height * scale / 2, 0.01f * i, new Vector3f())).rotateZ(rotation).scale(width * scale, height * scale, 1).translate(-0.5f, -0.5f, 0));
			vao.render();
		}
		if (state != STATE_REST) {
			effectShader.enable();
			effectShader.setSubtexture(sprite, TEXTURE_DIMS);
			effectShader.setTime(effectTime);
			effectShader.setColor(COLORS[state - 1]);
			effectShader.setMVP(camera.getMatrix().translate(positions[0].add(width * scale / 2, height * scale / 2, 0.03f, new Vector3f())).rotateZ(rotation).scale(width * scale, height * scale, 1).translate(-0.5f, -0.5f, 0));
			textures[3].bind();
			vao.render();
		}
		//rect.render();
	}
}
