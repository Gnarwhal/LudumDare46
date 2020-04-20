package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.display.Window;
import com.gnarwhal.ld46.engine.model.TexRect;

public class GamePanel {

	private Window window;
	private Camera camera;

	private Player player;
	private Egg egg;

	private Platform[] platforms;

	private TexRect killsText;
	private KillTracker killCount;

	public GamePanel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;

		player = new Player(window, camera);
		egg = new Egg(camera);

		platforms = new Platform[] {
			new Platform(camera, (camera.getWidth()     / 4 - Platform.SEGMENT_WIDTH * 4 / 2), camera.getHeight() * 2 / 5 - Platform.HEIGHT / 2, 2),
			new Platform(camera, (camera.getWidth() * 3 / 4 - Platform.SEGMENT_WIDTH * 4 / 2), camera.getHeight() * 2 / 5 - Platform.HEIGHT / 2, 2),
			new Platform(camera, (camera.getWidth()     / 2 - Platform.SEGMENT_WIDTH * 5 / 2), camera.getHeight() * 3 / 4 - Platform.HEIGHT / 2, 3)
		};

		killsText = new TexRect(camera, "res/img/kills.png", 0, 0, 0, 64 * 6, 64, 0, false);
		killCount = new KillTracker(camera, 64 * 6, 0);
	}
	
	public void update() {
		egg.update(platforms);
		if (egg.state() == Egg.DEAD) {
			egg.reset();
			killCount.increment();
		}
		player.update(platforms, egg);
	}
	
	public void render() {
		for (int i = 0; i < platforms.length; ++i) {
			platforms[i].render();
		}
		egg.render();
		player.render();
		killsText.render();
		killCount.render();
	}
}
