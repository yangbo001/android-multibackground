# android-multibackground
a background util that can easily create corner、stroke、shadow and the background state showing 
### SCREEN SHOT

<center>

<figure>
<img src="screenshot/screen-one.png" width="48%"/>
<img src="screenshot/screen-gif.gif" width="48%"/>
</figure>
</center>

preview real-time when editing layout file

<figure>
<img src="screenshot/screen-two.png" width="60%"/>
</figure>


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
