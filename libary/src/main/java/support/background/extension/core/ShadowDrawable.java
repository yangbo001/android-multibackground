package support.background.extension.core;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

interface ShadowDrawable {

    void compatDrawShadowLayer(Canvas canvas);

    int[] getShadowPadding();

    Drawable getDrawable();
}
