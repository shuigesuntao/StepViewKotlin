package cn.sun.stepview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import cn.sun.stepview.bean.StepBean

import java.util.ArrayList

class HorizontalStepsViewIndicator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //定义默认的高度
    private val defaultStepIndicatorNum = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics).toInt()

    private var mCompletedLineHeight = 0.05f * defaultStepIndicatorNum//完成线的高度
    private var mCircleRadius = 0.28f * defaultStepIndicatorNum //圆的半径

    var mCompleteIcon: Drawable = context.resources.getDrawable(R.drawable.complted)//完成的默认图片
    var mAttentionIcon: Drawable = context.resources.getDrawable(R.drawable.attention)//正在进行的默认图片on
    var mDefaultIcon: Drawable = context.resources.getDrawable(R.drawable.default_icon)//默认的背景图
    private var mCenterY = 0f//该view的Y轴中间位置
    private var mLeftY = 0f//左上方的Y位置
    private var mRightY = 0f//右下方的位置

    private var mStepBeanList: List<StepBean> = ArrayList()//当前有几部流程
    private var mStepNum = 0
    private var mLinePadding = 0.85f * defaultStepIndicatorNum//两条连线之间的间距

    private var mCircleCenterPointPositionList= ArrayList<Float>()//定义所有圆的圆心点位置的集合
    private var mUnCompletedPaint: Paint = Paint()//未完成Paint
    private var mCompletedPaint: Paint = Paint()//完成paint
    var mUnCompletedLineColor = Color.parseColor("#A3E0D9")
    var mCompletedLineColor = Color.WHITE//定义默认完成线的颜色
    private var mEffects: PathEffect = DashPathEffect(floatArrayOf(8f, 8f, 8f, 8f), 1f)
    var mComplectedPosition = 0//正在进行position


    private var mPath: Path = Path()

    private var mOnDrawListener: OnDrawIndicatorListener? = null

    fun setOnDrawListener(onDrawListener: OnDrawIndicatorListener) {
        mOnDrawListener = onDrawListener
    }

    init {
        mUnCompletedPaint.isAntiAlias = true
        mUnCompletedPaint.color = mUnCompletedLineColor
        mUnCompletedPaint.style = Paint.Style.STROKE
        mUnCompletedPaint.strokeWidth = 2f

        mCompletedPaint.isAntiAlias = true
        mCompletedPaint.color = mCompletedLineColor
        mCompletedPaint.style = Paint.Style.STROKE
        mCompletedPaint.strokeWidth = 2f

        mUnCompletedPaint.pathEffect = mEffects
        mCompletedPaint.style = Paint.Style.FILL
    }

    /**
     * 圆的半径
     */
    fun getCircleRadius(): Float {
        return mCircleRadius
    }

    /**
     * 设置对view监听
     */
    interface OnDrawIndicatorListener {
        fun onDrawIndicator()
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = defaultStepIndicatorNum * 2
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }
        var height = defaultStepIndicatorNum
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec))
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        notifyDataSetChanged()
    }

    fun notifyDataSetChanged(){
        //获取中间的高度,目的是为了让该view绘制的线和圆在该view垂直居中
        mCenterY = 0.5f * height
        //获取左上方Y的位置，获取该点的意义是为了方便画矩形左上的Y位置
        mLeftY = mCenterY - mCompletedLineHeight / 2
        //获取右下方Y的位置，获取该点的意义是为了方便画矩形右下的Y位置
        mRightY = mCenterY + mCompletedLineHeight / 2

        mCircleCenterPointPositionList.clear()
        for (i in 0 until mStepNum) {
            //先计算全部最左边的padding值（getWidth()-（圆形直径+两圆之间距离）*2）
            val paddingLeft = (width.toFloat() - mStepNum * mCircleRadius * 2f - (mStepNum - 1) * mLinePadding) / 2
            //add to list
            mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2f + i * mLinePadding)
        }

        /**
         * set listener
         */
        mOnDrawListener?.onDrawIndicator()
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mOnDrawListener?.onDrawIndicator()
        mUnCompletedPaint.color = mUnCompletedLineColor
        mCompletedPaint.color = mCompletedLineColor

        //-----------------------画线-------draw line-----------------------------------------------
        for (i in 0 until mCircleCenterPointPositionList.size - 1) {
            //前一个ComplectedXPosition
            val preComplectedXPosition = mCircleCenterPointPositionList[i]
            //后一个ComplectedXPosition
            val afterComplectedXPosition = mCircleCenterPointPositionList[i + 1]
            //判断在完成之前的所有点
            if (i <= mComplectedPosition && mStepBeanList[0].state != StepBean.STEP_UNDO) {
                //判断在完成之前的所有点，画完成的线，这里是矩形,很细的矩形，类似线，为了做区分，好看些
                canvas.drawRect(preComplectedXPosition + mCircleRadius - 10, mLeftY, afterComplectedXPosition - mCircleRadius + 10, mRightY, mCompletedPaint)
            } else {
                mPath.moveTo(preComplectedXPosition + mCircleRadius, mCenterY)
                mPath.lineTo(afterComplectedXPosition - mCircleRadius, mCenterY)
                canvas.drawPath(mPath, mUnCompletedPaint)
            }
        }
        //-----------------------画线-------draw line-----------------------------------------------

        //-----------------------画图标-----draw icon-----------------------------------------------

        for (i in mCircleCenterPointPositionList.indices) {
            val currentComplectedXPosition = mCircleCenterPointPositionList[i]
            val rect = Rect((currentComplectedXPosition - mCircleRadius).toInt(), (mCenterY - mCircleRadius).toInt(), (currentComplectedXPosition + mCircleRadius).toInt(), (mCenterY + mCircleRadius).toInt())

            val stepsBean = mStepBeanList[i]

            when (stepsBean.state) {
                StepBean.STEP_UNDO -> {
                    mDefaultIcon.bounds = rect
                    mDefaultIcon.draw(canvas)
                }
                StepBean.STEP_CURRENT -> {
                    mCompletedPaint.color = Color.WHITE
                    canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius * 1.1f, mCompletedPaint)
                    mAttentionIcon.bounds = rect
                    mAttentionIcon.draw(canvas)
                }
                StepBean.STEP_COMPLETED -> {
                    mCompleteIcon.bounds = rect
                    mCompleteIcon.draw(canvas)
                }
            }
        }
        //-----------------------画图标-----draw icon-----------------------------------------------
    }

    /**
     * 得到所有圆点所在的位置
     */
    fun getCircleCenterPointPositionList(): List<Float> {
        return mCircleCenterPointPositionList
    }

    /**
     * 设置流程步数
     */
    fun setStepNum(stepsBeanList: List<StepBean>) {
        this.mStepBeanList = stepsBeanList
        mStepNum = mStepBeanList.size

        if (mStepBeanList.isNotEmpty()) {
            for (i in 0 until mStepNum) {
                val stepsBean = mStepBeanList[i]
                run {
                    if (stepsBean.state == StepBean.STEP_COMPLETED) {
                        mComplectedPosition = i
                    }
                }
            }
        }

        requestLayout()
    }
}