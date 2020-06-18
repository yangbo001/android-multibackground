package support.background.extension.core;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;

// require Build.VERSION_CODES.LOLLIPOP
@SuppressLint("NewApi")
class ExtendRippleDrawable extends RippleDrawable {

    Path path = new Path();
    RectF rectF = new RectF();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int strokeWidth;
    private float[] cornerRadius = new float[]{0, 0, 0, 0, 0, 0, 0, 0};

    // shadow
    private int shadowColor, shadowRadius, shadowOffsetX, shadowOffsetY;
    private int[] drawableShadowPadding = new int[4];// left,top,right,bottom

    /**
     * Creates a new ripple drawable with the specified ripple color and
     * optional content and mask drawables.
     *
     * @param color   The ripple color
     * @param content The content drawable, may be {@code null}
     * @param mask    The mask drawable, may be {@code null}
     */
    public ExtendRippleDrawable(ColorStateList color, Drawable content, Drawable mask) {
        super(color, content, mask);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left + drawableShadowPadding[0], top + drawableShadowPadding[1],
                right - drawableShadowPadding[2], bottom - drawableShadowPadding[3]);
        setOutline(getBounds());
    }

    @Override
    public void setBounds(Rect b) {
        b.set(b.left + drawableShadowPadding[0], b.top + drawableShadowPadding[1],
                b.right - drawableShadowPadding[2], b.bottom - drawableShadowPadding[3]);
        super.setBounds(b);
        setOutline(b);
    }

    private void setOutline(Rect b) {
        rectF.set(b.left + strokeWidth / 2f, b.top + strokeWidth / 2f, b.right - strokeWidth / 2f, b.bottom - strokeWidth / 2f);
        path.reset();
        path.addRoundRect(rectF, cornerRadius, Path.Direction.CW);
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

    ExtendRippleDrawable setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    ExtendRippleDrawable setCornerRadius(float tlRadius, float trRadius, float blRadius, float brRadius) {
        cornerRadius = new float[]{tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius};
        return this;
    }

    ExtendRippleDrawable setShadow(int color, int radius, int offsetX, int offsetY) {
        this.shadowColor = color;
        this.shadowRadius = radius;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        calculatePadding();
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
