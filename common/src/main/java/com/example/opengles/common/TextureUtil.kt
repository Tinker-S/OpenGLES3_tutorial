package com.example.opengles.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import androidx.annotation.DrawableRes

object TextureUtil {

    fun loadTexture(context: Context, index: Int, @DrawableRes bitmapResource: Int) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapResource, options)
        loadTexture(index, bitmap)
    }

    fun loadTexture(index: Int, bitmap: Bitmap, recycle: Boolean = true) {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, index)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        if (recycle) {
            bitmap.recycle()
        }
    }
}