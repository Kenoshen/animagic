package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.bytebreakstudios.animagic.texture.data.AnimagicTextureData;
import com.bytebreakstudios.animagic.utils.AnimagicException;

public class AnimagicTextureRegion extends TextureRegion {
    public static final Texture FLAT_NORMAL_MAP;

    static {
        Pixmap p = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        p.setColor(new Color(0.5f, 0.5f, 1.0f, 1.0f));
        p.fill();
        FLAT_NORMAL_MAP = new Texture(p);
        p.dispose();
    }

    private final AnimagicTextureData meta;
    private final TextureRegion normals;
    public final AnimagicTextureRegionShaderData shaderData;

    private AnimagicTextureRegion(Texture texture, Texture normals, TextureRegion textureRegion, TextureRegion normalRegion, AnimagicTextureData meta) {
        super();
        if (textureRegion == null) {
            if (texture == null)
                throw new AnimagicException("Cannot create an AnimagicTextureRegion with a null texture");
            this.setTexture(texture);
            this.setRegion(0, 0, texture.getWidth(), texture.getHeight());
        } else this.setRegion(textureRegion);
        if (normalRegion == null) {
            if (normals == null) this.normals = null;
            else
                this.normals = new TextureRegion(normals, this.getRegionX(), this.getRegionY(), this.getRegionWidth(), this.getRegionHeight());
        } else this.normals = new TextureRegion(normalRegion);

        this.meta = new AnimagicTextureData(meta);
        this.shaderData = new AnimagicTextureRegionShaderData(this);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals) {
        this(texture, normals, null, null, null);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, AnimagicTextureData meta) {
        this(texture, normals, null, null, meta);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height) {
        this(texture, normals, new TextureRegion(texture, x, y, width, height), null, null);
    }

    public AnimagicTextureRegion(Texture texture, Texture normals, int x, int y, int width, int height, AnimagicTextureData meta) {
        this(texture, normals, new TextureRegion(texture, x, y, width, height), null, meta);
    }

    public AnimagicTextureRegion(TextureRegion texture, TextureRegion normals) {
        this(null, null, texture, normals, null);
    }

    public AnimagicTextureRegion(TextureRegion texture, TextureRegion normals, AnimagicTextureData meta) {
        this(null, null, texture, normals, meta);
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region) {
        this(null, null, region, region.getNormalTextureRegion(), region.meta());
    }

    public AnimagicTextureRegion(AnimagicTextureRegion region, AnimagicTextureData meta) {
        this(null, null, region, region.getNormalTextureRegion(), meta);
    }

    public TextureRegion getNormalTextureRegion() {
        return normals;
    }


    public Vector2 getOriginPercentage() {
        return new Vector2(getOriginPercentageX(), getOriginPercentageY());
    }


    public float getOriginPercentageX() {
        int w = getRegionWidth();
        if (w == 0) return 0;
        else return (float) meta.originX / (float) w;
    }


    public float getOriginPercentageY() {
        int h = getRegionHeight();
        if (h == 0) return 0;
        else return (float) meta.originY / (float) h;
    }

    public Vector2 getRegionOrigin() {
        return new Vector2(getRegionOriginX(), getRegionOriginY());
    }


    public int getRegionOriginX() {
        return meta.originX;
    }


    public int getRegionOriginY() {
        return meta.originY;
    }

    public AnimagicTextureData meta() {
        return meta;
    }

    public static final class AnimagicTextureRegionShaderData {
        public final float xCoordMin;
        public final float xCoordDiff;
        public final float yCoordMin;
        public final float yCoordDiff;
        public final float xNorCoordMin;
        public final float xNorCoordDiff;
        public final float yNorCoordMin;
        public final float yNorCoordDiff;

        public AnimagicTextureRegionShaderData(AnimagicTextureRegion region) {
            float width = region.getTexture().getWidth();
            float height = region.getTexture().getHeight();
            float x = region.getRegionX();
            float y = region.getRegionY();
            float w = region.getRegionWidth();
            float h = region.getRegionHeight();
            float width_n = 0;
            float height_n = 0;
            float x_n = 0;
            float y_n = 0;
            float w_n = 0;
            float h_n = 0;
            if (region.getNormalTextureRegion() != null) {
                width_n = region.getNormalTextureRegion().getTexture().getWidth();
                height_n = region.getNormalTextureRegion().getTexture().getHeight();
                x_n = region.getNormalTextureRegion().getRegionX();
                y_n = region.getNormalTextureRegion().getRegionY();
                w_n = region.getNormalTextureRegion().getRegionWidth();
                h_n = region.getNormalTextureRegion().getRegionHeight();
            }

            xCoordMin = x / width;
            float xCoordMax = (x + w) / width;
            xCoordDiff = xCoordMax - xCoordMin;

            yCoordMin = y / height;
            float yCoordMax = (y + h) / height;
            yCoordDiff = yCoordMax - yCoordMin;

            xNorCoordMin = x_n / width_n;
            float xNorCoordMax = (x_n + w_n) / width_n;
            xNorCoordDiff = xNorCoordMax - xNorCoordMin;

            yNorCoordMin = y_n / height_n;
            float yNorCoordMax = (y_n + h_n) / height_n;
            yNorCoordDiff = yNorCoordMax - yNorCoordMin;
        }

        public AnimagicTextureRegionShaderData(float xCoordMin, float xCoordDiff, float yCoordMin, float yCoordDiff, float xNorCoordMin, float xNorCoordDiff, float yNorCoordMin, float yNorCoordDiff) {
            this.xCoordMin = xCoordMin;
            this.xCoordDiff = xCoordDiff;
            this.yCoordMin = yCoordMin;
            this.yCoordDiff = yCoordDiff;
            this.xNorCoordMin = xNorCoordMin;
            this.xNorCoordDiff = xNorCoordDiff;
            this.yNorCoordMin = yNorCoordMin;
            this.yNorCoordDiff = yNorCoordDiff;
        }
    }
}

