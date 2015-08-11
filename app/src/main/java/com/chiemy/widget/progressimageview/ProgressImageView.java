package com.chiemy.widget.progressimageview;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by chiemy on 15/6/11.
 * @author chiemy
 */
public class ProgressImageView extends ImageView {
    private static int SHADOW_WIDTH;
    private int maxProgress = 100;
    private Paint fermodePaint;
    /**
     * 进度条背景
     */
    private Paint bgArcPaint;
    /**
     * 进度条
     */
    private Paint progressPaint;
    private Paint innerShadowPaint;
    private Paint progressPaint2; //中间的白色

    private float halfArcWidth;
    private float arcWidth;
    private float progressWidth;
    private float blurRadius; //模糊半径
    private int shadowColor;
    private int progressColor;

    public ProgressImageView(Context context) {
        super(context);
        init();
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        blurRadius = PixelUtil.dp2px(5, getContext());
        SHADOW_WIDTH = PixelUtil.dp2px(10,getContext());
        arcRadius = getResources().getDisplayMetrics().heightPixels;
        arcWidth = PixelUtil.dp2px(6, getContext()) + 10;
        progressWidth = arcWidth;
        halfArcWidth = arcWidth / 2;

        progressColor = getResources().getColor(R.color.color_main);
        shadowColor = getResources().getColor(R.color.color_main_light);

        fermodePaint = new Paint();
        fermodePaint.setAntiAlias(true);
        fermodePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        bgArcPaint = new Paint();
        int color = getResources().getColor(R.color.color_grey);
        bgArcPaint.setColor(color);
        bgArcPaint.setAntiAlias(true);
        bgArcPaint.setStyle(Paint.Style.STROKE);
        bgArcPaint.setStrokeWidth(2 * arcWidth);

        BlurMaskFilter maskFilter = new BlurMaskFilter(blurRadius/2, BlurMaskFilter.Blur.NORMAL);
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setMaskFilter(maskFilter);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint2 = new Paint();
        progressPaint2.setColor(shadowColor);
        progressPaint2.setMaskFilter(maskFilter);
        progressPaint2.setAntiAlias(true);
        progressPaint2.setStyle(Paint.Style.STROKE);
        progressPaint2.setStrokeWidth(progressWidth / 2);
        progressPaint2.setStrokeCap(Paint.Cap.ROUND);

        innerShadowPaint = new Paint();
        innerShadowPaint.setAntiAlias(true);
        innerShadowPaint.setStyle(Paint.Style.STROKE);
        innerShadowPaint.setStrokeWidth(2 * SHADOW_WIDTH);
        innerShadowPaint.setStrokeCap(Paint.Cap.ROUND);

        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
    }

    private RectF fermodeRect, arcRect;
    private Paint imagePaint;
    //    @Override
    //    public void draw(Canvas canvas) {
    //        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    //
    //        Canvas mCanvas = new Canvas(bitmap);
    //        super.draw(mCanvas);
    //
    //        drawPath(mCanvas);
    //
    //        canvas.drawBitmap(bitmap, 0, 0, imagePaint);
    //        bitmap.recycle();
    //    }

    private Path path;
    private double radians = -1;
    private float offset;
    /**
     * 去除的图片部分
     * @param canvas
     */
    private void drawPath(Canvas canvas){
        path = new Path();
        path.moveTo(0, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - offset);
        path.arcTo(fermodeRect, startDegree, sweepDegree);
        path.close();
        canvas.drawPath(path, fermodePaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 白底
        canvas.drawArc(fermodeRect, startDegree - 1, sweepDegree + 2, false, bgArcPaint);
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas){
        float ratio = (float)currentProgress / maxProgress;
        float sweep = sweepDegree * ratio;
        canvas.drawArc(arcRect, startDegree + sweepDegree, -sweep, false, progressPaint);
        canvas.drawArc(arcRect, startDegree + sweepDegree, -sweep, false, progressPaint2);
        //canvas.drawArc(fermodeRect, startDegree + sweepDegree, -sweep, false, innerShadowPaint);
    }

    private float centerX, arcRadius;
    private float startDegree, sweepDegree;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = getWidth() / 2;

        radians = Math.asin(centerX / arcRadius);
        float degree = (float) Math.toDegrees(radians);
        startDegree = 90 - degree;
        sweepDegree = 2 * degree;
        offset = (float)(arcRadius - Math.cos(radians)* arcRadius);

        float left = centerX - arcRadius;
        float right = centerX + arcRadius;
        float top = getHeight() - 2* arcRadius;
        float bottom = getHeight();
        arcRect = new RectF(left + halfArcWidth, top + halfArcWidth, right - halfArcWidth, bottom - halfArcWidth);
        //fermodeRect = new RectF(left, top, right, bottom);
        double bigRadius = Math.hypot(centerX, arcRadius);
        float offset = (float)(bigRadius - arcRadius);
        fermodeRect = new RectF(left-offset, top-offset, right+offset, bottom+offset);
        bgArcPaint.setStrokeWidth(2 * (offset + arcWidth));

        int [] innerColors = {progressColor, shadowColor};
        float r = SHADOW_WIDTH / arcRadius;
        float [] innerPositions = {1-r, 1};

        RadialGradient innerShadowGradient = new RadialGradient(centerX, getHeight() - arcRadius, arcRadius, innerColors, innerPositions, Shader.TileMode.CLAMP);
        innerShadowPaint.setShader(innerShadowGradient);
    }


    /**
     * 设置最大进度值
     */
    public void setMax(int max){
        maxProgress = max;
    }

    public int getMax(){
        return maxProgress;
    }

    private int currentProgress;
    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress){
        progress = Math.min(progress, maxProgress);
        currentProgress = Math.max(0, progress);
        invalidate();
    }

}
