package com.gnarwhal.ld46.engine.shaders;

import static org.lwjgl.opengl.GL20.*;

public class Shader2b extends Shader {

	private int offset0Loc;
	private int offset1Loc;

	protected Shader2b() {
		super("res/shaders/s2b/vert.gls", "res/shaders/s2b/frag.gls");
		getUniforms();
	}

	public void setOffset(float offset0, float offset1) {
		glUniform1f(offset0Loc, offset0);
		glUniform1f(offset1Loc, offset1);
	}

	@Override
	protected void getUniforms() {
		offset0Loc = glGetUniformLocation(program, "offset0");
		offset1Loc = glGetUniformLocation(program, "offset1");

		int layer0 = glGetUniformLocation(program, "layer0");
		int layer1 = glGetUniformLocation(program, "layer1");

		enable();
		glUniform1i(layer0, 0);
		glUniform1i(layer1, 1);
	}
}