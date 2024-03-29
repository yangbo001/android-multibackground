package support.background.extension.core;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import support.background.extension.R;

/**
 * <b>mutable background builder</b><br/>
 * you have two ways to create drawable with corner、stoke、shadow、drawable state and so on.<br/>
 * 1: new {@link BackgroundBuilder} and set the attrs by invoke the function like {@link BackgroundBuilder#setCornerRadius}
 * and create the final drawable by invoke {@link BackgroundBuilder#buildDrawable}, then set the background drawable to target view.<br/>
 * 2: set the attrs when create xml layout files like  <b>app:xxx=""</b>
 * please see attrs.xml:{@link  R.styleable#background_extension}<br/><br/>
 *
 *
 * <b>background state: <br/>Tip: </b><em>the drawable state effect is base on background, you should set background by
 * <b>android:background=""</b>, all the drawable reference is better to be bitmap or color, the others may have no effect.</em><br/>
 * <font color="#00A5FF">background_state_pressed</font> :reference;<br/>
 * <font color="#00A5FF">background_state_pressed_ripple</font> :color; pressed animation is the ripple effect<br/>
 * <font color="#00A5FF">background_state_checked</font> :reference;<br/>
 * <font color="#00A5FF">background_state_disable</font> :reference;<br/><br/>
 *
 * <b>stroke:</b><br/>
 * <font color="#00A5FF">background_stroke_width</font> :dimension; if width > 0 create the stroke<br/>
 * <font color="#00A5FF">background_stroke_dash_width</font> :dimension; the length of the dashes, 0 to disable dashes <br/>
 * <font color="#00A5FF">background_stroke_dash_gap</font> :dimension; the gap between dashes <br/>
 * <font color="#00A5FF">background_stroke_color</font> :color; default stroke color<br/>
 * <font color="#00A5FF">background_stroke_pressed</font> :color; pressed stroke color<br/>
 * <font color="#00A5FF">background_stroke_checked</font> :color; checked stroke color<br/>
 * <font color="#00A5FF">background_stroke_disable</font> :color; disable stroke color<br/><br/>
 *
 * <b>corner:</b><br/>
 * <font color="#00A5FF">background_corner_radius</font> :dimension<br/>
 * <font color="#00A5FF">background_corner_radius_tl</font> :dimension; top left corner<br/>
 * <font color="#00A5FF">background_corner_radius_tr</font> :dimension; top right corner<br/>
 * <font color="#00A5FF">background_corner_radius_bl</font> :dimension; bottom left corner<br/>
 * <font color="#00A5FF">background_corner_radius_br</font> :dimension; bottom right corner<br/><br/>
 *
 * <b>shadow:</b><br/>
 * <font color="#00A5FF">background_shadow_color</font> :color<br/>
 * <font color="#00A5FF">background_shadow_radius</font> :dimension<br/>
 * <font color="#00A5FF">background_shadow_offset_x</font> :dimension<br/>
 * <font color="#00A5FF">background_shadow_offset_y</font> :dimension<br/><br/>
 *
 * <b>text view's style:</b><br/>
 * <font color="#00A5FF">text_pressed_color</font> :color<br/>
 * <font color="#00A5FF">text_checked_color</font> :color<br/>
 * <font color="#00A5FF">text_disable_color</font> :color<br/>
 * <font color="#00A5FF">compound_drawable_width</font> :dimension<br/>
 * <font color="#00A5FF">compound_drawable_height</font> :dimension<br/>
 * <font color="#00A5FF">compound_drawable_align_to_text</font> :bool<br/>
 */
@SuppressWarnings("unused")
public class BackgroundBuilder {
    static final int defaultDisableColor = Color.parseColor("#DDDDDD");

    private int strokeWidth;
    private int strokeDashWidth;
    private int strokeDashGap;
    private int strokeColor = Color.TRANSPARENT;
    private int strokePressedColor = Color.TRANSPARENT;
    private int strokeCheckedColor = Color.TRANSPARENT;
    private int strokeDisableColor = defaultDisableColor;

    private float cornerRadius;
    private float cornerTlRadius; // top-left
    private float cornerTrRadius; // top-right
    private float cornerBlRadius; // bottom-left
    private float cornerBrRadius; // bottom-right

    private Drawable background; // the view's background
    private Drawable backgroundPressed; // can only be bitmap or color
    private int backgroundPressedRipple = Color.TRANSPARENT; // the effect of ripple
    private Drawable backgroundChecked; // can only be bitmap or color
    private Drawable backgroundDisable = new ColorDrawable(defaultDisableColor);

