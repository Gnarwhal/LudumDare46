package com.gnarwhal.ld46.game;

import com.gnarwhal.ld46.engine.audio.ALManagement;
import com.gnarwhal.ld46.engine.audio.Sound;
import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.display.Window;
import com.gnarwhal.ld46.engine.shaders.Shader;

public class Main {

	public static int fps;
	public static double dtime;
	
	private ALManagement al;
	
	private Window window;
	private Camera camera;
	
	private GamePanel panel;

	public void start() {
		init();
		int frames = 0;
		long curTime, pastTime, pastSec, nspf = 1000000000 / Window.REFRESH_RATE;
		pastTime = System.nanoTime();
		pastSec = pastTime;
		while(!window.shouldClose()) {
			curTime = System.nanoTime();
			if (curTime - pastTime > nspf) {
				dtime = nspf / 1000000000d;
				update();
				render();
				pastTime += nspf;
				++frames;
			}
			if (curTime - pastSec > 1000000000) {
				fps = frames;
				frames = 0;
				pastSec += 1000000000;
			}
			if (nspf - curTime + pastTime > 10000000) try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		al.destroy();
		Window.terminate();
	}

	private void init() {
		al = new ALManagement();

		final int WIN_WIDTH = 768, WIN_HEIGHT = 432;
		window = new Window("Ludum Dare 46", true);
		//window = new Window(WIN_WIDTH, WIN_HEIGHT, "Ludum Dare 46", true, true, true);
		camera = new Camera(WIN_WIDTH, WIN_HEIGHT);
		Shader.init();

		panel = new GamePanel(window, camera);
	}
	
	private void update() {
		window.update();
		panel.update();
		camera.update();
	}
	
	private void render() {
		window.clear();
		panel.render();
		window.swap();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}
