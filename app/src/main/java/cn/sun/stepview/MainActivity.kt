package cn.sun.stepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import cn.sun.stepview.bean.StepBean
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mStepView.setStepViewTexts(ArrayList<StepBean>().apply{
            add(StepBean("aa",1))
            add(StepBean("aa",1))
            add(StepBean("aa",1))
            add(StepBean("aa",1))
            add(StepBean("aa",1))
        })

        Handler().postDelayed({
            mStepView.notifyDataChanged(ArrayList<StepBean>().apply{
                add(StepBean("bb",1))
                add(StepBean("bb",1))
                add(StepBean("bb",1))
                add(StepBean("bb",1))
                add(StepBean("bb",1))
            })
        },3000)
    }
}
