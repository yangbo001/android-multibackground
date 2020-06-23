package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

import support.background.extension.core.BackgroundBuilder;

public class ExtendRadioButton extends RadioButton {

    public ExtendRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
