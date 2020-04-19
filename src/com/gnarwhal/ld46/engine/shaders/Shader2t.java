package com.gnarwhal.ld46.engine.shaders;

import org.joml.Vector2f;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class Shader2t extends Shader {

	private int subtextureLoc;

	public Shader2t() {
		super("res/shaders/s2t/vert.gls", "res/shaders/s2t/frag.gls");
		getUniforms();
	}

	@Override
	protected void getUniforms() {
		subtextureLoc = glGetUniformLocation(program, "subtexture");
	}

	public void setSubtexture(Vector2f position, Vector2f dimensions) {
		glUniform4f(subtextureLoc, position.x, position.y, dimensions.x, dimensions.y);
	}
}