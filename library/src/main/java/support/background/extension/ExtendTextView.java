package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import support.background.extension.core.BackgroundBuilder;

public class ExtendTextView extends TextView {

    public ExtendTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
