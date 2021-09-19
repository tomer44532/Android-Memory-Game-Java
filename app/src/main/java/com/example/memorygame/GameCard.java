package com.example.memorygame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

public class GameCard extends ImageView {
    public int value;
    public int team = -1;
    public boolean isFlipped = false;
    public Drawable front;
    public Drawable back;
    public boolean isFront;
    public boolean drawFrame;
    public Context context;
    public boolean deleteFrame;
    @SuppressLint("RestrictedApi")
    public GameCard(Context context, int value,int world)
    {
        super(context);
        this.value = value;
        this.context = context;
        //front = AppCompatDrawableManager.get().getDrawable(context,value);

        setImageDrawable(back);
        if(world ==0)
        {
            back = getResources().getDrawable(R.drawable.back_card_forest);
        }
        else if(world ==1)
        {
            back = getResources().getDrawable(R.drawable.back_card_candy);
        }
        else if(world ==2)
        {
            back = getResources().getDrawable(R.drawable.back_card_city);
        }


    }

    public GameCard(Context context, int value)
    {
        super(context);
        this.value = value;
        //front = AppCompatDrawableManager.get().getDrawable(context,value);

        setImageDrawable(back);
        back = getResources().getDrawable(R.drawable.back_card_forest);



    }

    public int getValue() {
        return value;
    }

    public Drawable getFront() {
        return front;
    }

    public void setFront(Drawable front) {
        this.front = front;
    }

    public Drawable getBack() {
        return back;
    }

    public void setBack(Drawable back) {
        this.back = back;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    ObjectAnimator invisibleAnim = ObjectAnimator.ofFloat(this,"scaleX",1f,0f).setDuration(500);
    ObjectAnimator visibleAnim = ObjectAnimator.ofFloat(this,"scaleX",0f,1f).setDuration(500);
    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(this,"alpha",1f,0f).setDuration(500);



public void fadeAnimation()
{
    fadeAnim.start();
}



public void flipFrontAnimation()
{
    invisibleAnim.setInterpolator(new DecelerateInterpolator());
    visibleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
    invisibleAnim.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {

                super.onAnimationEnd(animation);
                setImageDrawable(front);
                clearAnimation();
                visibleAnim.start();
            if(front == getDrawable())
            {

                drawFrame = true;
                invalidate();
            }



        }
    });
    invisibleAnim.start();
}
public void flipBackAnimation()
{
/*    invisibleAnim.setInterpolator(new DecelerateInterpolator());
    visibleAnim.setInterpolator(new AccelerateDecelerateInterpolator());*/
    invisibleAnim.addListener(new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
        //if(isFront)
        {
            // super.onAnimationEnd(animation);
            setImageDrawable(back);
            //visibleAnim.start();


        }

    }
});
    invisibleAnim.start();


}

    public boolean isFront() {
        return isFront;
    }

    public void setFront(boolean front) {
        isFront = front;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final Canvas tmpCanvas = canvas;
        //Toast.makeText(context, "draw", Toast.LENGTH_SHORT).show();
        if(drawFrame&&isFlipped()&&!deleteFrame)
        {

                    if(team == 0)
                    {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(16);
                        paint.setColor(Color.RED);
                        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
                    }
                    else if(team ==1)
                    {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(16);
                        paint.setColor(Color.BLUE);
                        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
                    }
                    else if(team ==2)
                    {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(16);
                        paint.setColor(Color.GREEN);
                        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
                    }
                    else if(team ==3)
                    {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(16);
                        paint.setColor(getResources().getColor(R.color.yellow));
                        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
                    }
                    drawFrame = false;



        }
        else if(deleteFrame)
        {

            deleteFrame = false;
        }
    }

    public void removeFrame()
    {
        deleteFrame = true;
        setImageDrawable(getFront());
        invalidate();
    }
}
