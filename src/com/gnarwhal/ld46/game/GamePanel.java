package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.display.Window;

public class GamePanel {

	private Window window;
	private Camera camera;

	private Player player;

	public GamePanel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;

		player = new Player(window, camera);
	}
	
	public void update() {
		player.update();
	}
	
	public void render() {
		player.render();
	}
}
