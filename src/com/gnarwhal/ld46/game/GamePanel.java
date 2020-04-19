package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.display.Window;

public class GamePanel {

	private Window window;
	private Camera camera;

	private Player player;

	private Platform[] platforms;

	public GamePanel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;

		player = new Player(window, camera);

		platforms = new Platform[] {
			new Platform(camera, 800, 800, 320)
		};
	}
	
	public void update() {
		player.update(platforms);
	}
	
	public void render() {
		for (int i = 0; i < platforms.length; ++i) {
			platforms[i].render();
		}
		player.render();
	}
}
