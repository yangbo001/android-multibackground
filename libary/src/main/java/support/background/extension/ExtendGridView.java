package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import support.background.extension.core.BackgroundBuilder;

public class ExtendGridView extends GridView {

    public ExtendGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
