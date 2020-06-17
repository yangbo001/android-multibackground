package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import support.background.extension.core.BackgroundBuilder;

public class ExtendLinearLayout extends LinearLayout {

    public ExtendLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
