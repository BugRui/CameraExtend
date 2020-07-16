package com.bugrui.cameralibrary

import android.content.Intent
import android.content.pm.ActivityInfo
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bugrui.permission.OnPermissionsTaskListener
import com.bugrui.permission.permissionCheck
import com.luck.picture.lib.PictureSelectionModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureCropParameterStyle
import com.luck.picture.lib.style.PictureParameterStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.luck.picture.lib.tools.PictureFileUtils
import com.luck.picture.lib.tools.SdkVersionUtils
import java.util.*

/**
 * @Author:            BugRui
 * @CreateDate:        2019/12/19 17:43
 * @Description:       相机扩展方法
 */
/**
 * 相机权限,sdk卡权限
 */
private val cameraAndStoragePermissions = arrayOf(
    "android.permission.CAMERA",
    "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.WRITE_EXTERNAL_STORAGE"
)
private val storagePermissions = arrayOf(
    "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.WRITE_EXTERNAL_STORAGE"
)

const val pictureCameraThemeWhite = 1
const val pictureCameraThemeQQ = 2
const val pictureCameraThemeSina = 3
const val pictureCameraThemeWeChat = 4

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
     * 其他自定义样式请直接传入R.style.xxxxxx
     */
    val theme: Int = -1,
    /**
     * pictureStyle动态自定义相册主题
     * 注意：此方法最好不要与.theme();同时存在， 二选一
     */
    val pictureStyle: PictureParameterStyle? = null,
    /**
     * cropPictureStyle动态自定义裁剪主题
     */
    val cropPictureStyle: PictureCropParameterStyle? = null,
    /**
     * windowAnimationStyle自定义相册启动退出动画
     */
    val windowAnimationStyle: PictureWindowAnimationStyle? = null
)

/**
 * 设置主题样式
 */
fun PictureSelectionModel.setCameraTheme(cameraTheme: CameraTheme? = null): PictureSelectionModel {
    cameraTheme?.let {
        this.isWeChatStyle(it.isWeChatStyle)
        if (it.theme != -1) {
            this.theme(
                when (it.theme) {
                    pictureCameraThemeWhite -> R.style.picture_white_style
                    pictureCameraThemeQQ -> R.style.picture_QQ_style
                    pictureCameraThemeSina -> R.style.picture_Sina_style
                    pictureCameraThemeWeChat -> R.style.picture_WeChat_style
                    else -> it.theme
                }
            )
        }
        if (it.pictureStyle != null) {
            this.setPictureStyle(it.pictureStyle)
        }
        if (it.cropPictureStyle != null) {
            this.setPictureCropStyle(it.cropPictureStyle)
        }
        if (it.windowAnimationStyle != null) {
            this.setPictureWindowAnimationStyle(it.windowAnimationStyle)
        }
    }
    return this
}

data class CameraCompress(
    val isCompress: Boolean = true,             //否压缩
    val minimumCompressSize: Int = 100,         //小于100kb的图片不压缩
    val synOrAsy: Boolean = false,              //同步true或异步false 压缩 默认异步
    val compressSavePath: String? = null        //压缩图片保存地址
)

/**
 * 压缩
 */
fun PictureSelectionModel.setCompress(compress: CameraCompress? = null): PictureSelectionModel {
    compress?.let {
        this.isCompress(it.isCompress)
            .minimumCompressSize(it.minimumCompressSize)
            .synOrAsy(it.synOrAsy)
        if (!TextUtils.isEmpty(it.compressSavePath)) {
            this.compressSavePath(it.compressSavePath)
        }
    }
    return this
}

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


/**
 * 裁剪
 */
