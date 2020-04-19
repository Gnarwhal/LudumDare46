package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.model.ColRect;
import org.joml.Vector3f;

public class Platform {

	private Camera camera;
	private ColRect rect;

	public Platform(Camera camera, float x, float y, float width) {
		this.camera = camera;
		this.rect = new ColRect(camera, x, y, -0.05f, width, 12, 1, 1, 1, 1, false);
	}

	public void update() {

	}

	public void render() {
		rect.render();
	}


	public Vector3f getOrigin() {
		return new Vector3f(rect.getX(), rect.getY(), 0);
	}

	public Vector3f getTranslation() {
		return new Vector3f(rect.getWidth(), 0, 0);
	}
}
