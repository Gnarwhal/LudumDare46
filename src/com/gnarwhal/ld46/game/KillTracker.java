package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.model.Rect;
import com.gnarwhal.ld46.engine.shaders.Shader;
import com.gnarwhal.ld46.engine.shaders.Shader2t;
import com.gnarwhal.ld46.engine.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class KillTracker extends Rect {

	public static final float SEGMENT_WIDTH = 64;
	public static final float HEIGHT        = 64;

	private static Texture texture;
	private static Shader2t shader;

	private int killCount;

	public KillTracker(Camera camera, float x, float y) {
		super(camera, x, y, -0.5f, 0, HEIGHT, 0, false);

		if (texture == null) {
			texture = new Texture("res/img/numbers.png");
			shader  = Shader.SHADER2T;
		}
	}

	public void update() {}

	private static final Vector2f SUBTEXTURE_DIMS = new Vector2f(0.1f, 1);

	public void render() {
		texture.bind();
		shader.enable();
		int killCount = this.killCount;
		int offset = 0;
		while ((killCount /= 10) > 0) {
			offset += SEGMENT_WIDTH;
		}
		killCount = this.killCount;
		while (killCount > 0 || offset == 0) {
			int digit = killCount % 10;
			killCount /= 10;
			shader.setSubtexture(new Vector2f(digit * SUBTEXTURE_DIMS.x, 0), SUBTEXTURE_DIMS);
			shader.setMVP(camera.getMatrix().translate(position.add(offset, 0, 0, new Vector3f())).scale(SEGMENT_WIDTH, HEIGHT, 1));
			vao.render();

			offset -= SEGMENT_WIDTH;
		}
	}

	public void increment() {
		++killCount;
	}

	public Vector3f getOrigin() {
		return new Vector3f(position.x, position.y, 0);
	}

	public Vector3f getTranslation() {
		return new Vector3f(width, 0, 0);
	}
}
