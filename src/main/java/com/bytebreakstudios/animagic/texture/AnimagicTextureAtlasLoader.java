package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class AnimagicTextureAtlasLoader extends SynchronousAssetLoader<AnimagicTextureAtlas, AnimagicTextureAtlasLoader.AnimagicTextureAtlasParameter> {
    TextureAtlasData data;

    public AnimagicTextureAtlasLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public AnimagicTextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, AnimagicTextureAtlasParameter parameter) {
//        Page page;
//        Texture texture;
//        for(Iterator var5 = this.data.getPages().iterator(); var5.hasNext(); page.texture = texture) {
//            page = (Page)var5.next();
//            texture = assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
//        }

        return new AnimagicTextureAtlas(file);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle atlasFile, AnimagicTextureAtlasParameter parameter) {
        FileHandle imgDir = atlasFile.parent();
        if(parameter != null) {
            this.data = new TextureAtlasData(atlasFile, imgDir, parameter.flip);
        } else {
            this.data = new TextureAtlasData(atlasFile, imgDir, false);
        }

        Array dependencies = new Array();
        Iterator var6 = this.data.getPages().iterator();

        while(var6.hasNext()) {
            Page page = (Page)var6.next();
            TextureParameter params = new TextureParameter();
            params.format = page.format;
            params.genMipMaps = page.useMipMaps;
            params.minFilter = page.minFilter;
            params.magFilter = page.magFilter;
            dependencies.add(new AssetDescriptor(page.textureFile, Texture.class, params));
        }

        return dependencies;
    }

    public static class AnimagicTextureAtlasParameter extends AssetLoaderParameters<AnimagicTextureAtlas> {
        public boolean flip = false;

        public AnimagicTextureAtlasParameter() {
        }

        public AnimagicTextureAtlasParameter(boolean flip) {
            this.flip = flip;
        }
    }
}
