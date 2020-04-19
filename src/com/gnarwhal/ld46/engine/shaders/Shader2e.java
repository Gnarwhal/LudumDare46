package com.gnarwhal.ld46.engine.shaders;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.*;

public class Shader2e extends Shader {

	private int subtextureLoc;
	private int colorLoc;
	private int timeLoc;

	public Shader2e() {
		super("res/shaders/s2e/vert.gls", "res/shaders/s2e/frag.gls");
		getUniforms();
	}

	@Override
	protected void getUniforms() {
		subtextureLoc = glGetUniformLocation(program, "subtexture");
		colorLoc      = glGetUniformLocation(program, "iColor");
		timeLoc       = glGetUniformLocation(program, "time");
	}

	public void setSubtexture(Vector2f position, Vector2f dimensions) {
		glUniform4f(subtextureLoc, position.x, position.y, dimensions.x, dimensions.y);
	}

	public void setColor(Vector3f color) {
		glUniform3f(colorLoc, color.x, color.y, color.z);
	}

	public void setTime(float time) {
		glUniform1f(timeLoc, time);
	}
}