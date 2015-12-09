package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.bytebreakstudios.animagic.texture.data.AnimagicAnimationData;
import com.bytebreakstudios.animagic.texture.data.AnimagicAtlasData;
import com.bytebreakstudios.animagic.texture.data.AnimagicTextureData;
import com.bytebreakstudios.animagic.utils.SerializationUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimagicTextureAtlas {
    private static final Pattern LAST_INT_PATTERN = Pattern.compile("[^0-9]+(_?[0-9]+)$");

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enableDefaultTyping();
    }

    private final TextureAtlas atlas;
    private AnimagicAtlasData meta;

    public AnimagicTextureAtlas(String internalPackFile) {
        atlas = new TextureAtlas(internalPackFile);
        findMetaFile(internalPackFile);
    }

    public AnimagicTextureAtlas(FileHandle packFile) {
        atlas = new TextureAtlas(packFile);
        findMetaFile(packFile);
    }

    public AnimagicTextureAtlas(FileHandle packFile, boolean flip) {
        atlas = new TextureAtlas(packFile, flip);
        findMetaFile(packFile);
    }

    public AnimagicTextureAtlas(FileHandle packFile, FileHandle imagesDir) {
        atlas = new TextureAtlas(packFile, imagesDir);
        findMetaFile(packFile);
    }

    public AnimagicTextureAtlas(FileHandle packFile, FileHandle imagesDir, boolean flip) {
        atlas = new TextureAtlas(packFile, imagesDir, flip);
        findMetaFile(packFile);
    }

    public Array<AnimagicTextureRegion> findRegions(String name) {
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(name);
        Array<AnimagicTextureRegion> animagicRegions = new Array<>();

        if (regions == null || regions.size <= 0) {
            AnimagicTextureRegion region = findRegion(name, 0);
            boolean zeroBased = false;
            if (region != null) {
                animagicRegions.add(region);
                zeroBased = true;
            }
            int i = 1;
            while (true) {
                region = findRegion(name, i, zeroBased);
                if (region == null) {
                    break;
                } else {
                    animagicRegions.add(region);
                }
                i++;
            }
            return animagicRegions;
        } else {
            for (int i = 0; i < regions.size; i++) {
                animagicRegions.add(new AnimagicTextureRegion(regions.get(i), new Texture(0, 0, Pixmap.Format.RGBA8888), getMeta(name, i)));
            }
            return animagicRegions;
        }
    }

    public AnimagicTextureRegion findRegion(String name) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(name);
        if (region != null)
            return new AnimagicTextureRegion(region, new Texture(0, 0, Pixmap.Format.RGBA8888), getMeta(name, 0));
        else return null;
    }

    public AnimagicTextureRegion findRegion(String name, int index) {
        return findRegion(name, index, true);
    }

    public AnimagicTextureRegion findRegion(String name, int index, boolean zeroBased) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(name, index);
        if (region == null) {
            region = atlas.findRegion(name + "/" + index);
            if (region == null) {
                region = atlas.findRegion(name + "/" + (index < 10 ? "0" + index : index));
                if (region == null) return null;
            }
        }
        return new AnimagicTextureRegion(region, new Texture(0, 0, Pixmap.Format.RGBA8888), getMeta(name, (zeroBased ? index : index - 1)));
    }

    private void findMetaFile(FileHandle atlasFile) {
        FileHandle packedMeta = Gdx.files.internal(atlasFile.pathWithoutExtension() + ".meta");
        if (packedMeta.exists() && !packedMeta.isDirectory()) {
            meta = SerializationUtils.fromJson(AnimagicAtlasData.class, packedMeta.readString());
        }
        if (meta == null) System.err.println("Could not find: " + packedMeta);
    }

    private void findMetaFile(String atlasLocation) {
        findMetaFile(Gdx.files.internal(atlasLocation));
    }

    private AnimagicTextureData getMeta(String name, int frameIndex) {
        if (meta != null) {
            String nameWithNoIndex = trimTrailingNumber(name);
            AnimagicAnimationData animData = meta.get(nameWithNoIndex);
            if (animData != null) {
                return animData.get(frameIndex);
            }
        }
        return new AnimagicTextureData(0, 0);
    }

    private String trimTrailingNumber(String text) {
        Matcher matcher = LAST_INT_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return text;
    }
}
