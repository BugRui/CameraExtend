package com.bugrui.cameraextend

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bugrui.cameralibrary.*
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureCropParameterStyle
import com.luck.picture.lib.tools.SdkVersionUtils

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
//            openCamera(
//                requestCode =10012,
//                cameraTheme = CameraTheme(
//                    //自定义裁剪样式
//                    cropPictureStyle = PictureCropParameterStyle(
//                        ContextCompat.getColor(this@MainActivity, android.R.color.white),
//                        ContextCompat.getColor(this@MainActivity, android.R.color.white),
//                        Color.parseColor("#333333"),
//                        true
//                    )
//                ),
//                compress = CameraCompress(
//                    isCompress = true
//                )
//            )
            openGallery(
                isCamera = true,
                engine = GlideEngine,
                cameraTheme = CameraTheme(
                    theme = pictureCameraThemeWhite
                ),
                compress = CameraCompress(
                    isCompress = true,
                    synOrAsy = false
                ),
                crop = CameraCrop(
                    isCrop = true
                ),
                resultListener = object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result == null) {
                            Toast.makeText(this@MainActivity, "result == null", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        val media = result[0]
                        Toast.makeText(
                            this@MainActivity, "Q:${media.androidQToPath}" +
                                    "\n\ncutPath:${media.cutPath}" +
                                    "\n\ncompressPath:${media.compressPath}" +
                                    "\n\npath:${media.path}" +
                                    "\n\ngetMediaPath:${media.getMediaPath}", Toast.LENGTH_SHORT
                        ).show()
                        Glide.with(this@MainActivity).load(media.getMediaPath).into(imageView1)
                    }

                    override fun onCancel() {
                        Toast.makeText(this@MainActivity, "onCancel", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                openGallery(
                    requestCode = 10011,
                    isCamera = true,
                    engine = GlideEngine,
                    cameraTheme = CameraTheme(
                        theme = pictureCameraThemeWhite
                    ),
                    compress = CameraCompress(
                        isCompress = true,
                        synOrAsy = true
                    ),
                    crop = CameraCrop(
                        isCrop = true
                    )
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(
            this@MainActivity,
            "onActivityResult:$resultCode\n\n requestCode:$requestCode",
            Toast.LENGTH_SHORT
        ).show()


        if (resultCode != Activity.RESULT_OK) {

            data?.imagePath111

            return
        }
        if (requestCode == 10011) {
            Glide.with(this).load(data?.imagePath111).into(imageView)
        }

    }

    private val Intent.imagePath111: String
        get() {
            // 图片、视频、音频选择结果回调
            val selectList = PictureSelector.obtainMultipleResult(this)

            if (selectList.isNullOrEmpty()) {
                Toast.makeText(this@MainActivity, "null", Toast.LENGTH_SHORT).show()
                return ""
            }

            val media = selectList[0]
            Toast.makeText(
                this@MainActivity, "Q:${media.androidQToPath}" +
                        "\n\ncutPath:${media.cutPath}" +
                        "\n\ncompressPath:${media.compressPath}" +
                        "\n\npath:${media.path}" +
                        "\n\ngetMediaPath:${media.getMediaPath}", Toast.LENGTH_SHORT
            ).show()
            return media.getMediaPath
        }

}



