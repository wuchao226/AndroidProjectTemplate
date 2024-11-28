package com.wuc.ft_home.widgets

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.wuc.ft_home.R
import com.wuc.lib_common.widget.view.BaseFloatView
import com.wuc.lib_common.widget.view.FloatAdsorbType

/**
 * @author: wuc
 * @date: 2024/11/27
 * @description:
 */
class AvatarFloatView(context: Context) : BaseFloatView(context) {

    private var mAdsorbType: FloatAdsorbType = FloatAdsorbType.AdsorbVertical

    override fun getChildView(): View {
        val imageView = ShapeableImageView(context)
        imageView.setImageResource(R.mipmap.ic_avatar)
        imageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // 获取宽度设置圆角
                val radius = imageView.width / 2f
                imageView.shapeAppearanceModel = ShapeAppearanceModel().toBuilder().setAllCornerSizes(radius).build()
            }
        })
        return imageView
    }

    override fun getIsCanDrag(): Boolean {
        return true
    }

    override fun getAdsorbType(): FloatAdsorbType {
        return mAdsorbType
    }

    override fun getAutoAdsorbTimeMillis(): Long {
        return 3000
    }

    fun setAdsorbType(type: FloatAdsorbType) {
        mAdsorbType = type
    }

}