fun PictureSelectionModel.setCrop(crop: CameraCrop? = null): PictureSelectionModel {
    crop?.let {
        this.isEnableCrop(it.isCrop)

        //裁剪输出质量
        this.cutOutQuality(it.cutOutQuality)

        //设置圆形裁剪背景色值
        if (it.circleDimmedColor != -1) {
            this.setCircleDimmedColor(it.circleDimmedColor)
        }
        //设置圆形裁剪边框色值
        if (it.circleDimmedBorderColor != -1) {
            this.setCircleDimmedBorderColor(it.circleDimmedBorderColor)
        }
        //设置圆形裁剪边框粗细
        this.setCircleStrokeWidth(it.CircleStrokeWidth)

        //裁剪是否可旋转图片
        this.rotateEnabled(it.rotateEnabled)

        //是否可拖动裁剪框
        this.isDragFrame(it.isDragFrame)
        this.freeStyleCropEnabled(it.isDragFrame)

        //是否圆形裁剪
        this.circleDimmedLayer(it.circleDimmedLayer)

        //裁剪是否可以
        this.scaleEnabled(it.scaleEnabled)


        if (it.circleDimmedLayer) {
            //是否显示裁剪矩形边框 圆形裁剪时设为false
            this.showCropFrame(false)

            //是否显示裁剪矩形网格 圆形裁剪时设为false
            this.showCropGrid(false)
        } else {
            //是否显示裁剪矩形边框
            this.showCropFrame(it.showCropFrame)

            //是否显示裁剪矩形网格
            this.showCropGrid(it.showCropGrid)
        }


        //是否显示uCrop工具栏，默认不显示
        this.hideBottomControls(it.hideBottomControls)

        //裁剪是否可放大缩小图片
        if (it.cropWidth != -1 && it.cropHeight != -1) {
            this.cropImageWideHigh(it.cropWidth, it.cropHeight)
        }
        //裁剪比例 如16:9 3:2 3:4 1:1 可自定义
        if (it.aspect_ratio_x != -1 && it.aspect_ratio_y != -1) {
            this.withAspectRatio(it.aspect_ratio_x, it.aspect_ratio_y)
        }

    }
    return this
}

private fun PictureSelector.createGalleryPictureSelectionModel(
    chooseMode: Int = PictureMimeType.ofImage(),//拍照or录视频
    cameraTheme: CameraTheme? = null,       //相册样式
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    language: Int = LanguageConfig.CHINESE,  //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//屏幕旋转方向
): PictureSelectionModel {
    return this.openCamera(chooseMode)
        .setLanguage(language)
        .setCameraTheme(cameraTheme)
        .setCompress(compress)
        .setCrop(crop)
        .setRequestedOrientation(requestedOrientation)
}

private fun PictureSelector.createGalleryPictureSelectionModel(
    engine: ImageEngine,                    //图片加载框架
    chooseMode: Int = PictureMimeType.ofImage(),    //图片or视频
    isCamera: Boolean = false,              //是否显示拍照按钮
    maxSelectNum: Int = 1,                  //最大图片选择数量
    minSelectNum: Int = 1,                  //最小图片选择数量
    cameraTheme: CameraTheme? = null,       //相册样式
    isOriginalControl: Boolean = true,      //是否显示原图控制按钮，如果用户勾选了 压缩、裁剪功能将会失效
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    isGif: Boolean = false,                 //是否显示gif图片
    language: Int = LanguageConfig.CHINESE,  //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//屏幕旋转方向
): PictureSelectionModel {
    return this.openGallery(chooseMode)
        .isCamera(isCamera)
        .maxSelectNum(maxSelectNum)
        .minSelectNum(minSelectNum)
        .theme(R.style.picture_white_style)
        .isGif(isGif)
        .imageEngine(engine)
        .isOriginalImageControl(isOriginalControl)
        .setLanguage(language)
        .setCameraTheme(cameraTheme)
        .setCompress(compress)
        .setCrop(crop)
        .setRequestedOrientation(requestedOrientation)
}


/**
 * 相机拍照
 */
fun FragmentActivity.openCamera(
    chooseMode: Int = PictureMimeType.ofImage(),    //拍照or录视频
    cameraTheme: CameraTheme? = null,               //相册样式
    compress: CameraCompress? = null,               //压缩
    crop: CameraCrop? = null,                       //裁剪
    language: Int = LanguageConfig.CHINESE,         //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    requestCode: Int                               //requestCode
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openCamera).createGalleryPictureSelectionModel(
                chooseMode, cameraTheme, compress, crop, language, requestedOrientation
            ).forResult(requestCode)
        }
    })
}

/**
 * 相机拍照
 */
