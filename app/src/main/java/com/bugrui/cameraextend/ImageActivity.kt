package com.bugrui.cameraextend

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bugrui.cameralibrary.*
import com.bumptech.glide.Glide
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.content_image.*

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        setSupportActionBar(findViewById(R.id.toolbar))

        fab.setOnClickListener { view ->
            if (checkBox1.isChecked) {
                openGallery(
                    isCamera = true,
                    engine = GlideEngine,
                    cameraTheme = CameraTheme(
                        theme = pictureCameraThemeWhite
                    ),
                    compress = CameraCompress(
                        isCompress = checkBox2.isChecked,
                        synOrAsy = checkBox3.isChecked
                    ),
                    crop = CameraCrop(
                        isCrop = checkBox.isChecked
                    ),
                    resultListener = object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            if (result == null) {
                                Toast.makeText(
                                    this@ImageActivity,
                                    "result == null",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return
                            }
                            val media = result[0]
                            Toast.makeText(
                                this@ImageActivity, "Q:${media.androidQToPath}" +
                                        "\n\ncutPath:${media.cutPath}" +
                                        "\n\ncompressPath:${media.compressPath}" +
                                        "\n\npath:${media.path}" +
                                        "\n\ngetMediaPath:${media.getMediaPath}", Toast.LENGTH_SHORT
                            ).show()
                            Glide.with(this@ImageActivity).load(media.getMediaPath).into(imageView2)
                        }

                        override fun onCancel() {
                            Toast.makeText(this@ImageActivity, "onCancel", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            } else {
                openCamera(
                    cameraTheme = CameraTheme(
                        theme = pictureCameraThemeWhite
                    ),
                    compress = CameraCompress(
                        isCompress = checkBox2.isChecked,
                        synOrAsy = checkBox3.isChecked
                    ),
                    crop = CameraCrop(
                        isCrop = checkBox.isChecked
                    ),
                    resultListener = object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            if (result == null) {
                                Toast.makeText(
                                    this@ImageActivity,
                                    "result == null",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return
                            }
                            val media = result[0]
                            Toast.makeText(
                                this@ImageActivity, "Q:${media.androidQToPath}" +
                                        "\n\ncutPath:${media.cutPath}" +
                                        "\n\ncompressPath:${media.compressPath}" +
                                        "\n\npath:${media.path}" +
                                        "\n\ngetMediaPath:${media.getMediaPath}", Toast.LENGTH_SHORT
                            ).show()
                            Glide.with(this@ImageActivity).load(media.getMediaPath).into(imageView2)
                        }

                        override fun onCancel() {
                            Toast.makeText(this@ImageActivity, "onCancel", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            }


        }
    }
}