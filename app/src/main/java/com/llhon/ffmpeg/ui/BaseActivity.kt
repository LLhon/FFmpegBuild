package com.llhon.ffmpeg.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.llhon.ffmpeg.ext.inflateBindingWithGeneric

/**
 * author  : LLhon
 * date    : 2024/3/21 17:07.
 * des     :
 */
open class BaseActivity<VB: ViewBinding> : AppCompatActivity() {

    lateinit var mBind: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(initDataBind())

        initView()
        initData()
    }

    /**
     * 绑定ViewBinding
     */
    private fun initDataBind(): View {
        mBind = inflateBindingWithGeneric(layoutInflater)
        return mBind.root
    }

    //初始化数据
    open fun initData() {}

    //初始化视图
    open fun initView() {}
}