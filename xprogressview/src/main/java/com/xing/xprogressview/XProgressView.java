package com.xing.xprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;

public class XProgressView extends View {
    private Paint paint_background_ring;//背景的Paint
    private Paint paint_progress;//进度条的Paint
    private Paint paint_full;//完成的Paint
    private Paint paint_tick;//对勾的Paint
    private Paint paint_error;//X的Paint
    private Paint paint_transparent;//透明的Paint
    private RectF rect_background;//背景的RectF
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
    public int state = State.Start.ordinal();

    private TypedArray ta;
    private int color_background_ring;
    private int color_progress;
    private int color_complete;
    private int color_white;
    private int color_error;

    public enum State {
        Start, Run, Complete, Error
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
        color_white = ta.getColor(R.styleable.XProgressView_xpv_color_tick, Color.WHITE);
        color_error = ta.getColor(R.styleable.XProgressView_xpv_color_error, Color.RED);
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

        paint_tick = new Paint();
        paint_tick.setAntiAlias(true);
        paint_tick.setStyle(Paint.Style.STROKE);
        paint_tick.setColor(color_white);

        paint_transparent = new Paint();
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

        strokewidth_background = size / 100;
        strokewidth_progress = strokewidth_background * 3;
        strokewidth_tick = strokewidth_background * 6;
        paint_background_ring.setStrokeWidth(strokewidth_background);
        paint_progress.setStrokeWidth(strokewidth_progress);
        paint_tick.setStrokeWidth(strokewidth_tick);

        rect_background = new RectF(strokewidth_background / 2, strokewidth_background / 2, size - strokewidth_background / 2, size - strokewidth_background / 2);
        int w = (strokewidth_background + strokewidth_progress) / 2;
        rect_progress = new RectF(w, w, size - w, size - w);

        int pix = (int) Math.sqrt(size * size);
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
            canvas.drawPath(path_play, paint_full);
            //绘制背景圆环
            canvas.drawArc(rect_background, 0, 360, false, paint_background_ring);
            invalidate();
        }
        if (state == State.Run.ordinal()) {
            canvas.drawPath(path_stop, paint_full);
            //绘制背景圆环
            canvas.drawArc(rect_background, 0, 360, false, paint_background_ring);
            //绘制进度条
            canvas.drawArc(rect_progress, startAngle, sweepAngle, false, paint_progress);
            invalidate();
        }
        if (state == State.Complete.ordinal()) {
            //绘制完成满圆和对勾
            canvas.drawArc(rect_background, 0, 360, false, state == XProgressView.State.Complete.ordinal() ? paint_full : paint_transparent);
            canvas.drawPath(path_tick, state == State.Complete.ordinal() ? paint_tick : paint_transparent);
            invalidate();
        }
        if (state == State.Error.ordinal()) {
            //绘制完成满圆和对勾
            canvas.drawArc(rect_background, 0, 360, false, paint_error);
            canvas.drawPath(path_error, paint_tick);
            invalidate();
        }
    }

    /**
     * 设置进度条
     *
     * @param progress 0~100
     */
    public void setupprogress(int progress) {
        if (state == State.Error.ordinal()) {
            return;
        }
        if (state == State.Complete.ordinal()) {
            return;
        }
        if (progress <= 0) {
            progress = 0;
            state = State.Start.ordinal();
        } else if (progress > 0 && progress < 100) {
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
        state = State.Start.ordinal();
        setupprogress(0);
    }

    /**
     * 完成
     */
    public void complete() {
        setupprogress(0);
        state = State.Complete.ordinal();
    }

    /**
     * 错误
     */
    public void error() {
        setupprogress(0);
        state = State.Error.ordinal();
    }
}