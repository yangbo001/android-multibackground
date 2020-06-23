package support.background.extension.core;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

class ExtendStateListDrawable extends StateListDrawable implements ShadowDrawable {

    Rect originDrawableRect = new Rect(); // the origin drawable rect
    Drawable normalDrawable, disableDrawable, pressedDrawable, checkedDrawable;

    private float[] cornerRadius = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
    // stroke
    private int strokeWidth, strokeDashWidth, strokeDashGap;
    private int strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor;
    // shadow
    private int shadowColor, shadowRadius, shadowOffsetX, shadowOffsetY;
    private int[] drawableShadowPadding = new int[4];// left,top,right,bottom

    ExtendStateListDrawable(Drawable normal, Drawable pressed, Drawable checked, Drawable disable) {
        this.normalDrawable = normal;
        this.disableDrawable = disable;
        this.pressedDrawable = pressed;
        this.checkedDrawable = checked;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        originDrawableRect.set(left, top, right, bottom);
        super.setBounds(left + drawableShadowPadding[0], top + drawableShadowPadding[1],
                right - drawableShadowPadding[2], bottom - drawableShadowPadding[3]);
    }

    @Override
    public void setBounds(Rect bounds) {
        originDrawableRect.set(bounds);
        bounds.set(bounds.left + drawableShadowPadding[0], bounds.top + drawableShadowPadding[1],
                bounds.right - drawableShadowPadding[2], bounds.bottom - drawableShadowPadding[3]);
        super.setBounds(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        if (shadowRadius > 0) compatDrawShadowLayer(canvas);
        super.draw(canvas);
    }

    @Override
    public void compatDrawShadowLayer(Canvas canvas) {
        Path path = new Path();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        Rect rect = getBounds();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // create shadow layer
            Bitmap bmp = Bitmap.createBitmap(originDrawableRect.width(), originDrawableRect.height(), Bitmap.Config.ARGB_8888);
            Canvas bmpCvs = new Canvas(bmp);
            paint.setMaskFilter(new BlurMaskFilter(shadowRadius / 3f * 2, BlurMaskFilter.Blur.NORMAL));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(shadowColor);
            path.addRoundRect(new RectF(
                    shadowRadius / 3f * 2,
                    shadowRadius / 3f * 2,
                    originDrawableRect.right - shadowRadius / 3f * 2,
                    originDrawableRect.bottom - shadowRadius / 3f * 2), cornerRadius, Path.Direction.CW);
            bmpCvs.drawPath(path, paint);
            // draw the shadow bitmap with the size of drawable rect and move to the right position
            Rect dstRect = new Rect(originDrawableRect);
            if (shadowOffsetX != 0 || shadowOffsetY != 0) {
                int l = rect.left - shadowRadius / 2 + shadowOffsetX;
                int t = rect.top - shadowRadius / 2 + shadowOffsetY;
                int r = rect.right + shadowRadius / 2 + shadowOffsetX;
                int b = rect.bottom + shadowRadius / 2 + shadowOffsetY;
                dstRect.set(l, t, r, b);
            }
            canvas.drawBitmap(bmp, null, dstRect, null);
            bmp.recycle();
        } else {
            path.addRoundRect(new RectF(rect.left + strokeWidth / 2f, rect.top + strokeWidth / 2f,
                    rect.right - strokeWidth / 2f, rect.bottom - strokeWidth / 2f), cornerRadius, Path.Direction.CW);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setShadowLayer(shadowRadius / 3f * 2, shadowOffsetX, shadowOffsetY, shadowColor);
            paint.setColor(shadowColor);
            canvas.drawPath(path, paint);
            paint.clearShadowLayer();
        }
    }

    @Override
    public int[] getShadowPadding() {
        return drawableShadowPadding;
    }

    @Override
    public Drawable getDrawable() {
        return this;
    }

    ExtendStateListDrawable setCornerRadius(float tlRadius, float trRadius, float blRadius, float brRadius) {
        cornerRadius = new float[]{tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius};
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

    ExtendStateListDrawable setShadow(int color, int radius, int offsetX, int offsetY) {
        this.shadowColor = color;
        this.shadowRadius = radius;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        calculatePadding();
        return this;
    }

    ExtendStateListDrawable apply() {
        normalDrawable = BackgroundBuilder.createCornerDrawable(normalDrawable, cornerRadius);
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
        pressedDrawable = BackgroundBuilder.createCornerDrawable(pressedDrawable, cornerRadius);
        BackgroundBuilder.setDrawableStroke(pressedDrawable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
        checkedDrawable = BackgroundBuilder.createCornerDrawable(checkedDrawable, cornerRadius);
        BackgroundBuilder.setDrawableStroke(checkedDrawable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
        disableDrawable = BackgroundBuilder.createCornerDrawable(disableDrawable, cornerRadius);
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
        return this;
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
}
