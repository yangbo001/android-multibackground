package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;


import support.background.extension.core.BackgroundBuilder;

public class ExtendButton extends Button {

    public ExtendButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
