package com.iranmobiledev.moodino.utlis

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class GlideImageLoader() : ImageLoadingService {
    override fun load(context: Context, imagePath: String, container: ImageView) {
        Glide.with(context).load(imagePath).dontAnimate().into(container)
    }

    override fun load(context: Context, imageResource: Int, container: ImageView) {
        Glide.with(context).load(imageResource).dontAnimate().into(container)
    }

    override fun load(context: Context, imageResource: Uri, container: ImageView) {
        Glide.with(context).load(imageResource).into(container)
    }

    override fun remove(context: Context, imageView: ImageView) {
        Glide.with(context).clear(imageView)
    }
}