    private int shadowRadius = 0;
    private int shadowOffsetX = 0;
    private int shadowOffsetY = 0;
    private int shadowColor = defaultDisableColor;

    private int textPressedColor;
    private int textCheckedColor;
    private int textDisableColor = defaultDisableColor;

    private int compoundDrawableWidth;
    private int compoundDrawableHeight;
    private boolean compoundDrawableAlignToText; // is the compound drawable align to text

    private WeakReference<View> targetViewReference;

    /**
     * create a drawable from the attr settings
     */
    public Drawable buildDrawable() {
        if (background == null) background = new ColorDrawable(Color.TRANSPARENT);
        Attrs attr = new Attrs();
        attr.setShadow(shadowColor, shadowRadius, shadowOffsetX, shadowOffsetY);
        attr.setCornerRadius(cornerTlRadius, cornerTrRadius, cornerBlRadius, cornerBrRadius);
        attr.setStroke(strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);

        Drawable targetDrawable = null;
        background = createCornerDrawable(background, attr.cornerRadii);
        // create ripple drawable
        if (backgroundPressedRipple != Color.TRANSPARENT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setDrawableStroke(background, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
            if (background instanceof ExtendBitmapDrawable && backgroundDisable instanceof ColorDrawable) {
                ((ExtendBitmapDrawable) background).setBackgroundStateColor(Color.TRANSPARENT, Color.TRANSPARENT, ((ColorDrawable) backgroundDisable).getColor());
                ((ExtendBitmapDrawable) background).setStateEnable();
            } else {
                // create disable drawable
                StateListDrawable stateListDrawable = new StateListDrawable();
                backgroundDisable = createCornerDrawable(backgroundDisable, attr.cornerRadii);
                setDrawableStroke(backgroundDisable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, backgroundDisable);
                stateListDrawable.addState(new int[]{}, background);
                background = stateListDrawable;
            }
            // create ripple mask
            GradientDrawable mask = new GradientDrawable();
            mask.setColor(BackgroundBuilder.defaultDisableColor);
            mask.setCornerRadii(attr.cornerRadii);
            mask.setStroke(5, BackgroundBuilder.defaultDisableColor);
            targetDrawable = new ShadowRippleDrawable(ColorStateList.valueOf(backgroundPressedRipple), background, mask, attr);
        }
        // create state list drawable
        if (targetDrawable == null) {
            if (background instanceof ExtendBitmapDrawable) {
                int pressColor = Color.TRANSPARENT, checkedColor = Color.TRANSPARENT, disableColor = Color.TRANSPARENT;
                if (backgroundPressed instanceof ColorDrawable) {
                    pressColor = ((ColorDrawable) backgroundPressed).getColor();
                    backgroundPressed = null;
                }
                if (backgroundChecked instanceof ColorDrawable) {
                    checkedColor = ((ColorDrawable) backgroundChecked).getColor();
                    backgroundChecked = null;
                }
                if (backgroundDisable instanceof ColorDrawable) {
                    disableColor = ((ColorDrawable) backgroundDisable).getColor();
                    backgroundDisable = null;
                }
                ((ExtendBitmapDrawable) background).setBackgroundStateColor(pressColor, checkedColor, disableColor);
                ((ExtendBitmapDrawable) background).setStateEnable();
            }
            setDrawableStroke(background, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
            backgroundPressed = createCornerDrawable(backgroundPressed, attr.cornerRadii);
            setDrawableStroke(backgroundPressed, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
            backgroundChecked = createCornerDrawable(backgroundChecked, attr.cornerRadii);
            setDrawableStroke(backgroundChecked, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
            backgroundDisable = createCornerDrawable(backgroundDisable, attr.cornerRadii);
            setDrawableStroke(backgroundDisable, strokeWidth, strokeDashWidth, strokeDashGap, strokeColor, strokePressedColor, strokeCheckedColor, strokeDisableColor);
            ShadowStateListDrawable stateListDrawable = new ShadowStateListDrawable(attr);
            if (backgroundChecked != null) {
                stateListDrawable.addState(new int[]{-android.R.attr.state_checked, -android.R.attr.state_pressed, android.R.attr.state_enabled}, background);
                stateListDrawable.addState(new int[]{android.R.attr.state_checked, android.R.attr.state_enabled}, backgroundChecked);
            }
            if (backgroundPressed != null) {
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, backgroundPressed);
            }
            if (backgroundDisable != null) stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, backgroundDisable);
            stateListDrawable.addState(new int[]{}, background);
            targetDrawable = stateListDrawable;
        }
        // try to resize the target view padding
        if (shadowRadius > 0 && targetViewReference != null && targetViewReference.get() != null) {
            calculateAndSetPaddingForTargetView(targetViewReference.get(), attr.shadowPadding);
            targetViewReference.clear();
        } else if (shadowRadius > 0) {
            Log.e("BackgroundBuilder", "the shadow effect is broken, please set target view by invoke BackgroundBuilder.setTargetView()");
        }
        return targetDrawable;
    }

    /**
     * set the background drawable<br/>
     * <em>only bitmapDrawable、colorDrawable、GradientDrawable can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackground(Drawable drawable) {
        background = drawable;
        return this;
    }

    /**
     * set the background color
     */
    public BackgroundBuilder setBackground(int colorInt) {
        background = new ColorDrawable(colorInt);
        return this;
    }

    /**
     * set the background drawable<br/>
     * <em>only bitmap、color、Gradient resource can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackground(Context ctx, int drawableResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            background = ctx.getDrawable(drawableResId);
        } else {
            background = ctx.getResources().getDrawable(drawableResId);
        }
        return this;
    }

    /**
     * set the background pressed showing<br/>
     * <em>only bitmapDrawable、colorDrawable、GradientDrawable can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackgroundPressed(Drawable drawable) {
        backgroundPressed = drawable;
        return this;
    }

    /**
     * set the background pressed showing
     */
    public BackgroundBuilder setBackgroundPressed(int colorInt) {
        backgroundPressed = new ColorDrawable(colorInt);
        return this;
    }

    /**
     * set the background pressed showing, the animation is ripple effect
     */
    public BackgroundBuilder setBackgroundPressedRippleColor(int colorInt) {
        backgroundPressedRipple = colorInt;
        return this;
    }

    /**
     * set the background pressed showing<br/>
     * <em>only bitmap、color、Gradient resource can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackgroundPressed(Context ctx, int drawableResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backgroundPressed = ctx.getDrawable(drawableResId);
        } else {
            backgroundPressed = ctx.getResources().getDrawable(drawableResId);
        }
        return this;
    }

    /**
     * set the background checked showing<br/>
     * <em>only bitmapDrawable、colorDrawable、GradientDrawable can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackgroundChecked(Drawable drawable) {
        backgroundChecked = drawable;
        return this;
    }

    /**
     * set the background checked showing
     */
    public BackgroundBuilder setBackgroundChecked(int colorInt) {
        backgroundChecked = new ColorDrawable(colorInt);
        return this;
    }

    /**
     * set the background checked showing<br/>
     * <em>only bitmap、color、Gradient resource can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackgroundChecked(Context ctx, int drawableResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backgroundChecked = ctx.getDrawable(drawableResId);
        } else {
            backgroundChecked = ctx.getResources().getDrawable(drawableResId);
        }
        return this;
    }

    /**
     * set the background disabled showing<br/>
     * <em>only bitmapDrawable、colorDrawable、GradientDrawable can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackgroundDisable(Drawable drawable) {
        backgroundDisable = drawable;
        return this;
    }

    /**
     * set the background disabled showing
     */
    public BackgroundBuilder setBackgroundDisable(int colorInt) {
        backgroundDisable = new ColorDrawable(colorInt);
        return this;
    }

    /**
     * set the background disabled showing<br/>
     * <em>only bitmap、color、Gradient resource can be applied the attr settings<em/>
     */
    public BackgroundBuilder setBackgroundDisable(Context ctx, int drawableResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backgroundDisable = ctx.getDrawable(drawableResId);
        } else {
            backgroundDisable = ctx.getResources().getDrawable(drawableResId);
        }
        return this;
    }

    /**
     * set the background round corner radius
     */
    public BackgroundBuilder setCornerRadius(float radius) {
        this.cornerRadius = radius;
        if (cornerRadius > 0) setCornerRadii(radius, radius, radius, radius);
        return this;
    }

    /**
     * set the background round corner radius
     */
    public BackgroundBuilder setCornerRadii(float tlRadius, float trRadius, float blRadius, float brRadius) {
        this.cornerTlRadius = tlRadius;
        this.cornerTrRadius = trRadius;
        this.cornerBlRadius = blRadius;
        this.cornerBrRadius = brRadius;
        return this;
    }

    /**
     * set the background outline stroke
     *
     * @param width    the stroke width
     * @param colorInt the stroke color
     */
    public BackgroundBuilder setStroke(int width, int colorInt) {
        this.strokeWidth = width;
        this.strokeColor = colorInt;
        return this;
    }

    /**
     * set the background outline stroke
     *
     * @param width     the stroke width
     * @param colorInt  the stroke color
     * @param dashWidth the length of the dashes
     * @param dashGap   the gap between dashes
     */
    public BackgroundBuilder setStroke(int width, int colorInt, int dashWidth, int dashGap) {
        this.strokeDashWidth = dashWidth;
        this.strokeDashGap = dashGap;
        return setStroke(width, colorInt);
    }

    /**
     * set the background outline stroke
     *
     * @param width           the stroke width
     * @param colorInt        the color width
     * @param pressedColorInt when view pressed, the stroke showing color
     * @param checkedColorInt when view checked, the stroke showing color
     * @param disableColorInt when view disable, the stroke showing color
     */
    public BackgroundBuilder setStroke(int width, int colorInt, int pressedColorInt, int checkedColorInt, int disableColorInt) {
        this.strokePressedColor = pressedColorInt;
        this.strokeCheckedColor = checkedColorInt;
        this.strokeDisableColor = disableColorInt;
        return setStroke(width, colorInt);
    }

    /**
     * set the background outline stroke
     *
     * @param width     the stroke width
     * @param colorInt  the stroke color
     * @param pressed   when view pressed, the stroke showing color
     * @param checked   when view checked, the stroke showing color
     * @param disable   when view disable, the stroke showing color
     * @param dashWidth the length of the dashes
     * @param dashGap   the gap between dashes
     */
    public BackgroundBuilder setStroke(int width, int colorInt, int pressed, int checked, int disable, int dashWidth, int dashGap) {
        setStroke(width, colorInt, pressed, checked, disable);
        return setStroke(width, colorInt, dashWidth, dashGap);
    }

    /**
     * set the background shadow effect<br/> <em>you must invoke {@link BackgroundBuilder#setTargetView(View)} before or after to
     * calculate the right drawing area.<em/>
     */
    public BackgroundBuilder setShadow(int colorInt, int radius, int offsetX, int offsetY) {
        this.shadowColor = colorInt;
        this.shadowRadius = radius;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        return this;
    }

    /**
     * set the view that the background will be set to, if the background has shadow then the target view need recalculate
     * padding to adapt shadow drawing area.
     */
    public BackgroundBuilder setTargetView(View targetView) {
        targetViewReference = new WeakReference<>(targetView);
        return this;
    }

    /**
     * load the attrs from layout setting
     */
    public BackgroundBuilder attributeFromView(View targetView, AttributeSet attrs) {
        setTargetView(targetView);
        background = targetView.getBackground();
        if (background == null) background = new ColorDrawable(Color.TRANSPARENT);
        if (attrs != null) {
            TypedArray a = targetView.getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.background_extension, 0, 0);
            cornerRadius = a.getDimension(R.styleable.background_extension_background_corner_radius, 0);
            if (cornerRadius != 0) {
                cornerTlRadius = cornerTrRadius = cornerBlRadius = cornerBrRadius = cornerRadius;
            } else {
                cornerTlRadius = a.getDimension(R.styleable.background_extension_background_corner_radius_tl, 0);
                cornerTrRadius = a.getDimension(R.styleable.background_extension_background_corner_radius_tr, 0);
                cornerBlRadius = a.getDimension(R.styleable.background_extension_background_corner_radius_bl, 0);
                cornerBrRadius = a.getDimension(R.styleable.background_extension_background_corner_radius_br, 0);
            }
            strokeWidth = (int) a.getDimension(R.styleable.background_extension_background_stroke_width, 0);
            strokeDashWidth = (int) a.getDimension(R.styleable.background_extension_background_stroke_dash_width, 0);
            strokeDashGap = (int) a.getDimension(R.styleable.background_extension_background_stroke_dash_gap, 0);
            strokeColor = a.getColor(R.styleable.background_extension_background_stroke_color, Color.TRANSPARENT);
            strokePressedColor = a.getColor(R.styleable.background_extension_background_stroke_pressed, strokeColor);
            strokeCheckedColor = a.getColor(R.styleable.background_extension_background_stroke_checked, strokeColor);
            strokeDisableColor = a.getColor(R.styleable.background_extension_background_stroke_disable, defaultDisableColor);
            backgroundPressed = a.getDrawable(R.styleable.background_extension_background_state_pressed);
            backgroundPressedRipple = a.getColor(R.styleable.background_extension_background_state_pressed_ripple, Color.TRANSPARENT);
            backgroundChecked = a.getDrawable(R.styleable.background_extension_background_state_checked);
            backgroundDisable = a.getDrawable(R.styleable.background_extension_background_state_disable);
            if (backgroundDisable == null) backgroundDisable = new ColorDrawable(defaultDisableColor);
            textPressedColor = a.getColor(R.styleable.background_extension_text_pressed_color, Color.TRANSPARENT);
            textCheckedColor = a.getColor(R.styleable.background_extension_text_checked_color, Color.TRANSPARENT);
            textDisableColor = a.getColor(R.styleable.background_extension_text_disable_color, defaultDisableColor);
            compoundDrawableWidth = (int) a.getDimension(R.styleable.background_extension_compound_drawable_width, 0);
            compoundDrawableHeight = (int) a.getDimension(R.styleable.background_extension_compound_drawable_height, 0);
            compoundDrawableAlignToText = a.getBoolean(R.styleable.background_extension_compound_drawable_align_to_text, false);
            shadowColor = a.getColor(R.styleable.background_extension_background_shadow_color, defaultDisableColor);
            shadowRadius = (int) a.getDimension(R.styleable.background_extension_background_shadow_radius, 0);
            shadowOffsetY = (int) a.getDimension(R.styleable.background_extension_background_shadow_offset_y, 0);
            shadowOffsetX = (int) a.getDimension(R.styleable.background_extension_background_shadow_offset_x, 0);
            a.recycle();
        }
        if (targetView instanceof TextView) fitTextView((TextView) targetView);
        return this;
    }