fun FragmentActivity.openCamera(
    chooseMode: Int = PictureMimeType.ofImage(),    //拍照or录视频
    cameraTheme: CameraTheme? = null,               //相册样式
    compress: CameraCompress? = null,               //压缩
    crop: CameraCrop? = null,                       //裁剪
    language: Int = LanguageConfig.CHINESE,         //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    resultListener: OnResultCallbackListener<LocalMedia>//结果回调
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openCamera).createGalleryPictureSelectionModel(
                chooseMode, cameraTheme, compress, crop, language, requestedOrientation
            ).forResult(resultListener)
        }
    })
}


/**
 * 相机拍照
 */
fun Fragment.openCamera(
    chooseMode: Int = PictureMimeType.ofImage(),    //拍照or录视频
    cameraTheme: CameraTheme? = null,               //相册样式
    compress: CameraCompress? = null,               //压缩
    crop: CameraCrop? = null,                       //裁剪
    language: Int = LanguageConfig.CHINESE,         //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    requestCode: Int                               //requestCode
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openCamera)
                .createGalleryPictureSelectionModel(
                    chooseMode = chooseMode,
                    cameraTheme = cameraTheme,
                    compress = compress,
                    crop = crop,
                    language = language,
                    requestedOrientation = requestedOrientation
                ).forResult(requestCode)
        }
    })
}

/**
 * 相机拍照
 */
fun Fragment.openCamera(
    chooseMode: Int = PictureMimeType.ofImage(),    //拍照or录视频
    cameraTheme: CameraTheme? = null,               //相册样式
    compress: CameraCompress? = null,               //压缩
    crop: CameraCrop? = null,                       //裁剪
    language: Int = LanguageConfig.CHINESE,         //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    resultListener: OnResultCallbackListener<LocalMedia>//结果回调
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openCamera)
                .createGalleryPictureSelectionModel(
                    chooseMode = chooseMode,
                    cameraTheme = cameraTheme,
                    compress = compress,
                    crop = crop,
                    language = language,
                    requestedOrientation = requestedOrientation
                ).forResult(resultListener)
        }
    })
}


/**
 * 相册选择
 */
fun FragmentActivity.openGallery(
    chooseMode: Int = PictureMimeType.ofImage(),    //图片or视频
    isCamera: Boolean = false,              //是否显示拍照按钮
    maxSelectNum: Int = 1,                  //最大图片选择数量
    minSelectNum: Int = 1,                  //最小图片选择数量
    cameraTheme: CameraTheme? = null,       //相册样式
    isOriginalControl: Boolean = true,      //是否显示原图控制按钮，如果用户勾选了 压缩、裁剪功能将会失效
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    isGif: Boolean = false,                 //是否显示gif图片
    language: Int = LanguageConfig.CHINESE,  //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    engine: ImageEngine,                    //图片加载框架
    requestCode: Int
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openGallery)
                .createGalleryPictureSelectionModel(
                    engine = engine,
                    chooseMode = chooseMode,
                    isCamera = isCamera,
                    maxSelectNum = maxSelectNum,
                    minSelectNum = minSelectNum,
                    cameraTheme = cameraTheme,
                    isOriginalControl = isOriginalControl,
                    compress = compress,
                    crop = crop,
                    isGif = isGif,
                    language = language,
                    requestedOrientation = requestedOrientation
                ).forResult(requestCode)
        }
    })
}

/**
 * 相册选择
 */
fun Fragment.openGallery(
    chooseMode: Int = PictureMimeType.ofImage(),    //图片or视频
    isCamera: Boolean = false,              //是否显示拍照按钮
    maxSelectNum: Int = 1,                  //最大图片选择数量
    minSelectNum: Int = 1,                  //最小图片选择数量
    cameraTheme: CameraTheme? = null,       //相册样式
    isOriginalControl: Boolean = true,      //是否显示原图控制按钮，如果用户勾选了 压缩、裁剪功能将会失效
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    isGif: Boolean = false,                 //是否显示gif图片
    language: Int = LanguageConfig.CHINESE, //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    engine: ImageEngine,                    //图片加载框架
    requestCode: Int                       //requestCode
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openGallery)
                .createGalleryPictureSelectionModel(
                    engine = engine,
                    chooseMode = chooseMode,
                    isCamera = isCamera,
                    maxSelectNum = maxSelectNum,
                    minSelectNum = minSelectNum,
                    cameraTheme = cameraTheme,
                    isOriginalControl = isOriginalControl,
                    compress = compress,
                    crop = crop,
                    isGif = isGif,
                    language = language,
                    requestedOrientation = requestedOrientation
                ).forResult(requestCode)
        }
    })
}


