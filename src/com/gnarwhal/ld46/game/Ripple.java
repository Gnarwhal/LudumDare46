package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.model.Rect;

public class Ripple extends Rect {

	public Ripple(Camera camera, float x, float y, float r, float g, float b, float a) {
		super(camera, x, y, -0.3f, 0, 0, 0, false);


	}
}
