package com.xing.xprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;

public class XProgressView extends View {
    private Paint paint_background_ring;//背景的Paint
    private Paint paint_progress;//进度条的Paint
    private Paint paint_full;//完成的Paint
    private Paint paint_tick;//对勾的Paint
    private Paint paint_error;//X的Paint
    private Paint paint_wait;//等待的Paint
    private Paint paint_transparent;//透明的Paint

    private RectF rect_wait;//等待的RectF
    private RectF rect_progress;//进度条的RectF

    private float startAngle = -90;//开始的角度
    private float sweepAngle = 0;//现在的角度

    private int strokewidth_background = 2;
    private int strokewidth_progress = 7;
    private int strokewidth_tick = 15;

    private Path path_play;
    private Path path_stop;
    private Path path_tick;
    private Path path_error;

    private int size;
    private int pix;
    public int state = State.Start.ordinal();

    private TypedArray ta;

    private int color_background_ring;
    private int color_progress;
    private int color_complete;
    private int color_tick;
    private int color_error;

    private int img_wait;
    private int img_play;
    private int img_stop;
    private int img_complete;

    private Bitmap bmp_wait;
    private Bitmap bmp_play;
    private Bitmap bmp_stop;
    private Bitmap bmp_complete;

    private float scale_wait;
    private float scale_play;
    private float scale_stop;
    private float scale_comllete;
    private WaitThread waitThread;
    private boolean isWait = false;
    private int wait_progress = 270;

    public enum State {
        Start, Run, Complete, Error, Wait
    }

    public XProgressView(Context context) {
        super(context, null);
    }

