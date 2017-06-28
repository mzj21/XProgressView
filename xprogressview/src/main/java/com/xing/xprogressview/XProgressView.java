package com.xing.xprogressview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class XProgressView extends View {
    private Paint paint_background;//背景的Paint
    private Paint paint_progress;//进度条的Paint
    private Paint paint_draw;//的Paint
    private Paint paint_tick;//对勾的Paint
    private Paint paint_tra;//透明的Paint
    private RectF rect_background;//背景的RectF
    private RectF rect_progress;//进度条的RectF
    private float startAngle = -90;//开始的角度
    private float sweepAngle = 0;//现在的角度
    private int strokewidth_background = 2;
    private int strokewidth_progress = 7;
    private Path path_play;
    private Path path_stop;
    private Path path_tick;
    private int state = State.Start.ordinal();

    public enum State {
        Start, Run, Complete
    }

    public XProgressView(Context context) {
        super(context, null);
    }

    public XProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rect_progress = new RectF(0, 0, 100, 100);

        paint_background = new Paint();
        paint_background.setAntiAlias(true);
        paint_background.setStyle(Paint.Style.STROKE);
        paint_background.setColor(Color.rgb(0, 161, 234));  //Edit this to change progress arc color.
        paint_background.setStrokeWidth(strokewidth_background);

        paint_progress = new Paint();
        paint_progress.setAntiAlias(true);
        paint_progress.setStyle(Paint.Style.STROKE);
        paint_progress.setColor(Color.rgb(0, 161, 234));  //Edit this to change progress arc color.
        paint_progress.setStrokeWidth(strokewidth_progress);

        paint_draw = new Paint();
        paint_draw.setAntiAlias(true);
        paint_draw.setStyle(Paint.Style.FILL);
        paint_draw.setColor(Color.rgb(0, 161, 234));  //Edit this to change progress arc color.

        paint_tick = new Paint();
        paint_tick.setAntiAlias(true);
        paint_tick.setStyle(Paint.Style.STROKE);
        paint_tick.setColor(Color.rgb(255, 255, 255));  //Edit this to change progress arc color.
        paint_tick.setStrokeWidth(10);

        paint_tra = new Paint();
        paint_tra.setStyle(Paint.Style.STROKE);
        paint_tra.setColor(Color.TRANSPARENT);  //Edit this to change progress arc color.
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
        int size = Math.min(width, height);
        rect_background = new RectF(strokewidth_background / 2, strokewidth_background / 2, size - strokewidth_background / 2, size - strokewidth_background / 2);
        int w = (strokewidth_background + strokewidth_progress) / 2;
        rect_progress = new RectF(w, w, size - w, size - w);

        int pix = (int) Math.sqrt(width * height);
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

        setMeasuredDimension(width + strokewidth_background, height + strokewidth_background);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制开始和运行
        canvas.drawPath(state == State.Start.ordinal() ? path_play : path_stop, paint_draw);
        //绘制完成满圆和对勾
        canvas.drawArc(rect_background, 0, 360, false, state == State.Complete.ordinal() ? paint_draw : paint_tra);
        canvas.drawPath(path_tick, state == State.Complete.ordinal() ? paint_tick : paint_tra);
        //绘制背景圆环
        canvas.drawArc(rect_background, 0, 360, false, paint_background);
        //绘制进度条
        canvas.drawArc(rect_progress, startAngle, sweepAngle, false, paint_progress);
        //刷新
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
            state = State.Run.ordinal();
        } else if (progress >= 100) {
            state = State.Complete.ordinal();
            progress = 100;
        }
        sweepAngle = (float) (progress * 3.6);
    }

    /**
     * 重置
     */
    public void reset() {
        setupprogress(0);
    }
}