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
## GlideEngine 需要集成ImageEngine
```
class GlideEngine : ImageEngine {

    /**
     * 加载图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        // * other https://www.jianshu.com/p/28f5bcee409f
        val drawableCrossFadeFactory =
            DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
            .into(imageView)
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     */
    override fun loadImage(
        context: Context, url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : ImageViewTarget<Bitmap>(imageView) {
                override fun setResource(resource: Bitmap?) {
                    if (resource != null) {
                        val eqLongImage = MediaUtils.isLongImg(
                            resource.width,
                            resource.height
                        )
                        longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                        imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                        if (eqLongImage) {
                            // 加载长图
                            longImageView.isQuickScaleEnabled = true
                            longImageView.isZoomEnabled = true
                            longImageView.isPanEnabled = true
                            longImageView.setDoubleTapZoomDuration(100)
                            longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                            longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                            longImageView.setImage(
                                ImageSource.bitmap(resource),
                                ImageViewState(0f, PointF(0f, 0f), 0)
                            )
                        } else {
                            // 普通图片
                            imageView.setImageBitmap(resource)
                        }
                    }
                }
            })
    }

    /**
     * 加载相册目录
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        // * other https://www.jianshu.com/p/28f5bcee409f
        val drawableCrossFadeFactory =
            DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .centerCrop()
            .sizeMultiplier(0.5f)
            .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
            .transition(BitmapTransitionOptions.withCrossFade(drawableCrossFadeFactory))
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.resources, resource)
                    circularBitmapDrawable.cornerRadius = 8f
                    imageView.setImageDrawable(circularBitmapDrawable)
                }
            })
    }


    /**
     * 加载gif
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAsGifImage(
        context: Context, url: String,
        imageView: ImageView
    ) {
        Glide.with(context)
            .asGif()
            .load(url)
            .into(imageView)
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        // * other https://www.jianshu.com/p/28f5bcee409f
        val drawableCrossFadeFactory =
            DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
            .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
            .into(imageView)
    }

    companion object {

        private var instance: GlideEngine? = null

        fun createGlideEngine(): GlideEngine {
            if (null == instance) {
                synchronized(GlideEngine::class.java) {
                    if (null == instance) {
                        instance = GlideEngine()
                    }
                }
            }
            return instance!!
        }
    }
}

```

