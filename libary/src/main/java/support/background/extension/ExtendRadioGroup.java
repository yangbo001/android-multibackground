package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import support.background.extension.core.BackgroundBuilder;

public class ExtendRadioGroup extends RadioGroup {

    public ExtendRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
