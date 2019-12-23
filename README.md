# CameraExtend  
## Android 相机拍照，相册选择 Camera to take pictures ,Photo album to choose 
基于PictureSelector扩展封装，使用方式更加便捷舒适

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
implementation 'com.github.BugRui:CameraExtend:v1.0.1'
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
## 相册选择 
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
