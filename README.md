# XProgressView
![Demo](https://github.com/mzj21/xprogressview/blob/master/screenshots/sample1.gif?raw=true)

### 简介
自定义进度条View。

### 使用
Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Step 2. Add the dependency
```
dependencies {
	    compile 'com.github.mzj21:XProgressView:1.0.1'
}
```
### 自定义属性
```
<declare-styleable name="XProgressView">
    <attr name="xpv_color_background_ring" format="reference" />
    <attr name="xpv_color_progress" format="reference" />
    <attr name="xpv_color_complete" format="reference" />
    <attr name="xpv_color_tick" format="reference" />
    <attr name="xpv_color_error" format="reference" />
</declare-styleable>
```