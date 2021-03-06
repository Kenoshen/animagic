package com.bytebreakstudios.animagic.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bytebreakstudios.animagic.utils.AnimagicException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationBlend implements IFrameByFrameAnimation {
    private String currentAnimationName = null;
    private int frameCount;
    private Map<String, Animation> blendData = new HashMap<>();

    public AnimationBlend(int frameCount){
        this.frameCount = frameCount;
    }

    public List<String> names(){
        List<String> l = new ArrayList<>();
        l.addAll(blendData.keySet());
        return l;
    }

    public AnimationBlend addAnimation(Animation animation) {
        if (animation == null) throw new AnimagicException("A null animation cannot be added to an AnimationBlend");
        if (frameCount != animation.totalFrames()) throw new AnimagicException("This AnimationBlend has a frame count of " + frameCount +", therefore, no animations can be added to it that do not have a total frame count of " + frameCount + " (tried one with " + animation.totalFrames() + " frames)");
        if (currentAnimationName == null) currentAnimationName = animation.name();
        blendData.put(animation.name(), animation);
        return this;
    }

    public boolean hasAnimation(String animationName){
        return blendData.containsKey(animationName);
    }

    public AnimationBlend switchToAnimation(String animationName) {
        if (!hasAnimation(animationName)) throw new AnimagicException("AnimationBlend does not have an animation by the name: " + animationName);
        Animation old = currentAnimation();
        currentAnimationName = animationName;
        currentAnimation().reset();
        currentAnimation().setFrameIndex(old.getFrameIndex());
        return this;
    }

    @Override
    public void reset() {
        for (String key : blendData.keySet()) blendData.get(key).reset();
    }

    private Animation currentAnimation(){
        return blendData.get(currentAnimationName);
    }

    @Override
    public void update(float delta) {
        currentAnimation().update(delta);
    }

    @Override
    public int getFrameIndex() {
        return currentAnimation().getFrameIndex();
    }

    @Override
    public TextureRegion getFrame() {
        return currentAnimation().getFrame();
    }
}
