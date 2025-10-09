package com.plcoding.drawinginjetpackcompose.Animation;

import android.os.Looper;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.os.Handler;

public class Animator {
    public static void animate(TextView view) {
        view.setVisibility(TextView.VISIBLE);
        view.setAlpha(0f);
        view.setScaleX(.5f);
        view.setScaleY(.5f);

        new Handler(Looper.getMainLooper())
                .postDelayed(
                        () -> {
                            AnimationHelper.popUp(view, () -> {
                                AnimationHelper.fade(view);
                            });
                        },
                        250
                );
    }
}

class AnimationHelper {
    public static void popUp(TextView view, Runnable runnable) {
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(750)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(runnable)
                .start();
    }

    public static void fade(TextView view) {
        view.animate()
                .alpha(0f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(1000)
                .start();
    }
}