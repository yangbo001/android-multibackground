package support.background.extension.core;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

class Attrs {
    int strokeWidth;
    int strokeDashWidth;
    int strokeDashGap;
    int strokeColor;
    int strokePressedColor;
    int strokeCheckedColor;
    int strokeDisableColor;

    int shadowColor;
    int shadowRadius;
    int shadowOffsetX;
    int shadowOffsetY;
    int[] shadowPadding = new int[4];
    float[] cornerRadii = new float[8];

    private Rect canvasBounds = new Rect();

    void setCornerRadius(float tlRadius, float trRadius, float blRadius, float brRadius) {
        cornerRadii = new float[]{tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius};
    }

    void setStroke(int width, int color, int pressedColor, int checkedColor, int disableColor, int dashWidth, int dashGap) {
        strokeWidth = width;
        strokeColor = color;
        strokePressedColor = pressedColor;
        strokeCheckedColor = checkedColor;
        strokeDisableColor = disableColor;
        strokeDashGap = dashGap;
        strokeDashWidth = dashWidth;
        if (strokeDashGap == 0) strokeDashGap = dashWidth;
    }

    void setShadow(int color, int radius, int offsetX, int offsetY) {
        shadowColor = color;
        shadowRadius = radius;
        shadowOffsetX = offsetX;
        shadowOffsetY = offsetY;
        calculatePadding();
    }

    void setDrawableOriginBounds(int left, int top, int right, int bottom) {
        canvasBounds.set(left, top, right, bottom);
    }

    void compatDrawShadowLayer(Canvas canvas, Rect bitmapBounds) {
        Path path = new Path();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // create shadow layer
            Bitmap bmp = Bitmap.createBitmap(canvasBounds.width(), canvasBounds.height(), Bitmap.Config.ARGB_8888);
            Canvas bmpCvs = new Canvas(bmp);
            paint.setMaskFilter(new BlurMaskFilter(shadowRadius / 3f * 2, BlurMaskFilter.Blur.NORMAL));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(shadowColor);
            path.addRoundRect(new RectF(
                    shadowRadius / 3f * 2,
                    shadowRadius / 3f * 2,
                    canvasBounds.right - shadowRadius / 3f * 2,
                    canvasBounds.bottom - shadowRadius / 3f * 2), cornerRadii, Path.Direction.CW);
            bmpCvs.drawPath(path, paint);
            // draw the shadow bitmap with the size of drawable rect and move to the right position
            Rect dstRect = new Rect(canvasBounds);
            if (shadowOffsetX != 0 || shadowOffsetY != 0) {
                int l = bitmapBounds.left - shadowRadius / 2 + shadowOffsetX;
                int t = bitmapBounds.top - shadowRadius / 2 + shadowOffsetY;
                int r = bitmapBounds.right + shadowRadius / 2 + shadowOffsetX;
                int b = bitmapBounds.bottom + shadowRadius / 2 + shadowOffsetY;
                dstRect.set(l, t, r, b);
            }
            canvas.drawBitmap(bmp, null, dstRect, null);
            bmp.recycle();
        } else {
            path.addRoundRect(new RectF(bitmapBounds.left + strokeWidth / 2f, bitmapBounds.top + strokeWidth / 2f,
                    bitmapBounds.right - strokeWidth / 2f, bitmapBounds.bottom - strokeWidth / 2f), cornerRadii, Path.Direction.CW);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setShadowLayer(shadowRadius / 3f * 2, shadowOffsetX, shadowOffsetY, shadowColor);
            paint.setColor(shadowColor);
            canvas.drawPath(path, paint);
            paint.clearShadowLayer();
        }
    }

    /**
     * calculate the drawable padding with shadow size
     */
    private void calculatePadding() {
        shadowPadding[0] = shadowRadius / 2 - shadowOffsetX;
        shadowPadding[1] = shadowRadius / 2 - shadowOffsetY;
        shadowPadding[2] = shadowRadius / 2 + shadowOffsetX;
        shadowPadding[3] = shadowRadius / 2 + shadowOffsetY;
        if (shadowPadding[0] < 0) shadowPadding[0] = 0;
        if (shadowPadding[1] < 0) shadowPadding[1] = 0;
        if (shadowPadding[2] < 0) shadowPadding[2] = 0;
        if (shadowPadding[3] < 0) shadowPadding[3] = 0;
    }
}
