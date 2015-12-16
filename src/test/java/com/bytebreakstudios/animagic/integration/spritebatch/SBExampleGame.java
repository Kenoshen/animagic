package com.bytebreakstudios.animagic.integration.spritebatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.bytebreakstudios.animagic.texture.AnimagicSpriteBatch;
import com.bytebreakstudios.animagic.texture.AnimagicTextureRegion;
import com.bytebreakstudios.animagic.texture.data.AnimagicTextureData;

public class SBExampleGame extends Game {
    AnimagicSpriteBatch spriteBatch;
    AnimagicTextureRegion texture0;
    AnimagicTextureRegion texture1;
    Camera camera;

    boolean useNormals = true;
    boolean debugNormals = false;
    boolean useShadow = true;
    float normalIntensity = 1f;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.lookAt(0, 0, 0);
        spriteBatch = new AnimagicSpriteBatch(camera);

        texture0 = new AnimagicTextureRegion(new Texture("textures/bum.png"), new Texture("textures/bum_n.png"), new AnimagicTextureData(400, 200));
        texture1 = new AnimagicTextureRegion(new TextureRegion(new Texture("textures/bum_prime.png"), 100, 200, 400, 400),
                new TextureRegion(new Texture("textures/bum_prime_n.png"), 100, 0, 400, 400), new AnimagicTextureData(0, 200));
    }

    @Override
    public void render() {
        super.render();

        camera.update();
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        //Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            useNormals = !useNormals;
            spriteBatch.isShaderOn(useNormals);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            useShadow = !useShadow;
            spriteBatch.useShadow(useShadow);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            debugNormals = !debugNormals;
            spriteBatch.debugNormals(debugNormals);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            normalIntensity *= 1.01f;
            if (normalIntensity > 1) normalIntensity = 1;
            spriteBatch.normalIntensity(normalIntensity);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            normalIntensity *= 0.99f;
            if (normalIntensity < 0) normalIntensity = 0;
            spriteBatch.normalIntensity(normalIntensity);
        }


        spriteBatch.begin();

        spriteBatch.setAmbientColor(Color.PURPLE);
        spriteBatch.setAmbientIntensity(0.05f);
        spriteBatch.setNextLight(mousePos.x, mousePos.y, 0.1f, 0.3f, Color.RED);
        spriteBatch.setNextLight(-mousePos.x, -mousePos.y, 0.5f, 1, Color.GREEN);
        spriteBatch.setNextLight(-10, -10, 0.5f, 1, Color.BLUE);


        spriteBatch.draw(texture1, 0, 0, 400, 400);
        spriteBatch.draw(texture0, 0, 0, 400, 400);
        spriteBatch.end();
    }
}
