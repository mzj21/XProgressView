# XProgressView
![Demo](https://github.com/mzj21/xprogressview/blob/master/screenshots/sample1.gif?raw=true)

### 简介
自定义进度条View。有开始，运行中，成功，错误，等待中5种状态。支持修改颜色，可自行添加图片资源，控制大小。

## 待添加
状态切换动画效果，更多的自定义

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
	    compile 'com.github.mzj21:XProgressView:1.1.5'
}
```

### 自定义属性
```
    <declare-styleable name="XProgressView">
        <!--圆环颜色-->
        <attr name="xpv_color_background_ring" format="reference" />
        <!--进度条颜色-->
        <attr name="xpv_color_progress" format="reference" />
        <!--完成颜色-->
        <attr name="xpv_color_complete" format="reference" />
        <!--√颜色-->
        <attr name="xpv_color_tick" format="reference" />
        <!--✖颜色-->
        <attr name="xpv_color_error" format="reference" />
        <!--等待图片资源-->
        <attr name="xpv_img_wait" format="reference" />
        <!--开始图片资源-->
        <attr name="xpv_img_play" format="reference" />
        <!--停止图片资源-->
        <attr name="xpv_img_stop" format="reference" />
        <!--完成图片资源-->
        <attr name="xpv_img_complete" format="reference" />
        <!--等待图片资源与整体显示比例,建议0.4-->
        <attr name="xpv_scale_wait" format="float" />
        <!--开始图片资源与整体显示比例,建议1.0-->
        <attr name="xpv_scale_play" format="float" />
        <!--停止图片资源与整体显示比例,建议0.4-->
        <attr name="xpv_scale_stop" format="float" />
        <!--完成图片资源与整体显示比例,建议0.4-->
        <attr name="xpv_scale_complete" format="float" />
        <!--状态-->
        <attr name="xpv_state" format="integer" />
    </declare-styleable>
```