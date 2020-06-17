package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import support.background.extension.core.BackgroundBuilder;

public class ExtendFrameLayout extends RelativeLayout {

    public ExtendFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
