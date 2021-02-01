# Kotlin CameraExtend   [![version](https://jitpack.io/v/BugRui/CameraExtend.svg)](https://jitpack.io/#BugRui/CameraExtend/1.1.2)
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
implementation 'com.github.BugRui:CameraExtend:1.1.3'
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
 openCamera(requestCode = 10086)  //直接拍照 
 
 or
 
 openCamera(
        requestCode = 10086,
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
 openGallery(requestCode = 10086, engine = GlideEngine())
 
 or
 
 openGallery(
          requestCode = 10086,
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

## 结果回调
```
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回五种path
                    // 1.media.getPath(); 原图path，但在Android Q版本上返回的是content:// Uri类型
                    // 2.media.getCutPath();裁剪后path，需判断media.isCut();切勿直接使用
                    // 3.media.getCompressPath();压缩后path，需判断media.isCompressed();切勿直接使用
                    // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                    // 5.media.getAndroidQToPath();Android Q版本特有返回的字段，但如果开启了压缩或裁剪还是取裁剪或压缩路
                       径；注意：.isAndroidQTransform(false);此字段将返回空
                    // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
                    for (LocalMedia media : selectList) {
                        Log.i(TAG, "压缩::" + media.getCompressPath());
                        Log.i(TAG, "原图::" + media.getPath());
                        Log.i(TAG, "裁剪::" + media.getCutPath());
                        Log.i(TAG, "是否开启原图::" + media.isOriginal());
                        Log.i(TAG, "原图路径::" + media.getOriginalPath());
                        Log.i(TAG, "Android Q 特有Path::" + media.getAndroidQToPath());
                        // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，
                        如旋转角度、经纬度等信息
                    }
                    break;
            }
        }
    }

```

## 新增结果回调方式
```
//相机
openCamera(resultListener = object : OnResultCallbackListener<LocalMedia>{
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            //返回结果
                        }

                        override fun onCancel() {
			    //取消
                        }
                    }
                )
		
//相册
openGallery(engine = GalleryImageEngine,
                    resultListener = object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
				//返回结果
                        }

                        override fun onCancel() {
				 //取消
                        }
                    }
                )

```

## 处理结果LocalMedia返回资源地址，（自己单独处理也行）
```
LocalMedia.getMediaPath

/**
 * 处理LocalMedia
 */
 val LocalMedia.getMediaPath: String
    get() {
        if (SdkVersionUtils.checkedAndroid_Q()) {
            return if (this.isCut && !this.isCompressed) {
                // 裁剪过
                this.cutPath
            } else if (this.isCompressed || this.isCut && this.isCompressed) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                this.compressPath
            } else if (!this.androidQToPath.isNullOrEmpty()) {
                //Android Q版本特有返回的字段，但如果开启了压缩或裁剪还是取裁剪或压缩路径；
                // 注意：.isAndroidQTransform(false);此字段将返回空
                this.androidQToPath
            } else {
                // 原图
                this.path
            }
        } else {
            return if (this.isCut && !this.isCompressed) {
                // 裁剪过
                this.cutPath
            } else if (this.isCompressed || this.isCut && this.isCompressed) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                this.compressPath
            } else {
                // 原图
                this.path
            }
        }
    }
    

```

如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩

LocalMedia  | 介绍
---|---
media.getPath() |  原图path，但在Android Q版本上返回的是content:// Uri类型
media.getCutPath()|裁剪后path，需判断media.isCut();切勿直接使用
media.getCompressPath()|压缩后path，需判断media.isCompressed();切勿直接使用
media.getOriginalPath()| media.isOriginal());为true时此字段才有值
media.getAndroidQToPath()|Android Q版本特有返回的字段，但如果开启了压缩或裁剪还是取裁剪或压缩路径；注意：.isAndroidQTransform(false);此字段将返回空,


## 拍照可选参数
```
  requestCode: Int,                       //requestCode
  chooseMode: Int = PictureMimeType.ofImage(), //选择模式，拍照、录像等
  cameraTheme: CameraTheme? = null,       //相册样式
  compress: CameraCompress? = null,       //压缩
  crop: CameraCrop? = null,               //裁剪
  isGif: Boolean = false,                 //是否显示gif图片
  language: Int = LanguageConfig.CHINESE  //设置语言，默认中文
  requestedOrientation: Int               //屏幕旋转方向

```

## 相册选择可选参数
```
 requestCode: Int,                       //requestCode
 chooseMode: Int = PictureMimeType.ofImage(), //选择模式，加载图片，视频等
 engine: ImageEngine,                    //图片加载框架
 isCamera: Boolean = false,              //是否显示拍照按钮
 maxSelectNum: Int = 1,                  //最大图片选择数量
 minSelectNum: Int = 1,                  //最小图片选择数量
 cameraTheme: CameraTheme? = null,       //相册样式
 isOriginalControl: Boolean = true,      //是否显示原图控制按钮，如果用户勾选了 压缩、裁剪功能将会失效
 compress: CameraCompress? = null,       //压缩
 crop: CameraCrop? = null,               //裁剪
 isGif: Boolean = false,                 //是否显示gif图片
 language: Int = LanguageConfig.CHINESE  //设置语言，默认中文
 requestedOrientation: Int               //屏幕旋转方向

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



