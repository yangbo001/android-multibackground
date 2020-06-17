package support.background.extension;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import support.background.extension.core.BackgroundBuilder;

public class ExtendListView extends ListView {

    public ExtendListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BackgroundBuilder backgroundBuilder = new BackgroundBuilder();
        setBackground(backgroundBuilder.attributeFromView(this, attrs).buildDrawable());
    }
}
