# CropVideoView
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![Download](https://img.shields.io/badge/latest%20version-0.0.1-green.svg)

This is a video library for android.

It has all scale types. Also user can change background of view

![](https://raw.githubusercontent.com/mishatron/CropVideoView/master/sample/src/main/res/drawable/screenshot1.png)

### Scale types
```
ScaleType {
    NONE,

    FIT_XY,
    FIT_START,
    FIT_CENTER,
    FIT_END,

    LEFT_TOP,
    LEFT_CENTER,
    LEFT_BOTTOM,
    CENTER_TOP,
    CENTER,
    CENTER_BOTTOM,
    RIGHT_TOP,
    RIGHT_CENTER,
    RIGHT_BOTTOM,

    LEFT_TOP_CROP,
    LEFT_CENTER_CROP,
    LEFT_BOTTOM_CROP,
    CENTER_TOP_CROP,
    CENTER_CROP,
    CENTER_BOTTOM_CROP,
    RIGHT_TOP_CROP,
    RIGHT_CENTER_CROP,
    RIGHT_BOTTOM_CROP,

    START_INSIDE,
    CENTER_INSIDE,
    END_INSIDE
}
```

## How to install


``` 
repositories {
    maven { url "https://mishatronic.bintray.com/cropvideoview" }
} 
```

``` 
dependencies {
    ...
    implementation 'mishatronic:cropvideoview:0.0.1'
} 
```
