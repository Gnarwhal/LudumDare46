package com.gnarwhal.ld46.engine.texture;

public class TextureSet {

	private Texture[] textures;

	public TextureSet(String[] paths) {
		textures = new Texture[paths.length];
		for (int i = 0; i < textures.length; ++i)
			textures[i] = new Texture(paths[i]);
	}

	public int length() {
		return textures.length;
	}

	public Texture get(int index) {
		return textures[index];
	}
}
