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
import com.luck.picture.lib.style.PictureCropParameterStyle

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            openCamera(
                10011,
                cameraTheme = CameraTheme(
                    //自定义裁剪样式
                    cropPictureStyle = PictureCropParameterStyle(
                        ContextCompat.getColor(this@MainActivity, android.R.color.white),
                        ContextCompat.getColor(this@MainActivity, android.R.color.white),
                        Color.parseColor("#333333"),
                        true
                    )
                ),
                compress = CameraCompress(
                    isCompress = true
                ),
                crop = CameraCrop(
                    isCrop = true
                )
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
                    10011,
                    isCamera = true,
                    engine = GlideEngine(),
                    cameraTheme = CameraTheme(
                        theme = pictureCameraThemeWhite
                    ),
                    compress = CameraCompress(
                        isCompress = true
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
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == 10011) {
            Glide.with(this).load(data?.imagePath).into(imageView)
        }
    }
}