/**
 * 相册选择
 */
fun FragmentActivity.openGallery(
    chooseMode: Int = PictureMimeType.ofImage(),    //图片or视频
    isCamera: Boolean = false,              //是否显示拍照按钮
    maxSelectNum: Int = 1,                  //最大图片选择数量
    minSelectNum: Int = 1,                  //最小图片选择数量
    cameraTheme: CameraTheme? = null,       //相册样式
    isOriginalControl: Boolean = true,      //是否显示原图控制按钮，如果用户勾选了 压缩、裁剪功能将会失效
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    isGif: Boolean = false,                 //是否显示gif图片
    language: Int = LanguageConfig.CHINESE,  //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    engine: ImageEngine,                    //图片加载框架
    resultListener: OnResultCallbackListener<LocalMedia>//结果回调
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openGallery)
                .createGalleryPictureSelectionModel(
                    engine = engine,
                    chooseMode = chooseMode,
                    isCamera = isCamera,
                    maxSelectNum = maxSelectNum,
                    minSelectNum = minSelectNum,
                    cameraTheme = cameraTheme,
                    isOriginalControl = isOriginalControl,
                    compress = compress,
                    crop = crop,
                    isGif = isGif,
                    language = language,
                    requestedOrientation = requestedOrientation
                ).forResult(resultListener)
        }
    })
}

/**
 * 相册选择
 */
fun Fragment.openGallery(
    chooseMode: Int = PictureMimeType.ofImage(),    //图片or视频
    isCamera: Boolean = false,              //是否显示拍照按钮
    maxSelectNum: Int = 1,                  //最大图片选择数量
    minSelectNum: Int = 1,                  //最小图片选择数量
    cameraTheme: CameraTheme? = null,       //相册样式
    isOriginalControl: Boolean = true,      //是否显示原图控制按钮，如果用户勾选了 压缩、裁剪功能将会失效
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    isGif: Boolean = false,                 //是否显示gif图片
    language: Int = LanguageConfig.CHINESE, //设置语言，默认中文
    requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//屏幕旋转方向
    engine: ImageEngine,                    //图片加载框架
    resultListener: OnResultCallbackListener<LocalMedia>//结果回调
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openGallery)
                .createGalleryPictureSelectionModel(
                    engine = engine,
                    chooseMode = chooseMode,
                    isCamera = isCamera,
                    maxSelectNum = maxSelectNum,
                    minSelectNum = minSelectNum,
                    cameraTheme = cameraTheme,
                    isOriginalControl = isOriginalControl,
                    compress = compress,
                    crop = crop,
                    isGif = isGif,
                    language = language,
                    requestedOrientation = requestedOrientation
                ).forResult(resultListener)
        }
    })
}


/**
 * 结果返回多张
 */
val Intent.imagePaths: List<String>
    get() {
        // 图片、视频、音频选择结果回调
        val selectList = PictureSelector.obtainMultipleResult(this)
        val imageList = ArrayList<String>()
        if (selectList == null) {
            imageList.add("相机返回为空!")
            return imageList
        }
        for (media in selectList) {
            imageList.add(media.getMediaPath)
        }
        return imageList
    }

/**
 * 结果返回单张
 */
val Intent.imagePath: String
    get() {
        // 图片、视频、音频选择结果回调
        val selectList = PictureSelector.obtainMultipleResult(this) ?: return ""
        return selectList[0].getMediaPath
    }


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

/**
 * 清除拍照缓存
 */
fun FragmentActivity.cleanPictureCache() {
    permissionCheck(storagePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            Thread {
                PictureFileUtils.deleteAllCacheDirFile(this@cleanPictureCache)
                FileUtils.deleteImageCacheFile(this@cleanPictureCache)
            }
        }
    })
}

/**
 * 清除拍照缓存
 */
fun Fragment.cleanPictureCache() {
    permissionCheck(storagePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            Thread {
                PictureFileUtils.deleteAllCacheDirFile(requireContext())
                FileUtils.deleteImageCacheFile(requireContext())
            }
        }
    })
}