    /**
     * if the target is textView or it's child, then apply the text style to this view<br/>
     */
    private void fitTextView(final TextView targetView) {
        // set color state list
        if (textPressedColor != Color.TRANSPARENT || textCheckedColor != Color.TRANSPARENT) {
            int txtColor = targetView.getCurrentTextColor();
            if (textPressedColor == 0) textPressedColor = txtColor;
            if (textCheckedColor == 0) textCheckedColor = textPressedColor;
            targetView.setTextColor(createColorStateList(txtColor, textPressedColor, textCheckedColor, textDisableColor));
        }
        // resize compound drawables
        boolean isRelative = false;
        Drawable[] drawables = targetView.getCompoundDrawables();
        if (drawables[0] == null && drawables[2] == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            drawables = targetView.getCompoundDrawablesRelative();
            if (drawables[0] != null || drawables[2] != null) isRelative = true;
        }
        for (Drawable drawable : drawables) {
            if (drawable != null) drawable.setBounds(0, 0, compoundDrawableWidth, compoundDrawableHeight);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isRelative) {
            targetView.setCompoundDrawablesRelative(drawables[0], drawables[1], drawables[2], drawables[3]);
        } else targetView.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);

        // set compound drawables's gravity
        if (!compoundDrawableAlignToText) return;
        targetView.addTextChangedListener(new TextWatcher() {
            Rect rect = new Rect();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                float twoDip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, targetView.getResources().getDisplayMetrics());
                targetView.getPaint().getTextBounds(s.toString(), 0, s.length(), rect);
                int width = (int) (rect.width() + twoDip);
                rect.setEmpty();
                Drawable[] drawables = targetView.getCompoundDrawables();
                if (drawables[0] == null && drawables[2] == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    drawables = targetView.getCompoundDrawablesRelative();
                }
                if (drawables[0] != null) {
                    width = width + drawables[0].getBounds().width() + targetView.getCompoundDrawablePadding();
                }
                if (drawables[2] != null) {
                    width = width + drawables[2].getBounds().width() + targetView.getCompoundDrawablePadding();
                }
                int grav = targetView.getGravity() & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
                if (grav == Gravity.CENTER_HORIZONTAL) {
                    int padding = (targetView.getWidth() - width) / 2;
                    targetView.setPadding(padding, targetView.getPaddingTop(), padding, targetView.getPaddingBottom());
                } else if (grav == Gravity.START || grav == Gravity.LEFT) {
                    width = width + targetView.getPaddingLeft();
                    targetView.setPadding(targetView.getPaddingLeft(), targetView.getPaddingTop(), targetView.getWidth() - width, targetView.getPaddingBottom());
                } else if (grav == Gravity.END || grav == Gravity.RIGHT) {
                    width = width + targetView.getPaddingRight();
                    targetView.setPadding(targetView.getWidth() - width, targetView.getPaddingTop(), targetView.getPaddingRight(), targetView.getPaddingBottom());
                }
            }
        });
        targetView.post(new Runnable() {
            @Override
            public void run() {
                targetView.setText(targetView.getText());
            }
        });
    }

    /**
     * if the drawable has shadow or stroke, the view's content region should be fit the drawable's content region
     *
     * @param drawableShadowPadding left,top,right,bottom
     */
    private void calculateAndSetPaddingForTargetView(View targetView, int[] drawableShadowPadding) {
        if (targetView == null) return;
        int l, r, t, b;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            l = targetView.getPaddingStart() != 0 ? targetView.getPaddingStart() : targetView.getPaddingLeft();
        } else l = targetView.getPaddingLeft();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            r = targetView.getPaddingEnd() != 0 ? targetView.getPaddingEnd() : targetView.getPaddingRight();
        } else r = targetView.getPaddingRight();
        t = targetView.getPaddingTop();
        b = targetView.getPaddingBottom();
        l += drawableShadowPadding[0] + strokeWidth;
        t += drawableShadowPadding[1] + strokeWidth;
        r += drawableShadowPadding[2] + strokeWidth;
        b += drawableShadowPadding[3] + strokeWidth;
        targetView.setPadding(l, t, r, b);
    }

    /**
     * create cornerDrawable , can only dispose BitmapDrawable、ColorDrawable、GradientDrawable<br/>
     * <em>if src is BitmapDrawable then convert it to ExtendBitmapDrawable; else if is GradientDrawable then convert to ShadowGradientDrawable;</em> <br/>
     */
    private Drawable createCornerDrawable(Drawable src, float[] radii) {
        if (src == null) return null;
        // convert src drawable to fitted drawable
        if (src instanceof BitmapDrawable) {
            if (!(src instanceof ExtendBitmapDrawable)) {
                src = new ExtendBitmapDrawable().loadAttrFrom((BitmapDrawable) src);
            }
        } else if (src instanceof ColorDrawable) {
            int color = ((ColorDrawable) src).getColor();
            src = new GradientDrawable();
            ((GradientDrawable) src).setColor(color);
        }

        if (radii == null || radii.length != 8) return src;
        boolean hasRoundCorner = false;
        for (float r : radii) {
            if (r > 0) {
                hasRoundCorner = true;
                break;
            }
        }
        if (!hasRoundCorner) return src;

        if (src instanceof GradientDrawable) {
            ((GradientDrawable) src).setCornerRadii(radii);
        } else if (src instanceof ExtendBitmapDrawable) {
            ((ExtendBitmapDrawable) src).setCornerRadius(radii);
        }
        return src;
    }

    private void setDrawableStroke(Drawable src, int width, int dasWidth, int dashGap, int color, int pressColor, int checkedColor, int disableColor) {
        if (width <= 0) return;
        if (src instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) src;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (dasWidth > 0) {
                    drawable.setStroke(width, createColorStateList(color, pressColor, checkedColor, disableColor), dasWidth, dashGap);
                } else drawable.setStroke(width, createColorStateList(color, pressColor, checkedColor, disableColor));
            } else {
                if (dasWidth > 0) {
                    drawable.setStroke(width, color, dasWidth, dashGap);
                } else drawable.setStroke(width, color);
            }
        } else if (src instanceof ExtendBitmapDrawable) {
            ((ExtendBitmapDrawable) src).setStroke(width, dasWidth, dashGap, color, pressColor, checkedColor, disableColor);
        }
    }

    private ColorStateList createColorStateList(int normal, int pressed, int checked, int disable) {
        int[] colors = new int[]{normal, checked, pressed, disable, normal};
        int[][] states = new int[5][];
        states[0] = new int[]{-android.R.attr.state_checked, -android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_checked, android.R.attr.state_enabled};
        states[2] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[3] = new int[]{-android.R.attr.state_enabled};
        states[4] = new int[]{};
        return new ColorStateList(states, colors);
    }
}
