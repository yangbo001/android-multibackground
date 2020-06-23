package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import support.background.extension.core.BackgroundBuilder;

public class ExtendImageView extends ImageView {

    public ExtendImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
