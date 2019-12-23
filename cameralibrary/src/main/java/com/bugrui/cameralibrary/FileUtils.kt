package com.bugrui.cameralibrary

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.os.Environment

import java.io.File
import java.io.IOException

/**
 * @Author: BugRui
 * @CreateDate: 2019/10/12 13:50
 * @Description: 文件工具类
 */
object FileUtils {


    /**
     * 缓存路径
     */
    fun getFileCachePath(context: Context): String {
        val externalFilesDir = context.getExternalFilesDir("camera_data_caches")
        val recordDirectory =
            Environment.getExternalStorageDirectory().absolutePath + "/camera_data_caches/"

        val fileDirPath =
            if (externalFilesDir == null) recordDirectory else externalFilesDir.absolutePath

        val recordDir = File(fileDirPath)

        // 要保证目录存在，如果不存在则主动创建
        if (!isAndroidQFileExists(context, recordDir, fileDirPath)) {
            recordDir.mkdirs()
        }

        return fileDirPath
    }


    /**
     * 判断公有目录文件是否存在，自Android Q开始，公有目录File API都失效，
     * 不能直接通过new File(path).exists();判断公有目录文件是否存在，正确方式如下：
     */
    fun isAndroidQFileExists(context: Context?, file: File, path: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return file.exists()
        }
        if (context == null) {
            return false
        }
        var afd: AssetFileDescriptor? = null
        val cr = context.contentResolver
        try {
            val uri = Uri.parse(path)
            afd = cr.openAssetFileDescriptor(Uri.parse(path), "r")
            if (afd == null) {
                return false
            } else {
                afd.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                afd?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return true
    }


    //删除集合文件并通知相册更新
    fun deleteImageCacheFile(context: Context) {
        //删除
        if (getFileCachePath(context) == null) {
            return
        }
        val rootPath = getFileCachePath(context)
        val folder = File(rootPath)
        if (!folder.exists()) return
        val fa = folder.listFiles() ?: return
        for (f in fa) {
            //文件是否存在
            if (f.exists()) continue
            //执行删除
            val delete = f.delete()
            if (delete) {
                //删除成功，通知相册刷新
                val uri = Uri.fromFile(f)
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
                context.sendBroadcast(intent)
            }
        }
    }


}
