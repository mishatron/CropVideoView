# CropVideoView
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

This is a video library for android.

It has all scale types. Also user can change background of view.

![](https://raw.githubusercontent.com/mishatron/CropVideoView/master/sample/src/main/res/drawable/screenshot.gif)

### Scale types

# Support Scale Types  

## Scale to fit 
- fitXY
- fitStart
- fitCenter
- fitEnd

### No Scale
- leftTop
- leftCenter
- leftBottom
- centerTop
- center
- centerBottom
- rightTop
- rightCenter
- rightBottom

### Crop
- leftTopCrop
- leftCenterCrop
- leftBottomCrop
- centerTopCrop
- centerCrop
- centerBottomCrop
- rightTopCrop
- rightCenterCrop
- rightBottomCrop

### Scale Inside
- startInside
- centerInside
- endInside

## How to install

Add it in your root build.gradle at the end of repositories:
``` 
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency:
``` 
dependencies {
    ...
    implementation 'com.github.mishatron:CropVideoView:0.0.2'
} 
```
And then you can use it:
```
<video.lib.mishatronic.cropvideoview.CropVideoView
        android:id="@+id/video_view"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:scaleType="fitCenter"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>
```
