# CameraExtend Android 
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
## 相机拍照 Camera to take pictures 
#### kotlin use
```
 openCamera(10086)  //直接拍照 Directly take photos
 
 or
 
 openCamera(
                10011,
		//使用压缩 use compressed
                compress = CameraCompress(
                    isCompress = true
                ),
		//使用裁剪 use crop
                crop = CameraCrop(
                    isCrop = true
                )
            )

```
