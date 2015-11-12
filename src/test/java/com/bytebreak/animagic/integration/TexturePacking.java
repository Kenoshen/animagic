package com.bytebreak.animagic.integration;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePacking {
    public static void main(String[] arg) {
        System.out.println("Start packing textures");
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth *= 4;
        settings.maxHeight *= 4;
        settings.combineSubdirectories = true;
        settings.duplicatePadding = true;
        settings.fast = true;
        settings.square = true;
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;
        //
        TexturePacker.process(settings, "src/test/resources/sprites", "src/test/resources/packed", "character");
        System.out.println("Finished packing textures");
    }
}
