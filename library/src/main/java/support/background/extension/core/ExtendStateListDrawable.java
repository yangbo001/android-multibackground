package support.background.extension.core;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;

class ExtendStateListDrawable extends StateListDrawable {

    private Attrs attr;

    ExtendStateListDrawable(Attrs attr) {
        this.attr = attr;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        attr.setDrawableOriginBounds(left, top, right, bottom);
        super.setBounds(left + attr.shadowPadding[0], top + attr.shadowPadding[1], right - attr.shadowPadding[2], bottom - attr.shadowPadding[3]);
    }

    @Override
    public void setBounds(Rect bounds) {
        attr.setDrawableOriginBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
        bounds.set(bounds.left + attr.shadowPadding[0], bounds.top + attr.shadowPadding[1], bounds.right - attr.shadowPadding[2], bounds.bottom - attr.shadowPadding[3]);
        super.setBounds(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        if (attr.shadowRadius > 0) attr.compatDrawShadowLayer(canvas, getBounds());
        super.draw(canvas);
    }
}
