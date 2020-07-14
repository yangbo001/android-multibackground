package support.background.extension.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import java.lang.reflect.Field;

class ExtendBitmapDrawable extends BitmapDrawable {

    private Path path = new Path();
    private RectF rectF = new RectF();
    
    private float[] cornerRadius = new float[8];
    // stroke
    private int strokeWidth, strokeDashWidth, strokeDashGap;
    private int currentStrokeColor, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor;
    // background state
    private int currentBackgroundStateColor = Color.TRANSPARENT,
            backgroundPressedColor, backgroundCheckedColor, backgroundDisableColor;

    private boolean stateEnable = false;

    @SuppressWarnings("deprecation")
    ExtendBitmapDrawable() {
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = getPaint();
        paint.setAntiAlias(true);
        Rect rect = getBounds();
        rectF.set(rect.left + strokeWidth / 2f, rect.top + strokeWidth / 2f, rect.right - strokeWidth / 2f, rect.bottom - strokeWidth / 2f);
        path.reset();
        path.addRoundRect(rectF, cornerRadius, Path.Direction.CW);
        // draw background
        paint.setColor(Color.WHITE);
        canvas.drawPath(path, paint);
        // draw corner bitmap
        int layer = canvas.saveLayer(new RectF(getBounds()), paint, Canvas.ALL_SAVE_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(getBitmap(), null, rect, paint);
        canvas.restoreToCount(layer);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        // draw state background color
        if (stateEnable) {
            paint.setColor(currentBackgroundStateColor);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawPath(path, paint);
        }
        // draw stroke
        if (strokeWidth > 0) {
            paint.setColor(currentStrokeColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            if (strokeDashWidth > 0) paint.setPathEffect(new DashPathEffect(new float[]{strokeDashWidth, strokeDashGap}, 0));
            canvas.drawPath(path, paint);
            paint.setPathEffect(null);
        }
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        if (!stateEnable) { // state not allowed
            invalidateSelf();
            return super.onStateChange(stateSet);
        }
        currentStrokeColor = strokeColor;
        currentBackgroundStateColor = Color.TRANSPARENT;
        boolean enable = false;
        for (int state : stateSet) { // first check if it is checked
            if (state == android.R.attr.state_checked) {
                currentStrokeColor = strokeCheckedColor;
                currentBackgroundStateColor = backgroundCheckedColor;
                break;
            }
        }
        for (int state : stateSet) { // then if it is in pressing status
            if (state == android.R.attr.state_pressed) {
                currentStrokeColor = strokePressedColor;
                currentBackgroundStateColor = backgroundPressedColor;
            }
            if (state == android.R.attr.state_enabled) {
                enable = true;
            }
        }
        if (!enable) { // if disable
            currentStrokeColor = strokeDisableColor;
            currentBackgroundStateColor = backgroundDisableColor;
        }
        invalidateSelf();
        return super.onStateChange(stateSet);
    }

    ExtendBitmapDrawable setCornerRadius(float[] radius) {
        this.cornerRadius = radius;
        return this;
    }

    ExtendBitmapDrawable setStroke(int width, int dashWidth, int dashGap, int color, int pressedColor, int checkedColor, int disableColor) {
        this.strokeWidth = width;
        this.strokeDashWidth = dashWidth;
        this.strokeDashGap = dashGap;
        this.strokeColor = color;
        this.strokePressedColor = pressedColor;
        this.strokeCheckedColor = checkedColor;
        this.strokeDisableColor = disableColor;
        currentStrokeColor = strokeColor;
        return this;
    }

    ExtendBitmapDrawable setBackgroundStateColor(int backgroundPressedColor, int backgroundCheckedColor, int backgroundDisableColor) {
        this.backgroundPressedColor = backgroundPressedColor;
        this.backgroundCheckedColor = backgroundCheckedColor;
        this.backgroundDisableColor = backgroundDisableColor;
        return this;
    }

    ExtendBitmapDrawable setStateEnable() {
        stateEnable = true;
        return this;
    }

    ExtendBitmapDrawable loadAttrFrom(BitmapDrawable target) {
        if (target != null) {
            loadAttr("mBitmapState", target);
            loadAttr("mTargetDensity", target);
        }
        return this;
    }

    private void loadAttr(String fieldName, BitmapDrawable from) {
        try {
            Field field = BitmapDrawable.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, field.get(from));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
