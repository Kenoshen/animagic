package com.bytebreak.animagic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Animation implements IFrameByFrameAnimation {
    private String name;
    private List<AnimationListener> listeners = new ArrayList<>();
    private TextureRegion[] textures;
    private int[] keyframes;
    private float totalDuration = 0;
    private float currentDuration = 0;
    private AnimationPlayState playState = AnimationPlayState.ONCE;
    private float playDirectionModifier = 1;
    private boolean finishedPlaying = false;
    private float percentagePerFrame;

    public Animation(@NotNull String name, AnimationPlayState playState, float frameDuration, @NotNull TextureRegion[] textureArray, @NotNull int[] keyframes){
        if (name == null) throw new AnimagicException("Animation.name cannot be null");
        if (name.trim().equalsIgnoreCase("")) throw new AnimagicException("Animation.name cannot be ''");
        if (textureArray == null) throw new AnimagicException("Animation.textures cannot be null");
        if (textureArray.length == 0) throw new AnimagicException("Animation.textures cannot be empty");
        if (keyframes == null) throw new AnimagicException("Animation.keyframes cannot be null");

        this.name = name;
        this.playState = playState;
        this.totalDuration = frameDuration * textureArray.length;
        this.textures = textureArray;
        this.keyframes = keyframes;

        if (this.totalDuration < 0) this.totalDuration = 0;
        percentagePerFrame = 1f / textures.length;
    }

    public Animation(@NotNull String name, AnimationPlayState playState, float totalDurationSeconds, @NotNull TextureRegion[] textureArray){
        this(name, playState, totalDurationSeconds, textureArray, new int[0]);
    }

    public String name(){
        return name;
    }

    public float totalDuration(){
        return totalDuration;
    }

    public int totalFrames(){
        return textures.length;
    }

    @Override
    public void reset(){
        currentDuration = 0;
        finishedPlaying = false;
    }

    public Animation setFrameIndex(int index) {
        if (index < 0) throw new AnimagicException("Cannot set the frame index to less than 0");
        if (index >= totalFrames()) throw new AnimagicException("Cannot set the frame index to more than the totalFrames(" + totalFrames() + ") - 1");
        currentDuration = (percentagePerFrame * index) * totalDuration;
        return this;
    }

    @Override
    public void update(float delta){
        currentDuration += delta * playDirectionModifier;
        if (currentDuration >= totalDuration || currentDuration <= 0) {
            switch (playState){
                case ONCE:
                    currentDuration = totalDuration;
                    if (!finishedPlaying){
                        notify(AnimationListenerState.FINISHED);
                    }
                    finishedPlaying = true;
                    break;
                case REPEAT:
                    currentDuration = 0;
                    notify(AnimationListenerState.REPEATED);
                    break;
                case PINGPONG:
                    if (playDirectionModifier == 1){
                        currentDuration = totalDuration;
                        notify(AnimationListenerState.PINGED);
                    } else {
                        currentDuration = 0;
                        notify(AnimationListenerState.PONGED);
                    }
                    playDirectionModifier *= -1;
                    break;
            }
        }

        if (keyframes != null){
            int curFrame = getFrameIndex();
            boolean isKeyframe = false;
            for (int keyframe : keyframes){
                if (keyframe == curFrame){
                    isKeyframe = true;
                    break;
                }
            }
            if (isKeyframe) notify(AnimationListenerState.KEYFRAME);
        }
    }

    @Override
    public int getFrameIndex(){
        if (percentagePerFrame <= 0) return 0;
        float percComp = percentComplete();
        if (percComp == 1) return totalFrames() - 1;
        return (int)Math.floor(percComp / percentagePerFrame);
    }

    @Override
    public TextureRegion getFrame(){
        return textures[getFrameIndex()];
    }

    public float percentComplete(){
        if (currentDuration == 0) return 0;
        else if (currentDuration >= totalDuration) return 1;
        else return currentDuration / totalDuration;
    }

    public Animation listen(@NotNull AnimationListener listener) {
        if (listener == null){
            throw new AnimagicException("Animation listener cannot be null");
        }
        listeners.add(listener);
        return this;
    }

    private void notify(AnimationListenerState listenerState){
        for (AnimationListener listener : listeners) listener.animationNotification(this, listenerState);
    }

    public enum AnimationPlayState {
        REPEAT,
        PINGPONG,
        ONCE
    }

    public enum AnimationListenerState {
        FINISHED,
        PINGED,
        PONGED,
        REPEATED,
        KEYFRAME
    }
}
