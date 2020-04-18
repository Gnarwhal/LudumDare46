package com.gnarwhal.ld46.engine.model;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.shaders.Shader;
import com.gnarwhal.ld46.engine.shaders.Shader2c;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ColRect extends Rect {
	
	private Shader2c shader;
	
	protected float r, g, b, a;
	
	public ColRect(Camera camera, float x, float y, float z, float width, float height, float r, float g, float b, float a, boolean gui) {
		super(camera, x, y, z, width, height, 0, gui);
		shader = Shader.SHADER2C;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void render() {
		shader.enable();
		shader.setColor(r, g, b, a);
		Matrix4f cmat = gui ? camera.getProjection() : camera.getMatrix();
		shader.setMVP(cmat.translate(position.add(width * scale / 2, height * scale / 2, 0, new Vector3f())).rotateZ(rotation * 3.1415927f / 180).scale(width * scale, height * scale, 1).translate(-0.5f, -0.5f, 0));
		vao.render();
		shader.disable();
	}
	
	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void setAlpha(float alpha) {
		a = alpha;
	}
}
