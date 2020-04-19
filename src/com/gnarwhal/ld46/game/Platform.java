package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.model.Rect;
import com.gnarwhal.ld46.engine.shaders.Shader;
import com.gnarwhal.ld46.engine.shaders.Shader2t;
import com.gnarwhal.ld46.engine.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Platform extends Rect {

	public static final float SEGMENT_WIDTH = 96;
	public static final float HEIGHT        = 96;

	private static Texture texture;
	private static Shader2t shader;

	private int segmentSelection[];

	public Platform(Camera camera, float x, float y, int internalSegments) {
		super(camera, x, y, -0.5f, (internalSegments + 2) * SEGMENT_WIDTH, HEIGHT, 0, false);

		segmentSelection = new int[internalSegments + 2];
		segmentSelection[                   0] = 0;
		segmentSelection[internalSegments + 1] = 4;
		if (segmentSelection.length > 2) {
			segmentSelection[1] = (int) (Math.random() * 3);
			for (int i = 2; i < internalSegments + 1; ++i) {
				segmentSelection[i] = segmentSelection[i - 1] + (int) (Math.random() * 2) + 1;
			}
			for (int i = 1; i < internalSegments + 1; ++i) {
				segmentSelection[i] = segmentSelection[i] % 3 + 1;
			}
		}

		if (texture == null) {
			texture = new Texture("res/img/platform.png");
			shader  = Shader.SHADER2T;
		}
	}

	public void update() {}

	private static final Vector2f SUBTEXTURE_DIMS = new Vector2f(0.2f, 1);

	public void render() {
		texture.bind();
		shader.enable();
		for (int i = 0; i < segmentSelection.length; ++i) {
			shader.setSubtexture(new Vector2f(segmentSelection[i] * SUBTEXTURE_DIMS.x, 0), SUBTEXTURE_DIMS);
			shader.setMVP(camera.getMatrix().translate(position.add(SEGMENT_WIDTH / 2 + SEGMENT_WIDTH * i, height / 2, 0, new Vector3f())).scale(SEGMENT_WIDTH, HEIGHT, 1).translate(-0.5f, -0.5f, 0));
			vao.render();
		}
	}

	public Vector3f getOrigin() {
		return new Vector3f(position.x, position.y, 0);
	}

	public Vector3f getTranslation() {
		return new Vector3f(width, 0, 0);
	}
}
