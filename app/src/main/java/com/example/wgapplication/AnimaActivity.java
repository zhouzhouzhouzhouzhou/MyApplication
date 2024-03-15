package com.example.wgapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.CommonUtils;
import com.example.DensityUtil;

public class AnimaActivity extends AppCompatActivity {

    private ImageView bigIv;
    private ImageView smallIv;

    private static final String TAG = "AnimaActivityAnimaActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anima);
        Button button = findViewById(R.id.btnStart);
        bigIv = findViewById(R.id.ivBig);
        smallIv = findViewById(R.id.ivSmall);
        findViewById(R.id.btnBig).setOnClickListener(view -> {
            startBigAnima();
        });
        ImageView smallIv = findViewById(R.id.ivSmall);
        startBigAnima();
        button.setOnClickListener(v -> {
            startAnima();
        });
        ImageView ivControl = findViewById(R.id.ivControl);
        findViewById(R.id.btnControl).setOnClickListener(view -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivControl.getLayoutParams();
            layoutParams.width = 50;
            layoutParams.height = 50;

            ivControl.setX(960);
            ivControl.setY(325);
            ivControl.setLayoutParams(layoutParams);
        });
        float scaleXRatio = (float)DensityUtil.dp2px(126)/(float)CommonUtils.getScreenWidth(this);
        float scaleYRatio = (float)DensityUtil.dp2px(204)/(float)CommonUtils.getScreenHeight(this);
        Log.i(TAG,"onCreate: scaleXRatio  "+ scaleXRatio + " scaleYRatio "+ scaleYRatio);
    }

    public void startAnima() {
        // 大小变化动画，从当前大小变为40
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(bigIv, "scaleX", 1f, 0.55f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(bigIv, "scaleY", 1f, 0.55f);

        ObjectAnimator moveX =
                ObjectAnimator.ofFloat(bigIv, "translationX",  300);
        ObjectAnimator moveY =
                ObjectAnimator.ofFloat(bigIv, "translationY",   500);


        // 创建动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(2000);
//        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.playTogether(scaleX, scaleY,moveX,moveY);


        // 启动动画
        animatorSet.start();
    }


    public void startBigAnima() {
        // 大小变化动画，从当前大小变为40
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(smallIv, "scaleX", 1f, 4f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(smallIv, "scaleY", 1f, 5f);
//
        ObjectAnimator moveX =
                ObjectAnimator.ofFloat(smallIv, "translationX",  -230);
        ObjectAnimator moveY =
                ObjectAnimator.ofFloat(smallIv, "translationY",   -370);


        // 创建动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(1000);
//        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.playTogether(scaleX, scaleY); //,moveX,moveY


        // 启动动画
        animatorSet.start();
    }
}
