package com.example.streetcat.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.streetcat.R
import com.example.streetcat.activity.CatAdd
import com.example.streetcat.activity.LoginActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view: View = inflater!!.inflate(R.layout.fragment_setting, container, false)
        view.btn_school_auth.setOnClickListener { view ->
            Log.d("btn_school", "Selected")
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout.setOnClickListener{ view ->
            Log.d("btnSetup", "Selected")
            //activity 캐스팅 문제로 꺼짐, 바로 로그아웃 함수 쓸 수 있게 수정해야
            //(activity as LoginActivity).signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
/*
        super.onViewCreated(view, savedInstanceState)
        btn_school_auth.setOnClickListener{view ->
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }*/
    }

    //로그아웃 리스너
    inner class ButtonListener : View.OnClickListener {
        override fun onClick(v: View?) {

        }
    }


}