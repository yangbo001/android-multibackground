package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import support.background.extension.core.BackgroundBuilder;

public class ExtendButton extends AppCompatButton {

    public ExtendButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
