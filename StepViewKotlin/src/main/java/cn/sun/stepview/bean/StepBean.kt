package cn.sun.stepview.bean

class StepBean(val name: String,
               val state: Int) {
    companion object {
        const val STEP_UNDO = -1//未完成  undo step
        const val STEP_CURRENT = 0//正在进行 current step
        const val STEP_COMPLETED = 1//已完成 completed step
    }
}