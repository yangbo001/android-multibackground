package support.background.extension.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

class ShadowStateListDrawable extends StateListDrawable {

    private Attrs attr;
    private Rect canvasBounds = new Rect();

    ShadowStateListDrawable(Attrs attr) {
        this.attr = attr;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        canvasBounds.set(left, top, right, bottom);
        super.setBounds(left + attr.shadowPadding[0], top + attr.shadowPadding[1], right - attr.shadowPadding[2], bottom - attr.shadowPadding[3]);
    }

    @Override
    public void setBounds(Rect bounds) {
        canvasBounds.set(bounds);
        bounds.set(bounds.left + attr.shadowPadding[0], bounds.top + attr.shadowPadding[1], bounds.right - attr.shadowPadding[2], bounds.bottom - attr.shadowPadding[3]);
        super.setBounds(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        if (attr.shadowRadius > 0) compatDrawShadowLayer(canvas);
        super.draw(canvas);
    }


    private void compatDrawShadowLayer(Canvas canvas) {
        Rect rect = getBounds();
        Path path = new Path();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(attr.strokeWidth);
        paint.setShadowLayer(attr.shadowRadius / 3f * 2, attr.shadowOffsetX, attr.shadowOffsetY, attr.shadowColor);
        paint.setColor(attr.shadowColor);
        path.addRoundRect(new RectF(rect.left + attr.strokeWidth / 2f, rect.top + attr.strokeWidth / 2f,
                rect.right - attr.strokeWidth / 2f, rect.bottom - attr.strokeWidth / 2f), attr.cornerRadii, Path.Direction.CW);
        // because the view started the hardware speedup, the func setShadowLayer() cannot work well by set directly;
        // luckily we can use software by create a bitmap and draw on it's canvas, this issue fixed by version 28
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Bitmap bmp = Bitmap.createBitmap(canvasBounds.width(), canvasBounds.height(), Bitmap.Config.ARGB_8888);
            Canvas bmpCvs = new Canvas(bmp);
            bmpCvs.drawPath(path, paint);
            canvas.drawBitmap(bmp, null, canvasBounds, null);
            bmp.recycle();
        } else {
            canvas.drawPath(path, paint);
        }
    }
}
