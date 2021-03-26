package tw.tcnr20.m0501

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity

//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class M0501 : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.m0501)
//    }
//}


class M0501 : AppCompatActivity() {
    private var e001: EditText? = null
    private var b001: Button? = null
    private var t004: TextView? = null
    private var s001: Spinner? = null
    private var sSex: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m0501)
        setupViewcomponent()
    }

    private fun setupViewcomponent() {
//        設定layout配置
        e001 = findViewById<View>(R.id.m0501_e001) as EditText //輸入年齡
        b001 = findViewById<View>(R.id.m0501_b01) as Button //婚姻建議鈕
        t004 = findViewById<View>(R.id.m0501_t004) as TextView //顯示建議
        s001 = findViewById<View>(R.id.m0501_s001) as Spinner //選擇性別
        //宣告按鈕監聽程式
        b001!!.setOnClickListener(btnDoSugOnClick)

        //設定spinneritem選項----------------------
        val adapSexList = ArrayAdapter
            .createFromResource(
                this, R.array.m0501_a001,
                android.R.layout.simple_spinner_item
            )
        adapSexList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s001!!.adapter = adapSexList
        s001!!.onItemSelectedListener = spnSexItemSelLis
        b001!!.setOnClickListener(btnDoSugOnClick)
    }

    //===========================================================
    private val spnSexItemSelLis: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            sSex = parent.selectedItem.toString()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    //=============================================================
    private val btnDoSugOnClick = View.OnClickListener {
        //↑建構式
        //宣告 按鈕 監聽b001ON=new(繼承)
        var strSug = getString(R.string.m0501_f000)
        // 檢查 年齡是否有輸入
        if (e001!!.text.toString().trim { it <= ' ' }.length != 0) {
            val iAge = e001!!.text.toString().toInt()
            strSug +=
                if (sSex == getString(R.string.chk01))
                    if (iAge < 28) getString(R.string.m0501_f001)
                    else if (iAge > 33) getString(
                R.string.m0501_f003
            ) else {
                getString(R.string.m0501_f002)
            } else if (iAge < 25) getString(R.string.m0501_f001) else if (iAge > 30) getString(R.string.m0501_f003) else {
                getString(R.string.m0501_f002)
            }
            t004!!.text = strSug
            //-------------------------------------------------------
        } else {
            strSug = getString(R.string.nospace) //else { //年齡輸入空白
        }
        t004!!.text = strSug //請勿輸入空白
    }
}