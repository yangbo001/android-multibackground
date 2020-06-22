# android-multibackground
a background util that can easily create corner、stroke、shadow and the background state showing 
### SCREEN SHOT

<center>

<figure>
<img src="screenshot/screen-gif.gif" width="48%"/>
<img src="screenshot/screen-two.png" width="48%"/>
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
	implementation 'com.github.yangbo001:android-multibackground:1.0.0'
}
```

### Step 2

**set corner**

1、by layout xml
```xml
<support.background.extension.*View
    ...
    app:background_corner_radius="4dp" />
<support.background.extension.*View
    ...
   app:background_corner_radius=""               
   app:background_corner_radius_tl="4dp"         
   app:background_corner_radius_tr="4dp"         
   app:background_corner_radius_bl="4dp"            
   app:background_corner_radius_br="4dp"/>
```

2、by java code
```java
Drawable drawable = new BackgroundBuilder()
        .setBackground(Color.WHITE)
        .setCornerRadius(4)
//      .setCornerRadii(4, 4, 4, 4)
        .buildDrawable();
```

**set stroke**

```xml
<support.background.extension.*View
     ...
     app:background_stroke_color="@color/colorAccent" <!--stroke color-->
     app:background_stroke_width="2dp" />             <!--stroke width-->
 <support.background.extension.*View
     ...  
     app:background_stroke_dash_width="4dp"           <!--if dash_width large than zero, the stroke style is dashed-->
     app:background_stroke_dash_gap="4dp"/>           <!--the gap between dashed-->
```