# CameraExtend Android相机拍照，相册选择

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
#### Step 3. Need to be in AndroidManifest.xml add
```
 <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> 
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

