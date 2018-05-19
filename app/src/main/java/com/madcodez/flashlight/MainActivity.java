package com.madcodez.flashlight;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public ImageButton iOn;
    boolean on;
    ConstraintLayout mCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCon = (ConstraintLayout) findViewById(R.id.mainCons);
        iOn = (ImageButton) findViewById(R.id.imageButton);
        iOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOn();
            }
        });

        postAnimationRev(iOn, 100);
    }

    public void postAnimation(final View view, int animationTime) {
        view.setVisibility(View.INVISIBLE);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_alpha);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(animation);
                view.setVisibility(View.VISIBLE);
            }
        }, animationTime);
    }

    public void postAnimationRev(final View view, int animationTime) {
        view.setVisibility(View.INVISIBLE);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(animation);
                view.setVisibility(View.VISIBLE);
            }
        }, animationTime);
    }

    public void turnOn() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            if (!on) {
                os.writeBytes("echo \"255\" >/sys/class/leds/led:flash_torch/brightness\n"); // cm12
//                os.writeBytes("echo \"255\" >/sys/class/leds/torch-light/brightness\n");
                on = true;
                iOn.setImageResource(R.mipmap.ic_bulb_on);
                mCon.setBackgroundColor(getResources().getColor(R.color.colorOn));
                //    postAnimationRev(mCon, 200);
                postAnimation(iOn, 200);

                animCircle(mCon);
                animCircle(iOn);
//                animCircle(iOn);

            } else {
                os.writeBytes("echo \"0\" >/sys/class/leds/led:flash_torch/brightness\n"); // cm12

                //              os.writeBytes("echo \"0\" >/sys/class/leds/torch-light/brightness\n");
                on = false;
                iOn.setImageResource(R.mipmap.ic_bulb_off);
                mCon.setBackgroundColor(getResources().getColor(R.color.colorOff));

//                postAnimationRev(mCon, 200);
                animCircle(mCon);
                animCircle(iOn);
                //              animCircle(iOn);

                postAnimationRev(iOn, 100);

            }
            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                if (p.exitValue() != 255) {
                    // TODO Code to run on success
                    //       toastMessage("root");
                } else {
                    // TODO Code to run on unsuccessful
//                    toastMessage("not root");
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                //              toastMessage("not root");
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            //        toastMessage("not root");
        }

    }

    public void animCircle(View mCon) {
        int centerX = (mCon.getLeft() + mCon.getRight()) / 2;
        int centerY = (mCon.getTop() + mCon.getBottom()) / 2;

        int startRadius = 0;
// get the final radius for the clipping circle
        int endRadius = Math.max(mCon.getWidth(), mCon.getHeight());

// create the animator for this view (the start radius is zero)
        Animator anim = null;
        Animation animation = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(mCon, centerX, centerY, startRadius, endRadius);
        } else {
            animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_alpha);
        }

// make the view visible and start the animation
        mCon.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim.start();
            } else {
                animation.start();
            }
        }

    }

    public void toastMessage(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

}
