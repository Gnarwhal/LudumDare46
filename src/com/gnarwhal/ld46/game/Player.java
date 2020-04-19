package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.display.Window;
import com.gnarwhal.ld46.engine.model.Rect;
import com.gnarwhal.ld46.engine.shaders.Shader2t;
import com.gnarwhal.ld46.engine.texture.Texture;
import jdk.jfr.Percentage;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import static com.gnarwhal.ld46.engine.display.Window.BUTTON_PRESSED;
import static com.gnarwhal.ld46.game.Main.dtime;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Rect {

	private Texture[] textures;
	private Shader2t shader;

	private Window window;

	private Vector3f[] positions;
	private Vector3f offset;
	private float idleTime;

	private Vector3f velocity;

	private boolean grounded = false;

	public Player(Window window, Camera camera) {
		super(camera, 0, 0, -0.1f, 200, 200, 0, false);
		this.window = window;

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

		velocity = new Vector3f();
	}

	public void update() {

		final float JUMP_SPEED              = 812;
		final float DECELERATION            = 1762;
		final float HIGH_SPEED_DECELERATION = 2048;
		final float TERMINAL_VELOCITY       = 1762;
		final float GRAVITY                 = 1762;

		final float speed = 768;

		float friction = 0;
		float floor = 0;
		Vector3f acceleration = new Vector3f(0, GRAVITY, 0);

		if (window.keyPressed(GLFW_KEY_A) >= BUTTON_PRESSED) {
			floor = -speed;
			if (velocity.x > -speed)
				velocity.x = -speed;
			else
				friction = HIGH_SPEED_DECELERATION;

		}
		else if (window.keyPressed(GLFW_KEY_D) >= BUTTON_PRESSED) {
			floor = speed;
			if (velocity.x < speed) {
				velocity.x = speed;
			} else {
				friction = -HIGH_SPEED_DECELERATION;
			}
		} else if (velocity.x != 0) {
			float absVelX = Math.abs(velocity.x);
			if (absVelX > speed) {
				friction = -HIGH_SPEED_DECELERATION * (velocity.x / absVelX);
			} else {
				friction = -DECELERATION * (velocity.x / absVelX);
			}
		}

		if (window.keyPressed(GLFW_KEY_S) == BUTTON_PRESSED && velocity.y >= 0) {
			velocity.y = TERMINAL_VELOCITY;
		} else {
			float absVelocityY = Math.abs(velocity.y());
			if (absVelocityY > TERMINAL_VELOCITY) {
				velocity.y = (velocity.y / absVelocityY) * TERMINAL_VELOCITY;
			}
		}

		if ((window.keyPressed(GLFW_KEY_W)     == BUTTON_PRESSED
		  || window.keyPressed(GLFW_KEY_SPACE) == BUTTON_PRESSED)) {
			velocity.y = -JUMP_SPEED;
		}

		if (velocity.x == 0 && grounded) {
			final float SCALE = 10;
			final float RATE  = (float) (2 * Math.PI / 2.5); // Last number is seconds per loop
			offset.y = (float) (SCALE * Math.sin(RATE * idleTime));
			idleTime += dtime;
		} else {
			idleTime = 0;
			offset.y = 0;
		}

		grounded = false;
		velocity.add(acceleration.mul((float) dtime, new Vector3f()));
		float degradedVelocity = velocity.x() + friction * (float) dtime;
		// If the acceleration due to friction would accelerate it past the speed floor set it to the speed floor
		if ((degradedVelocity - floor) * (velocity.x() - floor) < 0) {
			velocity.x = floor;
			// otherwise just apply the friction
		} else {
			velocity.x = degradedVelocity;
		}

		position.add(velocity.mul((float) dtime, new Vector3f()));

		/*
		if (state == STATE_GROUNDED) {
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

		// Check y bounds
		if (position.y + height > camera.getHeight()) {
			position.y = camera.getHeight() - height;
			velocity.y = 0;
			grounded = true;
		} else {
			grounded = false;
			if (position.y < 0) {
				position.y = 0;
				velocity.y = 0;
			}
		}
		// Check X bounds
		if (position.x < 0) {
			position.x = 0;
			velocity.x = 0;
		} else if (position.x + width > camera.getWidth()) {
			position.x = camera.getWidth() - width;
			velocity.x = 0;
		}

		final float MAX_DISTANCE = 10;
		final float ELASTICITY = 0.5f;
		positions[0].set(position).add(offset);
		Vector3f displacement = positions[2].sub(positions[0], new Vector3f()).mul(1 - ELASTICITY);
		displacement.x = 0;
		float length = displacement.length();
		if (length > MAX_DISTANCE) {
			displacement.div(length).mul(MAX_DISTANCE);
		}
		positions[2].set(positions[0]).add(displacement);
		displacement.set(positions[1]).sub(positions[2]).mul(1 - ELASTICITY);
		displacement.x = 0;
		length = displacement.length();
		if (length > MAX_DISTANCE) {
			displacement.div(length).mul(MAX_DISTANCE);
		}
		System.out.println(displacement);
		positions[1].set(positions[2]).add(displacement);
	}

	public void render() {
		shader.enable();
		shader.setSubtexture(new Vector2f(0, 0), new Vector2f(0.5f, 0.2f));
		for (int i = 0; i < 3; ++i) {
			textures[i].bind();
			shader.setMVP(camera.getMatrix().translate(positions[i].add(width * scale / 2, height * scale / 2, 0.01f * i, new Vector3f())).rotateZ(rotation).scale(width * scale, height * scale, 1).translate(-0.5f, -0.5f, 0));
			vao.render();
		}
	}
}
