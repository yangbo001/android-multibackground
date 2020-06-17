package support.background.extension.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

class ExtendStateListDrawable extends StateListDrawable {

    Path path = new Path();
    RectF rectF = new RectF();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Drawable normalDrawable, disableDrawable, pressedDrawable, checkedDrawable;
    // stroke
    private int strokeWidth, strokeDashWidth, strokeDashGap;
    private int strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor;
    // corner
    private float tlRadius, trRadius, blRadius, brRadius;
    // shadow
    private int shadowColor, shadowRadius, shadowOffsetX, shadowOffsetY;
    private int[] drawableShadowPadding = new int[4];// left,top,right,bottom

    ExtendStateListDrawable(@NonNull Drawable normal, Drawable pressed, Drawable checked, @NonNull Drawable disable) {
        this.normalDrawable = normal;
        this.disableDrawable = disable;
        this.pressedDrawable = pressed;
        this.checkedDrawable = checked;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left + drawableShadowPadding[0], top + drawableShadowPadding[1],
                right - drawableShadowPadding[2], bottom - drawableShadowPadding[3]);
        setOutline(getBounds());
    }

    @Override
    public void setBounds(@NonNull Rect b) {
        b.set(b.left + drawableShadowPadding[0], b.top + drawableShadowPadding[1],
                b.right - drawableShadowPadding[2], b.bottom - drawableShadowPadding[3]);
        super.setBounds(b);
        setOutline(b);
    }

    private void setOutline(Rect b) {
        rectF.set(b.left + strokeWidth / 2f, b.top + strokeWidth / 2f, b.right - strokeWidth / 2f, b.bottom - strokeWidth / 2f);
        path.reset();
        path.addRoundRect(rectF, getCornerRadius(), Path.Direction.CW);
    }

    @Override
    public void draw(Canvas canvas) {
        if (shadowRadius > 0) { // draw shadow
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setShadowLayer(shadowRadius / 3f * 2, shadowOffsetX, shadowOffsetY, shadowColor);
            paint.setColor(shadowColor);
            canvas.drawPath(path, paint);
            paint.clearShadowLayer();
        }
        super.draw(canvas);
    }

    ExtendStateListDrawable setCornerRadius(float tlRadius, float trRadius, float blRadius, float brRadius) {
        this.tlRadius = tlRadius;
        this.trRadius = trRadius;
        this.blRadius = blRadius;
        this.brRadius = brRadius;
        return this;
    }

    ExtendStateListDrawable setStroke(int width, int dashWidth, int dashGap, int color, int pressedColor, int checkedColor, int disableColor) {
        this.strokeWidth = width;
        this.strokeDashWidth = dashWidth;
        this.strokeDashGap = dashGap;
        if (this.strokeDashGap == 0) this.strokeDashGap = dashWidth;
        this.strokeColor = color;
        this.strokePressedColor = pressedColor;
        this.strokeCheckedColor = checkedColor;
        this.strokeDisableColor = disableColor;
        return this;
    }

    ExtendStateListDrawable setShadow(@ColorInt int color, int radius, int offsetX, int offsetY) {
        this.shadowColor = color;
        this.shadowRadius = radius;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        calculatePadding();
        return this;
    }

    void apply() {
        normalDrawable = BackgroundBuilder.createCornerDrawable(normalDrawable, tlRadius, trRadius, blRadius, brRadius);
        if (normalDrawable instanceof ExtendBitmapDrawable) {
            int pressColor = Color.TRANSPARENT, checkedColor = Color.TRANSPARENT, disableColor = Color.TRANSPARENT;
            if (pressedDrawable instanceof ColorDrawable) {
                pressColor = ((ColorDrawable) pressedDrawable).getColor();
                pressedDrawable = null;
            }
            if (checkedDrawable instanceof ColorDrawable) {
                checkedColor = ((ColorDrawable) checkedDrawable).getColor();
                checkedDrawable = null;
            }
            if (disableDrawable instanceof ColorDrawable) {
                disableColor = ((ColorDrawable) disableDrawable).getColor();
                disableDrawable = null;
            }
            ((ExtendBitmapDrawable) normalDrawable)
                    .setStroke(strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor)
                    .setBackgroundStateColor(pressColor, checkedColor, disableColor)
                    .setStateEnable();
        } else {
            BackgroundBuilder.setDrawableStroke(normalDrawable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
        }
        pressedDrawable = BackgroundBuilder.createCornerDrawable(pressedDrawable, tlRadius, trRadius, blRadius, brRadius);
        BackgroundBuilder.setDrawableStroke(pressedDrawable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
        checkedDrawable = BackgroundBuilder.createCornerDrawable(checkedDrawable, tlRadius, trRadius, blRadius, brRadius);
        BackgroundBuilder.setDrawableStroke(checkedDrawable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
        disableDrawable = BackgroundBuilder.createCornerDrawable(disableDrawable, tlRadius, trRadius, blRadius, brRadius);
        BackgroundBuilder.setDrawableStroke(disableDrawable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);

        if (checkedDrawable != null) {
            addState(new int[]{-android.R.attr.state_checked, -android.R.attr.state_pressed, android.R.attr.state_enabled}, normalDrawable);
            addState(new int[]{android.R.attr.state_checked, android.R.attr.state_enabled}, checkedDrawable);
        }
        if (pressedDrawable != null) {
            addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressedDrawable);
        }
        addState(new int[]{-android.R.attr.state_enabled}, disableDrawable);
        addState(new int[]{}, normalDrawable);
    }

    /**
     * if the drawable has shadow or stroke, the view's content region should be fit the drawable's content region
     */
    void calculateAndSetPaddingForTargetView(View targetView) {
        if (targetView == null) return;
        int l, r, t, b;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            l = targetView.getPaddingStart() != 0 ? targetView.getPaddingStart() : targetView.getPaddingLeft();
        } else l = targetView.getPaddingLeft();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            r = targetView.getPaddingEnd() != 0 ? targetView.getPaddingEnd() : targetView.getPaddingRight();
        } else r = targetView.getPaddingRight();
        t = targetView.getPaddingTop();
        b = targetView.getPaddingBottom();
        l += drawableShadowPadding[0] + strokeWidth;
        t += drawableShadowPadding[1] + strokeWidth;
        r += drawableShadowPadding[2] + strokeWidth;
        b += drawableShadowPadding[3] + strokeWidth;
        targetView.setPadding(l, t, r, b);
    }

    /**
     * calculate the drawable padding with shadow size
     */
    private void calculatePadding() {
        drawableShadowPadding[0] = shadowRadius / 2 - shadowOffsetX;
        drawableShadowPadding[1] = shadowRadius / 2 - shadowOffsetY;
        drawableShadowPadding[2] = shadowRadius / 2 + shadowOffsetX;
        drawableShadowPadding[3] = shadowRadius / 2 + shadowOffsetY;
        if (drawableShadowPadding[0] < 0) drawableShadowPadding[0] = 0;
        if (drawableShadowPadding[1] < 0) drawableShadowPadding[1] = 0;
        if (drawableShadowPadding[2] < 0) drawableShadowPadding[2] = 0;
        if (drawableShadowPadding[3] < 0) drawableShadowPadding[3] = 0;
    }

    private float[] getCornerRadius() {
        return new float[]{tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius};
    }
}
