package cn.sun.stepview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.sun.stepview.bean.StepBean


class HorizontalStepView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    HorizontalStepsViewIndicator.OnDrawIndicatorListener {
    private val mTextContainer: RelativeLayout
    private val mStepsViewIndicator: HorizontalStepsViewIndicator
    private var mStepBeanList: List<StepBean> = ArrayList()
    private var mUnComplectedTextColor = Color.parseColor("#A3E0D9")//定义默认未完成文字的颜色
    private var mComplectedTextColor = Color.WHITE//定义默认完成文字的颜色
    private var mTextSize = 14//

    init{
//        val linearLayout = LinearLayout(context)
//                .apply {
//                    orientation = VERTICAL
//                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//                }
//        mTextContainer = RelativeLayout(context)
//                .apply {
//                    layoutParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
//                }
//        mStepsViewIndicator = HorizontalStepsViewIndicator(context)
//                .apply {
//                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
//                            .apply {
//                                setMargins(0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt(),0,0)
//                            }
//                }
        val rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_horizontal_stepsview_kotlin, this)
        mStepsViewIndicator = rootView.findViewById(R.id.steps_indicator_kt)
        mStepsViewIndicator.setOnDrawListener(this)
        mTextContainer = rootView.findViewById(R.id.rl_text_container_kt)
//        linearLayout.addView(mStepsViewIndicator)
//        linearLayout.addView(mTextContainer)


    }

    /**
     * 设置显示的文字
     */
    fun setStepViewTexts(stepsBeanList: List<StepBean>): HorizontalStepView {
        mStepBeanList = stepsBeanList
        mStepsViewIndicator.setStepNum(mStepBeanList)
        return this
    }


    /**
     * 设置未完成文字的颜色
     */
    fun setStepViewUnComplectedTextColor(unComplectedTextColor: Int): HorizontalStepView {
        mUnComplectedTextColor = unComplectedTextColor
        return this
    }

    /**
     * 设置完成文字的颜色
     */
    fun setStepViewComplectedTextColor(complectedTextColor: Int): HorizontalStepView {
        this.mComplectedTextColor = complectedTextColor
        return this
    }

    /**
     * 设置StepsViewIndicator未完成线的颜色
     */
    fun setStepsViewIndicatorUnCompletedLineColor(unCompletedLineColor: Int): HorizontalStepView {
        mStepsViewIndicator.mUnCompletedLineColor = unCompletedLineColor
        return this
    }

    /**
     * 设置StepsViewIndicator完成线的颜色
     */
    fun setStepsViewIndicatorCompletedLineColor(completedLineColor: Int): HorizontalStepView {
        mStepsViewIndicator.mCompletedLineColor = completedLineColor
        return this
    }

    /**
     * 设置StepsViewIndicator默认图片
     */
    fun setStepsViewIndicatorDefaultIcon(defaultIcon: Drawable): HorizontalStepView {
        mStepsViewIndicator.mDefaultIcon = defaultIcon
        return this
    }

    /**
     * 设置StepsViewIndicator已完成图片
     */
    fun setStepsViewIndicatorCompleteIcon(completeIcon: Drawable): HorizontalStepView {
        mStepsViewIndicator.mCompleteIcon = completeIcon
        return this
    }

    /**
     * 设置StepsViewIndicator正在进行中的图片
     */
    fun setStepsViewIndicatorAttentionIcon(attentionIcon: Drawable): HorizontalStepView {
        mStepsViewIndicator.mAttentionIcon = attentionIcon
        return this
    }

    /**
     * 设置字体大小
     */
    fun setTextSize(textSize: Int): HorizontalStepView {
        if (textSize > 0) {
            mTextSize = textSize
        }
        return this
    }

    override fun onDrawIndicator() {
        mTextContainer.removeAllViews()
        val complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList()
        if (complectedXPosition.isNotEmpty()) {
            for (i in mStepBeanList.indices) {
                val textView = TextView(context)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize.toFloat())
                textView.text = mStepBeanList[i].name
                val spec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED)
                textView.measure(spec, spec)
                // getMeasuredWidth
                val measuredWidth = textView.measuredWidth
                textView.x = complectedXPosition[i] - measuredWidth / 2
                textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                if (i <= mStepsViewIndicator.mComplectedPosition) {
                    textView.setTypeface(null, Typeface.BOLD)
                    textView.setTextColor(mComplectedTextColor)
                } else {
                    textView.setTextColor(mUnComplectedTextColor)
                }

                mTextContainer.addView(textView)
            }
        }
    }
}