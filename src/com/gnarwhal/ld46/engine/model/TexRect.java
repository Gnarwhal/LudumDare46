package com.gnarwhal.ld46.engine.model;

import com.gnarwhal.ld46.engine.display.Camera;
import com.gnarwhal.ld46.engine.shaders.Shader;
import com.gnarwhal.ld46.engine.shaders.Shader2t;
import com.gnarwhal.ld46.engine.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TexRect extends Rect {

	private Texture texture;
	private Shader2t shader = Shader.SHADER2T;
	protected float direction = 1;
	
	public TexRect(Camera camera, String path, float x, float y, float z, float width, float height, float rotation, boolean gui) {
		super(camera, x, y, z, width, height, rotation, gui);
		texture = new Texture(path);
	}
	
	public TexRect(Camera camera, Texture texture, float x, float y, float z, float width, float height, float rotation, boolean gui) {
		super(camera, x, y, z, width, height, rotation, gui);
		this.texture = texture;
	}

	private final Vector2f offset = new Vector2f(0, 0);
	private final Vector2f sub    = new Vector2f(1, 1);

	public void render() {
		texture.bind();
		shader.enable();
		shader.setSubtexture(offset, sub);
		Matrix4f cmat = gui ? camera.getProjection() : camera.getMatrix();
		shader.setMVP(cmat.translate(position.add(width * scale / 2, height * scale / 2, 0, new Vector3f())).rotateZ(rotation).scale(width * scale * direction, height * scale, 1).translate(-0.5f, -0.5f, 0));
		vao.render();
		shader.disable();
		texture.unbind();
	}
	
	public void setCenter(float x, float y) {
		position.x = x - width  / 2;
		position.y = y - height / 2;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
