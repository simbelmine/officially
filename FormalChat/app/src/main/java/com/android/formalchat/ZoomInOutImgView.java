package com.android.formalchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Sve on 4/1/15.
 */
public class ZoomInOutImgView extends ImageView {
    private static final int INVALID_POINTER_ID = -1;

    private Drawable image;
    private float posX;
    private float posY;
    private float lastTouchX;
    private float lastTouchY;
    private int activePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.f;

    private int size;
    private int pX;
    private int pY;

//    public ZoomInOutImgView(Context context) {
//        this(context, null, 0);
//        image = getResources().getDrawable(context.getResources().getIdentifier("background", "drawable", context.getPackageName()));
//        image.setBounds(50, 50, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//
//        RelativeLayout rl = (RelativeLayout)findViewById(R.id.root_crop);
//        posX = rl.getWidth()/2;
//        posY = rl.getHeight()/2;
//        Log.v("formalchat", "posX = "+posX+"   posY = "+posY);
//    }
//
//    public ZoomInOutImgView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public ZoomInOutImgView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//    }


    public ZoomInOutImgView(Context context, Drawable image, int pX, int pY) {
        super(context);
        this.image = image;
        int pxs = (int) (100 * getResources().getDisplayMetrics().density);
        image.setBounds(0, 0, pxs, pxs);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());

        posX = pX / 2 - pxs / 2;
        posY = pY / 2 - pxs / 2;
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        size = 0;
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//
//        if (width > height) {
//            size = height;
//        } else {
//            size = width;
//        }
//
//        setMeasuredDimension(size, size);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                float y = event.getY();
                lastTouchX = x;
                lastTouchY = y;
                activePointerId = event.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int pointerIndex = event.findPointerIndex(activePointerId);
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);

                //Only move is scaleGestureDetector isn't processing a gesture
                if (!scaleGestureDetector.isInProgress()) {
                    float dx = x - lastTouchX;
                    float dy = y - lastTouchY;
                    posX += dx;
                    posY += dy;

                    invalidate();
                }

                lastTouchX = x;
                lastTouchY = y;
                break;
            }
            case MotionEvent.ACTION_UP:
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.

                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;

                    lastTouchX = event.getX(newPointerIndex);
                    lastTouchY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(posX, posY);
        canvas.scale(scaleFactor, scaleFactor);
        image.draw(canvas);

        canvas.restore();
    }

    public Drawable getImageDrawable() {
        return image;
    }

    public int getPosX() {
        return (int) posX;
    }

    public int getPosY() {
        return (int) posY;
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

            invalidate();
            return true;
        }
    }
}
