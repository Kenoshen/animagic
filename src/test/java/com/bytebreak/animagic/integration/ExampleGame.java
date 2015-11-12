package com.bytebreak.animagic.integration;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bytebreak.animagic.BitTextureAtlas;
import com.bytebreak.animagic.BitTextureAtlasLoader;

public class ExampleGame extends Game {
    AssetManager assetManager;
    SpriteBatch spriteBatch;

    Character character;

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.setLoader(BitTextureAtlas.class, new BitTextureAtlasLoader(new InternalFileHandleResolver()));
        assetManager.load("packed/character.atlas", BitTextureAtlas.class);
        assetManager.finishLoading();

        character = new Character(assetManager.get("packed/character.atlas", BitTextureAtlas.class));

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render() {
        super.render();

        character.update(1f / 60f);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        character.draw(spriteBatch);
        spriteBatch.end();
    }
}
