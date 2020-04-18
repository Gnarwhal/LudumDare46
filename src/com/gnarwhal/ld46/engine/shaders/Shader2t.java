package com.gnarwhal.ld46.engine.shaders;

public class Shader2t extends Shader {
	
	protected Shader2t() {
		super("res/shaders/s2t/vert.gls", "res/shaders/s2t/frag.gls");
		getUniforms();
	}

	@Override
	protected void getUniforms() {}
}