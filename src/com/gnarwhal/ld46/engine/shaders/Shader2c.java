package com.gnarwhal.ld46.engine.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class Shader2c extends Shader {

	private int colorLoc;
	
	protected Shader2c(String vert, String frag) {
		super(vert, frag);
		getUniforms();
	}
	
	protected Shader2c() {
		super("res/shaders/s2c/vert.gls", "res/shaders/s2c/frag.gls");
		getUniforms();
	}
	
	@Override
	protected void getUniforms() {
		colorLoc = glGetUniformLocation(program, "iColor");
	}
	
	public void setColor(float r, float g, float b, float a) {
		glUniform4f(colorLoc, r, g, b, a);
	}
}
