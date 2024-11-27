package com.wuc.ft_home.activity

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapePath
import com.google.android.material.shape.TriangleEdgeTreatment
import com.wuc.ft_home.R
import com.wuc.ft_home.databinding.ActivityShapeableImageViewBinding
import com.wuc.ft_home.toolbar.ToolbarActivity
/**
  * @author: wuc
  * @date: 2024/11/27
  * @description: https://juejin.cn/post/6869376452040196109
  */
class ShapeableImageViewActivity : ToolbarActivity<ActivityShapeableImageViewBinding>() {
    override fun setToolbar() {
        mToolbar.setTitle(R.string.shapeable_image_view)
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 代码设置圆角、切角
        val shapeAppearanceModel1 = ShapeAppearanceModel.builder().apply {
            setTopLeftCorner(RoundedCornerTreatment())
            setTopLeftCornerSize(80f)
            setBottomRightCorner(RoundedCornerTreatment())
            setBottomRightCornerSize(80f)
            setTopRightCorner(CutCornerTreatment())
            setTopRightCornerSize(80f)
            setBottomLeftCorner(CutCornerTreatment())
            setBottomLeftCornerSize(80f)
        }.build()
        binding.siv1.shapeAppearanceModel = shapeAppearanceModel1

        // 代码设置 角和边
        val shapeAppearanceModel2 = ShapeAppearanceModel.builder().apply {
            setAllCorners(RoundedCornerTreatment())
            setAllCornerSizes(50f)
            setAllEdges(TriangleEdgeTreatment(50f, false))
        }.build()
        val drawable2 = MaterialShapeDrawable(shapeAppearanceModel2).apply {
            setTint(ContextCompat.getColor(this@ShapeableImageViewActivity, com.wuc.lib_common.R.color.common_accent_color))
            paintStyle = Paint.Style.FILL_AND_STROKE
            strokeWidth = 50f
            strokeColor = ContextCompat.getColorStateList(this@ShapeableImageViewActivity, com.wuc.lib_common.R.color.red)
        }
        binding.text2.setTextColor(Color.WHITE)
        binding.text2.background = drawable2

        // 代码设置 聊天框效果
        val shapeAppearanceModel3 = ShapeAppearanceModel.builder().apply {
            setAllCorners(RoundedCornerTreatment())
            setAllCornerSizes(20f)
            setRightEdge(object : TriangleEdgeTreatment(20f, false) {
                // center 位置 ， interpolation 角的大小
                override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                    super.getEdgePath(length, 35f, interpolation, shapePath)
                }
            })
        }.build()
        val drawable3 = MaterialShapeDrawable(shapeAppearanceModel3).apply {
            setTint(ContextCompat.getColor(this@ShapeableImageViewActivity, com.wuc.lib_common.R.color.common_accent_color))
            paintStyle = Paint.Style.FILL
        }
        (binding.text3.parent as ViewGroup).clipChildren = false // 不限制子view在其范围内
        binding.text3.setTextColor(Color.WHITE)
        binding.text3.background = drawable3
    }

}