# CameraExtend Kotlin  
## Android 相机拍照，相册选择 Camera to take pictures ,Photo album to choose 
基于PictureSelector扩展封装，使用方式更加便捷舒适,已封装了权限申请框架基于PermissionsDispatcher封装的AndroidPermission,调用拍照时会自动申请
### 集成
#### Step 1. Add the JitPack repository to your build file
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
#### Step 2. Add the dependency
```
implementation 'com.github.BugRui:CameraExtend:v1.0.3'
```
#### Step 3. Need to be in AndroidManifest.xml add permission
```
 <uses-permission android:name="android.permission.CAMERA" />
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> 
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
## 相机拍照 
#### kotlin use
```
 openCamera(10086)  //直接拍照 
 
 or
 
 openCamera(
        10086,
	//使用压缩 
        compress = CameraCompress(
            isCompress = true
        ),
	//使用裁剪
        crop = CameraCrop(
            isCrop = true
        )
 )

```
## 相册选择  GlideEngine 继承 ImageEngine实现，详细可参考demo
```
 openGallery(10086, engine = GlideEngine())
 
 or
 
 openGallery(
          10086,
	  //是否开启拍照按钮
          isCamera = true,
	  //列表图片加载器
          engine = GlideEngine(),
	  //相册主题
          cameraTheme = CameraTheme(
              theme = pictureCameraThemeWhite
          ),
	  //压缩
          compress = CameraCompress(
              isCompress = true
          ),
	  //裁剪
          crop = CameraCrop(
              isCrop = true
          )
)
 
```
## 主题可选参数
```
data class CameraTheme(
    /**
     * isWeChatStyle是否开启微信图片选择风格，此开关开启了才可使用微信主题
     */
    val isWeChatStyle: Boolean = false,
    /**
     * theme主题样式(不设置为默认样式)
     * pictureCameraThemeWhite 白色主题
     * pictureCameraThemeQQ QQ数字风格样式
     * pictureCameraThemeSina 新浪微博样式
     * pictureCameraThemeWeChat 仿微信样式
     *
     */
    val theme: Int = -1,
    /**
     * pictureStyle动态自定义相册主题
     * 注意：此方法最好不要与.theme();同时存在， 二选一
     */
    val pictureStyle: PictureParameterStyle? = null,
    /**
     * cropPictureStyle动态自定义裁剪主题
     * 注意：此方法最好不要与.theme();同时存在， 二选一
     */
    val cropPictureStyle: PictureCropParameterStyle? = null,
    /**
     * windowAnimationStyle自定义相册启动退出动画
     */
    val windowAnimationStyle: PictureWindowAnimationStyle? = null
)

```


## 压缩可选参数
```
data class CameraCompress(
    val isCompress: Boolean = true,             //否压缩
    val minimumCompressSize: Int = 100,         //小于100kb的图片不压缩
    val synOrAsy: Boolean = false,              //同步true或异步false 压缩 默认异步
    val compressSavePath: String? = null        //压缩图片保存地址
)
```
## 裁剪可选参数
```
data class CameraCrop(
    val isCrop: Boolean = false,            //是否开启裁剪
    val cutOutQuality: Int = 90,            //裁剪输出质量 默认100
    val cropWidth: Int = -1,                //裁剪宽度,与高度同时配置才有效
    val cropHeight: Int = -1,               //裁剪高度,与宽度同时配置才有效
    val aspect_ratio_x: Int = -1,           //裁剪比例x,裁剪比例 如16:9 3:2 3:4 1:1 可自定义,与y同时配置才有效
    val aspect_ratio_y: Int = -1,           //裁剪比例y,裁剪比例 如16:9 3:2 3:4 1:1 可自定义,与x同时配置才有效
    val circleDimmedLayer: Boolean = false, //是否圆形裁剪
    val showCropFrame: Boolean = true,      //是否显示裁剪矩形边框 圆形裁剪时设为false
    val showCropGrid: Boolean = true,       //是否显示裁剪矩形网格 圆形裁剪时设为false
    val rotateEnabled: Boolean = true,      //裁剪是否可旋转图片
    val isDragFrame: Boolean = true,        //是否可拖动裁剪框
    val scaleEnabled: Boolean = true,       //裁剪是否可放大缩小图片
    val hideBottomControls: Boolean = false,//是否显示uCrop工具栏，默认不显示
    val circleDimmedColor: Int = -1,        //设置圆形裁剪背景色值
    val circleDimmedBorderColor: Int = -1,  //设置圆形裁剪边框色值
    val CircleStrokeWidth: Int = 3          //设置圆形裁剪边框粗细,默认3
)

```



