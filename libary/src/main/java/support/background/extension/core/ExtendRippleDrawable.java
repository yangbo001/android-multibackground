package support.background.extension.core;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

// require Build.VERSION_CODES.LOLLIPOP
@SuppressLint("NewApi")
class ExtendRippleDrawable extends RippleDrawable implements ShadowDrawable {

    private Builder.Attr attr;
    private Rect originDrawableRect = new Rect();

    ExtendRippleDrawable(Builder.Attr attr, ColorStateList color, Drawable content, Drawable mask) {
        super(color, content, mask);
        this.attr = attr;
    }


    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        originDrawableRect.set(left, top, right, bottom);
        super.setBounds(left + attr.shadowPadding[0], top + attr.shadowPadding[1],
                right - attr.shadowPadding[2], bottom - attr.shadowPadding[3]);
    }

    @Override
    public void setBounds(Rect bounds) {
        originDrawableRect.set(bounds);
        bounds.set(bounds.left + attr.shadowPadding[0], bounds.top + attr.shadowPadding[1],
                bounds.right - attr.shadowPadding[2], bounds.bottom - attr.shadowPadding[3]);
        super.setBounds(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        if (attr.shadowRadius > 0) compatDrawShadowLayer(canvas);
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
            paint.setMaskFilter(new BlurMaskFilter(attr.shadowRadius / 3f * 2, BlurMaskFilter.Blur.NORMAL));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(attr.shadowColor);
            path.addRoundRect(new RectF(
                    attr.shadowRadius / 3f * 2,
                    attr.shadowRadius / 3f * 2,
                    originDrawableRect.right - attr.shadowRadius / 3f * 2,
                    originDrawableRect.bottom - attr.shadowRadius / 3f * 2), attr.corners, Path.Direction.CW);
            bmpCvs.drawPath(path, paint);
            // draw the shadow bitmap with the size of drawable rect and move to the right position
            Rect dstRect = new Rect(originDrawableRect);
            if (attr.shadowOffsetX != 0 || attr.shadowOffsetY != 0) {
                int l = rect.left - attr.shadowRadius / 2 + attr.shadowOffsetX;
                int t = rect.top - attr.shadowRadius / 2 + attr.shadowOffsetY;
                int r = rect.right + attr.shadowRadius / 2 + attr.shadowOffsetX;
                int b = rect.bottom + attr.shadowRadius / 2 + attr.shadowOffsetY;
                dstRect.set(l, t, r, b);
            }
            canvas.drawBitmap(bmp, null, dstRect, null);
            bmp.recycle();
        } else {
            path.addRoundRect(new RectF(rect.left + attr.strokeWidth / 2f, rect.top + attr.strokeWidth / 2f,
                    rect.right - attr.strokeWidth / 2f, rect.bottom - attr.strokeWidth / 2f), attr.corners, Path.Direction.CW);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(attr.strokeWidth);
            paint.setShadowLayer(attr.shadowRadius / 3f * 2, attr.shadowOffsetX, attr.shadowOffsetY, attr.shadowColor);
            paint.setColor(attr.shadowColor);
            canvas.drawPath(path, paint);
            paint.clearShadowLayer();
        }
    }

    @Override
    public int[] getShadowPadding() {
        return attr.shadowPadding;
    }

    @Override
    public Drawable getDrawable() {
        return this;
    }


    static class Builder {
        private static class Attr {
            private int strokeWidth;
            private int strokeDashWidth;
            private int strokeDashGap;
            private int strokeColor;
            private int strokePressedColor;
            private int strokeCheckedColor;
            private int strokeDisableColor;

            private float[] corners = new float[]{0, 0, 0, 0, 0, 0, 0, 0};

            private int shadowColor, shadowRadius, shadowOffsetX, shadowOffsetY;
            private int[] shadowPadding = new int[4];
        }

        private Attr attr = new Attr();
        private Drawable background, backgroundChecked, backgroundDisable;
        private int backgroundPressedRipple; // the effect of ripple

        Builder setBackground(Drawable background, int backgroundPressedRipple, Drawable backgroundChecked, Drawable backgroundDisable) {
            this.background = background;
            this.backgroundPressedRipple = backgroundPressedRipple;
            this.backgroundDisable = backgroundDisable;
            this.backgroundChecked = backgroundChecked;
            return this;
        }

        Builder setStroke(int width, int dashWidth, int dashGap, int color, int pressedColor, int checkedColor, int disableColor) {
            attr.strokeWidth = width;
            attr.strokeDashWidth = dashWidth;
            attr.strokeDashGap = dashGap;
            if (attr.strokeDashGap == 0) attr.strokeDashGap = dashWidth;
            attr.strokeColor = color;
            attr.strokePressedColor = pressedColor;
            attr.strokeCheckedColor = checkedColor;
            attr.strokeDisableColor = disableColor;
            return this;
        }

        Builder setCornerRadius(float tlRadius, float trRadius, float blRadius, float brRadius) {
            attr.corners = new float[]{tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius};
            return this;
        }

        Builder setShadow(int color, int radius, int offsetX, int offsetY) {
            attr.shadowColor = color;
            attr.shadowRadius = radius;
            attr.shadowOffsetX = offsetX;
            attr.shadowOffsetY = offsetY;
            calculatePadding(attr);
            return this;
        }

        ExtendRippleDrawable apply() {
            background = BackgroundBuilder.createCornerDrawable(background, attr.corners);
            BackgroundBuilder.setDrawableStroke(background,
                    attr.strokeWidth, attr.strokeDashWidth, attr.strokeDashGap, attr.strokeColor, attr.strokePressedColor, attr.strokeCheckedColor, attr.strokeDisableColor);
            if (background instanceof ExtendBitmapDrawable && backgroundDisable instanceof ColorDrawable) {
                ((ExtendBitmapDrawable) background)
                        .setBackgroundStateColor(Color.TRANSPARENT, Color.TRANSPARENT, ((ColorDrawable) backgroundDisable).getColor())
                        .setStateEnable();
            }
            GradientDrawable mask = new GradientDrawable();
            mask.setColor(BackgroundBuilder.defaultDisableColor);
            mask.setCornerRadii(attr.corners);
            mask.setStroke(5, BackgroundBuilder.defaultDisableColor);
            int disableColor = BackgroundBuilder.defaultDisableColor;
            int checkedColor = backgroundPressedRipple;
            if (backgroundDisable != null && backgroundDisable instanceof ColorDrawable) {
                disableColor = ((ColorDrawable) backgroundDisable).getColor();
            }
            if (backgroundChecked != null && backgroundChecked instanceof ColorDrawable) {
                checkedColor = ((ColorDrawable) backgroundChecked).getColor();
            }
            return new ExtendRippleDrawable(attr,
                    BackgroundBuilder.createColorStateList(backgroundPressedRipple, backgroundPressedRipple, checkedColor, disableColor),
                    background, mask);
        }

        /**
         * calculate the drawable padding with shadow size
         */
        private void calculatePadding(Attr attr) {
            attr.shadowPadding[0] = attr.shadowRadius / 2 - attr.shadowOffsetX;
            attr.shadowPadding[1] = attr.shadowRadius / 2 - attr.shadowOffsetY;
            attr.shadowPadding[2] = attr.shadowRadius / 2 + attr.shadowOffsetX;
            attr.shadowPadding[3] = attr.shadowRadius / 2 + attr.shadowOffsetY;
            if (attr.shadowPadding[0] < 0) attr.shadowPadding[0] = 0;
            if (attr.shadowPadding[1] < 0) attr.shadowPadding[1] = 0;
            if (attr.shadowPadding[2] < 0) attr.shadowPadding[2] = 0;
            if (attr.shadowPadding[3] < 0) attr.shadowPadding[3] = 0;
        }
    }
}
