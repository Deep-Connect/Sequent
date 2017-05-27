package com.fujiyuu75.sequent;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by y_fujikawa on 2017/05/24.
 */
public class Sequent {
    private static final String TAG = "Sequent";

    private List<View> viewList = new ArrayList<>();
    private ViewGroup vg;
    private final int startOffset;
    private final int duration;
    private final Direction direction;
    private final Context context;
    private final int animId;

    public static class Builder {
        private static final int DEFAULT_OFFSET = 70;
        private static final int DEFAULT_DURATION = 500;

        private ViewGroup vg;
        private int startOffset = DEFAULT_OFFSET;
        private int duration = DEFAULT_DURATION;
        private Direction direction = Direction.FORWARD;
        private Context context;
        private int animId;

        Builder(ViewGroup vg) {
            this.vg = vg;
        }

        public Builder offset(int offset) {
            this.startOffset = offset;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder flow(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder anim(Context context, int animId) {
            this.context = context;
            this.animId = animId;
            return this;
        }

        public Sequent start() {
            return new Sequent(this);
        }
    }

    public static Builder origin(ViewGroup vg) {
        return new Builder(vg);
    }

    private Sequent(Builder builder) {
        this.vg = builder.vg;
        this.startOffset = builder.startOffset;
        this.duration = builder.duration;
        this.direction = builder.direction;
        this.context = builder.context;
        this.animId = builder.animId;

        fetchChildLayouts(vg);
        arrangeLayouts(viewList);
        setAnimation();
    }

    private void fetchChildLayouts(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                fetchChildLayouts((ViewGroup) view);
            } else {
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                    viewList.add(view);
                }
            }
        }
    }

    private List<View> arrangeLayouts(List<View> viewList) {
        switch (direction) {
            case BACKWARD:
                Collections.reverse(viewList);
                break;
        }
        return viewList;
    }

    private void setAnimation() {
        int count = viewList.size();
        for (int i = 0; i < count; i++) {
            final View view = viewList.get(i);
            final int offset = i * startOffset;

            List<Animator> animatorList = new ArrayList<>();
            animatorList.add(getStartObjectAnimator(offset, view));

            if (animId != 0) {
                animatorList.add(getResAnimator(context, animId, view));
            } else {
                animatorList.add(ObjectAnimator.ofFloat( view, View.ALPHA, 0, 1 ));
            }

            AnimatorSet set = new AnimatorSet();
            set.playTogether(animatorList);
            set.setDuration(duration);
            set.setStartDelay(i * startOffset);
            set.setInterpolator(new OvershootInterpolator());
            set.start();
        }
    }

    @NonNull
    private ObjectAnimator getStartObjectAnimator(int offset, final View view) {
        ObjectAnimator ob = ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1);
        ob.setDuration(1).setStartDelay(offset);
        ob.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator anim) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator anim) {
            }

            @Override
            public void onAnimationEnd(Animator anim) {
            }

            @Override
            public void onAnimationCancel(Animator anim) {
            }
        });
        return ob;
    }

    private Animator getResAnimator(Context context, int animId, View view) {
        Animator anim = AnimatorInflater.loadAnimator(context, animId);
        anim.setTarget(view);
        return anim;
    }
}