    public XProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ta = context.obtainStyledAttributes(attrs, R.styleable.XProgressView);
        color_background_ring = ta.getColor(R.styleable.XProgressView_xpv_color_background_ring, Color.rgb(0, 161, 234));
        color_progress = ta.getColor(R.styleable.XProgressView_xpv_color_progress, Color.rgb(0, 161, 234));
        color_complete = ta.getColor(R.styleable.XProgressView_xpv_color_complete, Color.rgb(0, 161, 234));
        color_tick = ta.getColor(R.styleable.XProgressView_xpv_color_tick, Color.WHITE);
        color_error = ta.getColor(R.styleable.XProgressView_xpv_color_error, Color.RED);
        img_play = ta.getResourceId(R.styleable.XProgressView_xpv_img_play, 0);
        img_stop = ta.getResourceId(R.styleable.XProgressView_xpv_img_stop, 0);
        img_complete = ta.getResourceId(R.styleable.XProgressView_xpv_img_complete, 0);
        img_wait = ta.getResourceId(R.styleable.XProgressView_xpv_img_wait, 0);
        if (img_play != 0) {
            bmp_play = BitmapFactory.decodeResource(getResources(), img_play);
        }
        if (img_stop != 0) {
            bmp_stop = BitmapFactory.decodeResource(getResources(), img_stop);
        }
        if (img_complete != 0) {
            bmp_complete = BitmapFactory.decodeResource(getResources(), img_complete);
        }
        if (img_wait != 0) {
            bmp_wait = BitmapFactory.decodeResource(getResources(), img_wait);
        }
        scale_wait = ta.getFloat(R.styleable.XProgressView_xpv_scale_wait, 0);
        scale_play = ta.getFloat(R.styleable.XProgressView_xpv_scale_play, 0);
        scale_stop = ta.getFloat(R.styleable.XProgressView_xpv_scale_stop, 0);
        scale_comllete = ta.getFloat(R.styleable.XProgressView_xpv_scale_complete, 0);
        state = ta.getInt(R.styleable.XProgressView_xpv_state, 0);
        init();
    }

    private void init() {
        rect_progress = new RectF(0, 0, 100, 100);

        paint_background_ring = new Paint();
        paint_background_ring.setAntiAlias(true);
        paint_background_ring.setStyle(Paint.Style.STROKE);
        paint_background_ring.setColor(color_background_ring);

        paint_progress = new Paint();
        paint_progress.setAntiAlias(true);
        paint_progress.setStyle(Paint.Style.STROKE);
        paint_progress.setColor(color_progress);

        paint_full = new Paint();
        paint_full.setAntiAlias(true);
        paint_full.setStyle(Paint.Style.FILL);
        paint_full.setColor(color_complete);

        paint_error = new Paint();
        paint_error.setAntiAlias(true);
        paint_error.setStyle(Paint.Style.FILL);
        paint_error.setColor(color_error);

        paint_wait = new Paint();
        paint_wait.setAntiAlias(true);
        paint_wait.setStyle(Paint.Style.FILL);
        paint_wait.setColor(color_tick);

        paint_tick = new Paint();
        paint_tick.setAntiAlias(true);
        paint_tick.setStyle(Paint.Style.STROKE);
        paint_tick.setColor(color_tick);

        paint_transparent = new Paint();
        paint_transparent.setAntiAlias(true);
        paint_transparent.setStyle(Paint.Style.STROKE);
        paint_transparent.setColor(Color.TRANSPARENT);

        AlphaAnimation alp = new AlphaAnimation(0.0f, 1.0f);
        alp.setDuration(1000);
        alp.setRepeatCount(AlphaAnimation.INFINITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 100;//wrap_content默认大小
        int desiredHeight = 100;//wrap_content默认大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }
        size = Math.min(width, height);

        strokewidth_background = size / 40;
        strokewidth_progress = strokewidth_background * 3;
        strokewidth_tick = strokewidth_background * 6;
        paint_background_ring.setStrokeWidth(strokewidth_background);
        paint_progress.setStrokeWidth(strokewidth_progress);
        paint_tick.setStrokeWidth(strokewidth_tick);

        rect_wait = new RectF(0, 0, size, size);
        int w = strokewidth_background + strokewidth_progress / 2;
        rect_progress = new RectF(w, w, size - w, size - w);

        pix = (int) Math.sqrt(size * size);
        path_play = new Path();
        path_play.moveTo(pix * 40 / 100, pix * 36 / 100);
        path_play.lineTo(pix * 40 / 100, pix * 63 / 100);
        path_play.lineTo(pix * 69 / 100, pix * 50 / 100);
        path_play.close();

        path_stop = new Path();
        path_stop.moveTo(pix * 38 / 100, pix * 38 / 100);
        path_stop.lineTo(pix * 62 / 100, pix * 38 / 100);
        path_stop.lineTo(pix * 62 / 100, pix * 62 / 100);
        path_stop.lineTo(pix * 38 / 100, pix * 62 / 100);
        path_stop.close();

        path_tick = new Path();
        path_tick.moveTo(pix * 30 / 100, pix * 50 / 100);
        path_tick.lineTo(pix * 45 / 100, pix * 625 / 1000);
        path_tick.lineTo(pix * 65 / 100, pix * 350 / 1000);

        path_error = new Path();
        path_error.moveTo(pix * 350 / 1000, pix * 350 / 1000);
        path_error.lineTo(pix * 650 / 1000, pix * 650 / 1000);
        path_error.moveTo(pix * 650 / 1000, pix * 350 / 1000);
        path_error.lineTo(pix * 350 / 1000, pix * 650 / 1000);

        setMeasuredDimension(width + strokewidth_background, height + strokewidth_background);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == State.Start.ordinal()) {
            //绘制开始和运行
            if (img_play == 0) {
                canvas.drawPath(path_play, paint_full);
            } else {
                canvas.drawBitmap(bmp_play, null, getRect(scale_play), paint_full);
            }
            //绘制背景圆环
            canvas.drawCircle(size / 2, size / 2, size / 2 - strokewidth_background, paint_background_ring);
        }
        if (state == State.Run.ordinal()) {
            if (img_stop == 0) {
                canvas.drawPath(path_stop, paint_full);
            } else {
                canvas.drawBitmap(bmp_stop, null, getRect(scale_stop), paint_full);
            }
            //绘制背景圆环
            canvas.drawCircle(size / 2, size / 2, size / 2 - strokewidth_background, paint_background_ring);
            //绘制进度条
            canvas.drawArc(rect_progress, startAngle, sweepAngle, false, paint_progress);
        }
        if (state == State.Complete.ordinal()) {
            //绘制完成满圆和对勾
            if (img_complete == 0) {
                canvas.drawCircle(size / 2, size / 2, size / 2 - strokewidth_background, state == XProgressView.State.Complete.ordinal() ? paint_full : paint_transparent);
                canvas.drawPath(path_tick, state == State.Complete.ordinal() ? paint_tick : paint_transparent);
            } else {
                canvas.drawBitmap(bmp_complete, null, getRect(scale_comllete), paint_full);
            }
        }
        if (state == State.Error.ordinal()) {
            //绘制完成满圆和对勾
            canvas.drawCircle(size / 2, size / 2, size / 2 - strokewidth_background, paint_error);
            canvas.drawPath(path_error, paint_tick);
        }
        if (state == State.Wait.ordinal()) {
            canvas.drawCircle(size / 2, size / 2, size / 2 - strokewidth_background, paint_background_ring);
            canvas.drawArc(rect_wait, wait_progress % 360, 30, true, paint_wait);
            if (img_wait == 0) {
                canvas.drawPath(path_play, paint_full);
            } else {
                canvas.drawBitmap(bmp_wait, null, getRect(scale_wait), paint_full);
            }
        }
        invalidate();
    }

    /**
     * 设置进度条
     *
     * @param progress 0~100
     */
    public void setupprogress(int progress) {
        if (progress <= 0) {
            progress = 0;
            state = State.Start.ordinal();
        } else if (progress > 0 && progress < 100) {
            if (isWait) {
                isWait = false;
            }
            state = State.Run.ordinal();
        } else if (progress >= 100) {
            progress = 100;
        }
        sweepAngle = (float) (progress * 3.6);
    }

    /**
     * 重置
     */
    public void reset() {
        reset(State.Start.ordinal());
    }

    /**
     * 重置
     */
    public void reset(int state) {
        this.state = state;
        isWait = false;
    }

    /**
     * 完成
     */
    public void complete() {
        state = State.Complete.ordinal();
        isWait = false;
    }

    /**
     * 错误
     */
    public void error() {
        state = State.Error.ordinal();
        isWait = false;
    }

    /**
     * 等待
     */
    public void wait_start() {
        wait_start(270);
    }

    /**
     * 等待
     */
    public void wait_start(int wait_progress) {
        setupprogress(0);
        state = State.Wait.ordinal();
        isWait = true;
        this.wait_progress = wait_progress;
        if (waitThread == null) {
            waitThread = new WaitThread();
            waitThread.start();
        }
    }

    private class WaitThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                if (isWait) {
                    SystemClock.sleep(3);
                    wait_progress++;
                }
            }
        }
    }

    private Rect getRect(float scale) {
        return new Rect((int) (size / 2 - size * scale / 2), (int) (size / 2 - size * scale / 2), (int) (size / 2 + size * scale / 2), (int) (size / 2 + size * scale / 2));
    }

    public void setImg_complete(int resourceid){
        img_complete = resourceid;
        bmp_complete = BitmapFactory.decodeResource(getResources(), img_complete);
        invalidate();
    }
}