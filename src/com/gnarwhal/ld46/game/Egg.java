package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.model.Rect;
import com.gnarwhal.ld46.engine.shaders.Shader;
import com.gnarwhal.ld46.engine.shaders.Shader2t;
import com.gnarwhal.ld46.engine.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static com.gnarwhal.ld46.game.Main.dtime;

public class Egg extends Rect {

	private static final float WIDTH  = 96;
	private static final float HEIGHT = 128;

	private static Texture texture;
	private static Shader2t shader;

	private Vector3f velocity;
	private boolean grounded;

	private float a;
	private float b;

	final float VERT_BOUNCE_DECAY = 0.25f;
	final float HIT_BOUNCE_DECAY  = 0.9f;

	private float vertBounceDecay;
	private float bounceChangeTime;

	public Egg(Camera camera) {
		super(camera, (camera.getWidth() - WIDTH) / 2, (camera.getHeight() - HEIGHT) / 2, -0.2f, WIDTH, HEIGHT, 0, false);

		velocity = new Vector3f(600, -600, 0);

		if (texture == null) {
			texture = new Texture("res/img/eggs.png");
			shader = Shader.SHADER2T;
		}

		a = (WIDTH + HEIGHT) / 4;
		b = (WIDTH - HEIGHT) / 4;

		vertBounceDecay = VERT_BOUNCE_DECAY;
	}

	public void update(Platform[] platforms) {
		final float GROUNDED_DECELERATION = 1760;
		final float TERMINAL_VELOCITY = 4096;
		final float GRAVITY = 2048;

		final float BOUNCE_CHANGE_DELAY = 0.05f;
		if (bounceChangeTime > BOUNCE_CHANGE_DELAY) {
			vertBounceDecay = VERT_BOUNCE_DECAY;
		}
		bounceChangeTime += dtime;

		float friction = 0;
		float floor = 0;
		Vector3f acceleration = new Vector3f(0, GRAVITY, 0);

		if (velocity.x != 0 && grounded) {
			friction = -GROUNDED_DECELERATION * (velocity.x / Math.abs(velocity.x));
		}

		float absVelocityY = Math.abs(velocity.y());
		if (absVelocityY > TERMINAL_VELOCITY) {
			velocity.y = (velocity.y / absVelocityY) * TERMINAL_VELOCITY;
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

		// --- C O L L I S I O N --- //

		Vector3f translation = velocity.mul((float) dtime, new Vector3f());
		Vector3f translationCopy = new Vector3f(translation);

		if (translation.y > 0) {
			Vector3f bottomLeft  = new Vector3f(position).add(0, height, 0);
			Vector3f bottomRight = new Vector3f(position).add(width, height, 0);
			for (int i = 0; i < platforms.length; ++i) {
				Vector3f porigin = platforms[i].getOrigin();
				Vector3f ptranslation = platforms[i].getTranslation();
				float s1, t1, s2, t2;
				if (translation.x != 0) {
					s1 = ((porigin.y - bottomLeft.y) / translation.y - (porigin.x - bottomLeft.x) / translation.x) / (ptranslation.x / translation.x - ptranslation.y / translation.y);
					s2 = ((porigin.y - bottomRight.y) / translation.y - (porigin.x - bottomRight.x) / translation.x) / (ptranslation.x / translation.x - ptranslation.y / translation.y);
				} else {
					s1 = (bottomLeft.x - porigin.x) / ptranslation.x;
					s2 = (bottomRight.x - porigin.x) / ptranslation.x;
				}
				t1 = (ptranslation.y * s1 + porigin.y - bottomLeft.y) / translation.y;
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

		final float HORZ_BOUNCE_DECAY = 0.75f;
		grounded = false;
		if (!translation.equals(translationCopy, 0)) {
			velocity.y *= -vertBounceDecay;
			grounded = true;
		}

		position.add(translation);

		// Check y bounds
		if (position.y + height > camera.getHeight()) {
			position.y = camera.getHeight() - height;
			velocity.y *= -vertBounceDecay;
			grounded = true;
		} else {
			if (position.y < 0) {
				position.y = 0;
				velocity.y *= -vertBounceDecay;
			}
		}
		// Check X bounds
		if (position.x < 0) {
			position.x = 0;
			velocity.x *= -HORZ_BOUNCE_DECAY;
		} else if (position.x + width > camera.getWidth()) {
			position.x = camera.getWidth() - width;
			velocity.x *= -HORZ_BOUNCE_DECAY;
		}

		// --- E N D   C O L L I S I O N --- //
	}

	private static final Vector2f SUBTEXTURE_DIMS = new Vector2f(0.2f, 1);

	public void render() {
		texture.bind();
		shader.enable();
		shader.setSubtexture(new Vector2f(0, 0), SUBTEXTURE_DIMS);
		shader.setMVP(camera.getMatrix().translate(position).scale(WIDTH * scale, HEIGHT * scale, 1));
		vao.render();
	}

	public void launch(Vector3f velocity) {
		this.velocity.set(velocity);
		vertBounceDecay = HIT_BOUNCE_DECAY;
		bounceChangeTime = 0;
	}

	public Vector3f getOrigin() {
		return position.add(width / 2, height / 2, 0, new Vector3f());
	}

	public float getRadius(float theta) {
		return a + b * (float) Math.cos(2 * theta);
	}
}
