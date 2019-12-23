package com.bugrui.cameralibrary

import android.content.Intent
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bugrui.permission.OnPermissionsTaskListener
import com.bugrui.permission.permissionCheck
import com.luck.picture.lib.PictureSelectionModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureCropParameterStyle
import com.luck.picture.lib.style.PictureParameterStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.luck.picture.lib.tools.PictureFileUtils
import java.util.ArrayList

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
                    else -> R.style.picture_white_style
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
        this.compress(it.isCompress)
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
        this.enableCrop(it.isCrop)

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
            this.cropWH(it.cropWidth, it.cropHeight)
        }
        //裁剪比例 如16:9 3:2 3:4 1:1 可自定义
        if (it.aspect_ratio_x != -1 && it.aspect_ratio_y != -1) {
            this.withAspectRatio(it.aspect_ratio_x, it.aspect_ratio_y)
        }

    }
    return this
}

/**
 * 相机拍照
 */
fun FragmentActivity.openCamera(
    requestCode: Int,                       //requestCode
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    language: Int = LanguageConfig.CHINESE  //设置语言，默认中文
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openCamera)
                .openCamera(PictureMimeType.ofImage())
                .setLanguage(language)
                .setCompress(compress)
                .setCrop(crop)
                .forResult(requestCode)
        }
    })

}

/**
 * 相机拍照
 */
fun Fragment.openCamera(
    requestCode: Int,                       //requestCode
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    language: Int = LanguageConfig.CHINESE  //设置语言，默认中文
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openCamera)
                .openCamera(PictureMimeType.ofImage())
                .setLanguage(language)
                .setCompress(compress)
                .setCrop(crop)
                .forResult(requestCode)
        }
    })
}

/**
 * 相册选择
 */
fun FragmentActivity.openGallery(
    requestCode: Int,                       //requestCode
    engine: ImageEngine,                    //图片加载框架
    isCamera: Boolean = false,              //是否显示拍照按钮
    maxSelectNum: Int = 1,                  //最大图片选择数量
    minSelectNum: Int = 1,                  //最小图片选择数量
    cameraTheme: CameraTheme? = null,       //相册样式
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    isGif: Boolean = false,                 //是否显示gif图片
    language: Int = LanguageConfig.CHINESE  //设置语言，默认中文
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openGallery)
                .openGallery(PictureMimeType.ofImage())
                .isCamera(isCamera)
                .maxSelectNum(maxSelectNum)
                .minSelectNum(minSelectNum)
                .theme(R.style.picture_white_style)
                .isGif(isGif)
                .loadImageEngine(engine)
                .setLanguage(language)
                .setCameraTheme(cameraTheme)
                .setCompress(compress)
                .setCrop(crop)
                .forResult(requestCode)
        }
    })
}

/**
 * 相册选择
 */
fun Fragment.openGallery(
    requestCode: Int,                       //requestCode
    engine: ImageEngine,                    //图片加载框架
    isCamera: Boolean = false,              //是否显示拍照按钮
    maxSelectNum: Int = 1,                  //最大图片选择数量
    minSelectNum: Int = 1,                  //最小图片选择数量
    cameraTheme: CameraTheme? = null,       //相册样式
    compress: CameraCompress? = null,       //压缩
    crop: CameraCrop? = null,               //裁剪
    isGif: Boolean = false,                 //是否显示gif图片
    language: Int = LanguageConfig.CHINESE  //设置语言，默认中文
) {
    permissionCheck(cameraAndStoragePermissions, object : OnPermissionsTaskListener() {
        override fun onPermissionsTask() {
            PictureSelector.create(this@openGallery)
                .openGallery(PictureMimeType.ofImage())
                .isCamera(isCamera)
                .maxSelectNum(maxSelectNum)
                .minSelectNum(minSelectNum)
                .theme(R.style.picture_white_style)
                .isGif(isGif)
                .loadImageEngine(engine)
                .setLanguage(language)
                .setCameraTheme(cameraTheme)
                .setCompress(compress)
                .setCrop(crop)
                .forResult(requestCode)
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
            if (media.isCompressed && !TextUtils.isEmpty(media.compressPath)) {
                imageList.add(media.compressPath)
                continue
            }
            if (media.isCut && !TextUtils.isEmpty(media.cutPath)) {
                imageList.add(media.cutPath)
                continue
            }
            imageList.add(media.path)
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
        val media = selectList[0]
        if (media.isCompressed && !TextUtils.isEmpty(media.compressPath)) {
            return media.compressPath
        }
        return if (media.isCut && !TextUtils.isEmpty(media.cutPath)) {
            media.cutPath
        } else media.path
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