package com.bytebreakstudios.animagic.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bytebreakstudios.animagic.utils.AnimagicException;

import java.util.ArrayList;
import java.util.List;

public class SingleFrame implements IFrameByFrameAnimation {
    private String name;
    private List<AnimationListener> listeners = new ArrayList<>();
    private TextureRegion textureRegion;
    private float totalFrameRateDuration;
    private float perFrameRateDuration;
    private float currentDuration = 0;
    private boolean finishedPlaying = false;

    public SingleFrame(String name, FrameRate frameRate, TextureRegion textureRegion) {
        if (name == null) throw new AnimagicException("SingleFrame.name cannot be null");
        if (name.trim().equalsIgnoreCase("")) throw new AnimagicException("SingleFrame.name cannot be ''");
        if (frameRate == null) throw new AnimagicException("SingleFrame.frameRate cannot be null");
        if (textureRegion == null) throw new AnimagicException("SingleFrame.textureRegion cannot be null");

        this.name = name;
        this.textureRegion = textureRegion;

        setFrameRate(frameRate);
    }

    public SingleFrame(String name, TextureRegion textureRegion) {
        this(name, FrameRate.total(0), textureRegion);
    }

    public String name() {
        return name;
    }

    public float totalDuration() {
        return totalFrameRateDuration;
    }

    public float perFrameDuration() {
        return perFrameRateDuration;
    }

    public SingleFrame setFrameRate(FrameRate frameRate) {
        if (frameRate.total()) {
            this.totalFrameRateDuration = frameRate.seconds();
            this.perFrameRateDuration = frameRate.seconds();
        } else {
            this.totalFrameRateDuration = frameRate.seconds();
            this.perFrameRateDuration = frameRate.seconds();
        }
        return this;
    }

    @Override
    public void reset() {
        currentDuration = 0;
        finishedPlaying = false;
    }

    @Override
    public void update(float delta) {
        currentDuration += delta;
        if (currentDuration >= totalDuration() || currentDuration <= 0) {
            currentDuration = totalDuration();
            if (!finishedPlaying) notify(Animation.AnimationListenerState.FINISHED);
            finishedPlaying = true;
        }
    }

    @Override
    public int getFrameIndex() {
        return 0;
    }

    @Override
    public TextureRegion getFrame() {
        return textureRegion;
    }


    public SingleFrame listen(AnimationListener listener) {
        if (listener == null) {
            throw new AnimagicException("Animation listener cannot be null");
        }
        listeners.add(listener);
        return this;
    }

    public SingleFrame stopListening(AnimationListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);
        return this;
    }

    private void notify(Animation.AnimationListenerState listenerState) {
        for (AnimationListener listener : listeners) listener.animationNotification(this, listenerState);
    }
}
