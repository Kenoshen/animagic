package com.bytebreakstudios.animagic.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class AnimagicSpriteBatch extends SpriteBatch {

    public static final int MAX_LIGHTS = 10;
    private static final Color BLACK = Color.BLACK.cpy();
    private final Light[] lights;

    private Camera camera;

    private boolean isShaderOn = true;
    private boolean useShadow = true;
    private boolean debugNormals = false;
    private float normalIntensity = 1;

    public AnimagicSpriteBatch(Camera camera) {
        super();
        this.setCamera(camera);
        this.setShader(createShader());

        lights = new Light[]{new Light(0), new Light(1), new Light(2), new Light(3), new Light(4), new Light(5), new Light(6), new Light(7), new Light(8), new Light(9)};
        if (lights.length != MAX_LIGHTS)
            throw new RuntimeException("The array of light objects cannot differ in length from the MAX_LIGHTS constant");
    }

    public AnimagicSpriteBatch(){
        this(null);
    }


    public AnimagicSpriteBatch setCamera(Camera camera) {
        //if (camera == null) throw new RuntimeException("Cannot set the AnimagicSpriteBatch.camera with a null camera");
        this.camera = camera;
        return this;
    }

    public Camera getCamera() {
        return camera;
    }


    private ShaderProgram createShader() {
        FileHandle vertex = Gdx.files.internal("vertex.glsl");
        FileHandle fragment = Gdx.files.internal("fragment.glsl");
        ShaderProgram program = new ShaderProgram(vertex, fragment);
        // u_proj and u_trans will not be active but SpriteBatch will still try to set them...
        ShaderProgram.pedantic = false;
        if (!program.isCompiled())
            throw new RuntimeException("Could not compile Animagic shader: "
                    + program.getLog());

        // we are only using this many uniforms for testing purposes...!!
        program.begin();
        program.setUniformi("u_texture", 0);
        program.setUniformi("u_normals", 1);
        program.setUniformf("strength", 1f);
        program.setUniformf("ambientIntensity", 0f);
        program.setUniformf("ambientColor", new Vector3(0, 0, 0));
        program.setUniformi("useShadow", 1); // true
        program.setUniformi("useNormals", 1); // true
        program.setUniformi("yInvert", 0); // false
        program.end();

        return program;
    }

    public AnimagicSpriteBatch isShaderOn(boolean isShaderOn) {
        this.isShaderOn = isShaderOn;
        getShader().begin();
        if (isShaderOn) {
            getShader().setUniformi("useNormals", 1);
        } else {
            getShader().setUniformi("useNormals", 0);
        }
        getShader().end();
        return this;
    }


    public boolean isShaderOn() {
        return isShaderOn;
    }


    public AnimagicSpriteBatch debugNormals(boolean debugNormals) {
        this.debugNormals = debugNormals;
        getShader().begin();
        if (debugNormals) {
            getShader().setUniformi("drawNormals", 1);
        } else {
            getShader().setUniformi("drawNormals", 0);
        }
        getShader().end();
        return this;
    }

    public boolean debugNormals() {
        return debugNormals;
    }

    public AnimagicSpriteBatch useShadow(boolean useShadow) {
        this.useShadow = useShadow;
        getShader().begin();
        if (useShadow) {
            getShader().setUniformi("useShadow", 1);
        } else {
            getShader().setUniformi("useShadow", 0);
        }
        getShader().end();
        return this;
    }

    public boolean useShadow() {
        return useShadow;
    }

    public AnimagicSpriteBatch normalIntensity(float normalIntensity) {
        this.normalIntensity = Math.min(Math.max(normalIntensity, 0), 1);
        getShader().begin();
        getShader().setUniformf("strength", this.normalIntensity);
        getShader().end();
        return this;
    }

    public float normalIntensity() {
        return normalIntensity;
    }

    public AnimagicSpriteBatch setAmbientColor(Color ambientColor){
        if (ambientColor == null) throw new RuntimeException("Ambient color cannot be null");
        getShader().setUniformf("ambientColor", new Vector3(ambientColor.r, ambientColor.g, ambientColor.b));
        return this;
    }


    public AnimagicSpriteBatch setAmbientIntensity(float ambientIntensity) {
        getShader().setUniformf("ambientIntensity", Math.min(1, Math.max(0, ambientIntensity)));
        return this;
    }


    public AnimagicSpriteBatch setLight(int index, float x, float y, float z, float attenuation, Color color) {
        if (index < 0) throw new RuntimeException("Light index cannot be less than 0");
        else if (index >= MAX_LIGHTS) throw new RuntimeException("Light index cannot be greater or equal to " + MAX_LIGHTS);
        else if (color == null) throw new RuntimeException("Light[" + index + "] color cannot be null");
        Light l = lights[index];
        l.position(x, y, z);
        l.attenuation(attenuation);
        l.color(color);
        return this;
    }

    public AnimagicSpriteBatch setNextLight(float x, float y, float z, float attenuation, Color color) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (!lights[i].isDirty()) return setLight(i, x, y, z, attenuation, color);
        }
        return this;
    }


    private void resetLights() {
        for (int i = 0; i < MAX_LIGHTS; i++){
            if (lights[i].isDirty()) {
                setLight(i, 0, 0, 0, 0, BLACK);
                lights[i].resetDirty();
            }
        }
    }

    @Override
    public void begin() {
        if (camera != null) setProjectionMatrix(camera.combined);
        super.begin();
        resetLights();
    }


    private Vector2 preDraw(TextureRegion region, float x, float y, float width, float height) {
        Vector2 pos = new Vector2(x, y);
        if (region instanceof AnimagicTextureRegion) {
            AnimagicTextureRegion aRegion = (AnimagicTextureRegion) region;
            if (aRegion.getNormalTextureRegion() != null) {
                aRegion.getNormalTextureRegion().getTexture().bind(1);
            } else {
                AnimagicTextureRegion.FLAT_NORMAL_MAP.bind(1);
            }
            region.getTexture().bind(0);
            ShaderProgram p = getShader();
            p.setUniformf("xCoordMin", aRegion.shaderData.xCoordMin);
            p.setUniformf("xCoordDiff", aRegion.shaderData.xCoordDiff);
            p.setUniformf("yCoordMin", aRegion.shaderData.yCoordMin);
            p.setUniformf("yCoordDiff", aRegion.shaderData.yCoordDiff);
            p.setUniformf("xNorCoordMin", aRegion.shaderData.xNorCoordMin);
            p.setUniformf("xNorCoordDiff", aRegion.shaderData.xNorCoordDiff);
            p.setUniformf("yNorCoordMin", aRegion.shaderData.yNorCoordMin);
            p.setUniformf("yNorCoordDiff", aRegion.shaderData.yNorCoordDiff);
            pos = pos.sub(width * aRegion.getOriginPercentageX(), height * aRegion.getOriginPercentageY());
        }
        return pos;
    }

    private void preDraw(Texture texture) {
        AnimagicTextureRegion.FLAT_NORMAL_MAP.bind(1);
        texture.bind(0);
    }

    @Override
    public void draw(TextureRegion region, float x, float y) {
        Vector2 pos = preDraw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
        super.draw(region, pos.x, pos.y);
        flush();
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float width, float height) {
        Vector2 pos = preDraw(region, x, y, width, height);
        super.draw(region, pos.x, pos.y, width, height);
        flush();
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        Vector2 pos = preDraw(region, x, y, width, height);
        super.draw(region, pos.x, pos.y, originX, originY, width, height, scaleX, scaleY, rotation);
        flush();
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        Vector2 pos = preDraw(region, x, y, width, height);
        super.draw(region, pos.x, pos.y, originX, originY, width, height, scaleX, scaleY, rotation, clockwise);
        flush();
    }

    @Override
    public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        preDraw(region.getTexture());
        super.draw(region, width, height, transform);
    }

    @Override
    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        preDraw(texture);
        super.draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        preDraw(texture);
        super.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }

    @Override
    public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        preDraw(texture);
        super.draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        preDraw(texture);
        super.draw(texture, x, y, width, height, u, v, u2, v2);
    }

    @Override
    public void draw(Texture texture, float x, float y) {
        preDraw(texture);
        super.draw(texture, x, y);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        preDraw(texture);
        super.draw(texture, x, y, width, height);
    }

    @Override
    public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        preDraw(texture);
        super.draw(texture, spriteVertices, offset, count);
    }

    public class Light {
        private float x = 0;
        private float y = 0;
        private float z = 0;
        private float attenuation = 0;
        private float r = 0;
        private float g = 0;
        private float b = 0;

        private boolean dirty = false;
        public final int index;

        protected Light(int index) {
            this.index = index;
        }

        public Light position(float x, float y, float z) {
            if (this.x != x || this.y != y || this.z != z) {
                Vector3 p = new Vector3(x, y, z);
                Vector3 pScreen = camera.project(p);
                // TODO: Why does this work, you ask?
                pScreen.x -= Gdx.graphics.getWidth() / 2;
                pScreen.x /= Gdx.graphics.getWidth() / 2;
                pScreen.y -= Gdx.graphics.getHeight() / 2;
                pScreen.y /= Gdx.graphics.getHeight() / 2;
                // TODO: I have no idea...
                pScreen.z = z;
                getShader().setUniformf("light" + index, pScreen);

                isDirty(true);
            }
            return this;
        }

        public Light attenuation(float attenuation) {
            if (this.attenuation != attenuation) {
                this.attenuation = attenuation;
                getShader().setUniformf("attenuation" + index, this.attenuation);

                isDirty(true);
            }
            return this;
        }

        public Light color(float r, float g, float b) {
            if (this.r != r || this.g != g || this.b != b) {
                this.r = r;
                this.g = g;
                this.b = b;
                getShader().setUniformf("lightColor" + index, new Vector3(this.r, this.g, this.b));

                isDirty(true);
            }
            return this;
        }

        public Light color(Color color) {
            return color(color.r, color.g, color.b);
        }

        public boolean isDirty() {
            return dirty;
        }

        private void isDirty(boolean isDirty){
            dirty = isDirty;
            getShader().setUniformi("isLight" + index, isDirty ? 1 : 0);
        }

        public Light resetDirty() {
            isDirty(false);
            return this;
        }

    }
}
