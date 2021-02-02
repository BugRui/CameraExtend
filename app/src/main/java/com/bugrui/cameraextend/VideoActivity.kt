package com.bugrui.cameraextend

import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import com.bugrui.cameralibrary.*
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.android.synthetic.main.content_video.*

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        setSupportActionBar(findViewById(R.id.toolbar))

        //添加播放控制条,还是自定义好点
        videoView.setMediaController(MediaController(this))


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            if (checkBox1.isChecked) {

                openGallery(
                    isCamera = true,
                    chooseMode = PictureMimeType.ofVideo(),
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
                            if (result==null) {
                                Toast.makeText(this@VideoActivity, "result is null", Toast.LENGTH_SHORT)
                                    .show()
                                return
                            }
                            val media = result[0]
                            Toast.makeText(
                                this@VideoActivity, "Q:${media.androidQToPath}" +
                                        "\n\ncutPath:${media.cutPath}" +
                                        "\n\ncompressPath:${media.compressPath}" +
                                        "\n\npath:${media.path}" +
                                        "\n\nrealPath:${media.realPath}" +
                                        "\n\ngetMediaPath:${media.getMediaPath}", Toast.LENGTH_SHORT
                            ).show()
                            videoView.setVideoPath(media.getMediaPath)
                            videoView.start()
                        }

                        override fun onCancel() {
                            Toast.makeText(this@VideoActivity, "onCancel", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            } else {
                openCamera(
                    chooseMode = PictureMimeType.ofVideo(),
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
                            if (result==null) {
                                Toast.makeText(this@VideoActivity, "result is null", Toast.LENGTH_SHORT)
                                    .show()
                                return
                            }
                            val media = result[0]
                            Toast.makeText(
                                this@VideoActivity, "Q:${media.androidQToPath}" +
                                        "\n\ncutPath:${media.cutPath}" +
                                        "\n\ncompressPath:${media.compressPath}" +
                                        "\n\npath:${media.path}" +
                                        "\n\ngetMediaPath:${media.getMediaPath}", Toast.LENGTH_SHORT
                            ).show()
                            videoView.setVideoPath(media.getMediaPath)
                            videoView.start()
                        }

                        override fun onCancel() {
                            Toast.makeText(this@VideoActivity, "onCancel", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            }
        }
    }
}