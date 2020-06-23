# android-multibackground
​		This library can easily apply round corner、stroke、shadow and different state effects to  background drawable. it do not update the view code directly, all the attrs are set to create the suitable drawable and applied to view by setbackground.

​		When we're working on large projects with multiple people, it is difficult to manage the drawable files. lot of times we created many duplicate files, maybe the small differences of ui or didn't check the drawable directories. this lib is to   improve this situation, you can apply the background attrs by setting layout xml or create drawable by java code. also you can define some global theme style  to apply to the different and common views

### SCREEN SHOT

<center>

<figure>
<img src="screenshot/screen-gif.gif" width="48%"/>
<img src="screenshot/screen-two.png" width="70%"/>
</figure>
</center>

## Usage

### Step 1
1. Add  repositories in your  **project build.gradle** file.
```groovy
buildscript {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
allprojects {
    repositories {
       ...
       maven { url "https://jitpack.io" }
    }
}
```
2. Add dependency
```groovy
dependencies {
  ...
	implementation 'com.github.yangbo001:android-multibackground:1.0.1'
}
```

### Step 2

**set corner**

```xml
<support.background.extension.*View
    ...
    app:background_corner_radius="4dp" />
<support.background.extension.*View
    ...
   android:background="@mipmap/background"           <!--the default background-->
   app:background_corner_radius_tl="4dp"             <!--top-left-->
   app:background_corner_radius_tr="4dp"             <!--top-right-->
   app:background_corner_radius_bl="4dp"             <!--bottom-left-->
   app:background_corner_radius_br="4dp"/>           <!--bottom-right-->
```
```java
  Drawable drawable = new BackgroundBuilder()
                .setBackground(Color.WHITE)
                .setCornerRadius(4)
//              .setCornerRadii(4, 4, 4, 4)
                .buildDrawable();
```

**set stroke**

```xml
<support.background.extension.*View
     ...
     android:background="@mipmap/background"                     <!--the default background-->
     app:background_stroke_color="@color/colorAccent"            <!--stroke color-->
     app:background_stroke_width="2dp"                           <!--stroke width-->
     app:background_stroke_pressed="@color/colorPrimary"         <!--stroke pressed color-->
     app:background_stroke_checked="@color/colorPrimaryDark"     <!--stroke checked color-->
     app:background_stroke_disable="@color/light_gray"/>         <!--stroke disabled color-->
 <support.background.extension.*View
     ...  
     app:background_stroke_dash_width="4dp"           <!--if dash_width large than zero, the stroke style is dashed-->
     app:background_stroke_dash_gap="4dp"/>           <!--the gap between dashed-->
```
```java
  Drawable drawable = new BackgroundBuilder()
                .setBackground(getResources().getDrawable(R.mipmap.background))    <!--set background-->
                .setStroke(2, Color.BLUE)                                          <!--stroke: width,color-->
//              .setStroke(2, Color.BLUE, 4, 4)                                    <!--stroke: width,color; dashed:width,gap-->
//              .setStroke(2, Color.BLACK, Color.GREEN, Color.BLUE, Color.GRAY)    <!--stroke: width,color; pressed-color,checked-color,disable-color-->
//              .setStroke(2, Color.BLACK, Color.GREEN, Color.BLUE, Color.GRAY, 4, 4) <!--stroke: width,color; pressed-color,checked-color,disable-color;dashed:width,gap-->
//              .buildDrawable();
```

**set shadow**

```xml
<support.background.extension.*View
     ...
     android:background="@mipmap/background"                     <!--the default background-->
     app:background_shadow_color="@color/gray"                   <!--shadow color-->
     app:background_shadow_radius="2dp"                          <!--shadow radius-->
     app:background_shadow_offset_x="2dp"                        <!--shadow offset of x-axs-->
     app:background_shadow_offset_y="-2dp"/>                     <!--shadow offset of y-axs-->
```
```java
   Drawable drawable = new BackgroundBuilder()
                .setBackground(getResources().getDrawable(R.mipmap.background))
                .setTargetView(targetView)                      // must set the target view to reset the view padding to fit the shadow region
                .setShadow(Color.GRAY, 6, 6, -6)
                .buildDrawable();
```

**set background state**

explain：the pressed background is mutually exclusive with pressed ripple animation
```xml
<support.background.extension.*View
     ...
     android:background="@mipmap/background"                     <!--the default background-->
     app:background_state_pressed="@mipmap/background"           <!--the pressed background-->
     app:background_state_checked="@color/colorPrimary"          <!--the checked background-->
     app:background_state_disable="@mipmap/background_gray"/>    <!--the view disabled background-->
```
```java
   Drawable drawable = new BackgroundBuilder()
                .setBackground(Color.RED)
                .setBackgroundPressed(Color.BLUE)
//              .setBackgroundPressed(getResources().getDrawable(R.mipmap.background))
//              .setBackgroundPressedRippleColor(Color.GREEN)
                .setBackgroundChecked(Color.GREEN)
//              .setBackgroundPressed(getResources().getDrawable(R.mipmap.background))
                .setBackgroundDisable(Color.GRAY)
//              .setBackgroundDisable(getResources().getDrawable(R.mipmap.background))
                .buildDrawable();
```

**special for text view**
explain：if the view is child of TextView, you can set the attr for compound like compound drawable size and drawable align to text; and the text color state

```xml
<support.background.extension.*TextView
     ...
     android:textColor="@color/black"                      <!--the default text color-->
     app:text_pressed_color="@color/green"                 <!--the pressed text color-->
     app:text_checked_color="@color/blue"                  <!--the checked text color-->
     app:text_disable_color="@color/gray"                  <!--the view disabled text color-->
     app:compound_drawable_width="20dp"                    <!--compound drawable width-->
     app:compound_drawable_height="20dp"                   <!--compound drawable height-->
     app:compound_drawable_align_to_text="true"/>          <!--compound drawable align to text-->
```
**theme style**

```xml
<resources>
 <style name="ButtonBlue">
        <item name="android:textColor">@android:color/white</item>
        <item name="text_pressed_color">@android:color/black</item>
        <item name="android:background">@android:color/holo_blue_light</item>
        <item name="background_state_pressed_ripple">@android:color/darker_gray</item>
    </style>
    <style name="ButtonBlue.Round_4dp">
        <item name="background_corner_radius">4dp</item>
    </style>
    <style name="ButtonBlue.Round_4dp.StrokeDarkBlue">
        <item name="background_stroke_width">1dp</item>
        <item name="background_stroke_color">@android:color/holo_blue_dark</item>
        <item name="background_stroke_pressed">@android:color/darker_gray</item>
    </style>
</resources>
    
<support.background.extension.ExtendButton
        android:layout_width="match_parent"
        android:layout_height="50dp"
        style="@style/ButtonBlue.Round_4dp.StrokeDarkBlue"/>
```
## Addition

the extension view is only implement the android normal view, if you want to use layout xml attr for your custom view, just extend the custom view and add the following lines to the constructor like this：
```java
    public ExtendFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // the key codes
        setBackground(new BackgroundBuilder().attributeFromView(this, attrs).buildDrawable());
    }
```