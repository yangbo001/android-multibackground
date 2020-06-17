package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

import support.background.extension.core.BackgroundBuilder;

public class ExtendRadioButton extends AppCompatRadioButton {

    public ExtendRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
