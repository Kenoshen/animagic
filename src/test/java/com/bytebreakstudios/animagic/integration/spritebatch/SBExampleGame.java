package com.bytebreakstudios.animagic.integration.spritebatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.lookAt(0, 0, 0);
        spriteBatch = new AnimagicSpriteBatch(camera);

        texture0 = new AnimagicTextureRegion(new Texture("textures/bum.png"), new Texture("textures/bum_n.png"), new AnimagicTextureData(200, 200));
        texture1 = new AnimagicTextureRegion(new TextureRegion(new Texture("textures/bum_prime.png"), 100, 200, 400, 400),
                new TextureRegion(new Texture("textures/bum_prime_n.png"), 100, 0, 400, 400), new AnimagicTextureData(200, 200));
    }

    @Override
    public void render() {
        super.render();

        camera.update();
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        spriteBatch.begin();

        spriteBatch.setAmbientColor(Color.WHITE);
        spriteBatch.setAmbientIntensity(0.01f);
        spriteBatch.setNextLight(mousePos.x, mousePos.y, 0.2f, 0.6f, Color.WHITE);
        //spriteBatchA.setNextLight(-mousePos.x, -mousePos.y, 0.5f, 1, Color.GREEN);


        spriteBatch.draw(texture1, 0, -200, 400, 400);
        spriteBatch.draw(texture0, -400, -200, 400, 400);
        spriteBatch.end();
    }
}
