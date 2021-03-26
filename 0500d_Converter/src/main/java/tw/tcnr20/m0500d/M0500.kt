package tw.tcnr20.m0500d

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class M0500 : AppCompatActivity() {
    private var e001: EditText? = null
    private var b001: Button? = null
    private var t003: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m0500)
        setupViewcomponent()
    }

    private fun setupViewcomponent() {
//        設定layout配置
        e001 = findViewById<View>(R.id.m0500_e001) as EditText //輸入公斤
        b001 = findViewById<View>(R.id.m0500_b01) as Button //換算按鈕
        t003 = findViewById<View>(R.id.m0500_t003) as TextView //顯示磅
        //宣告按鈕監聽程式
        b001!!.setOnClickListener(b001ON)
    }

    private val b001ON = View.OnClickListener { //↑建構式
        //宣告 按鈕 監聽b001ON=new(繼承)
        val pondsFormat = DecimalFormat("0.0000")
        val outcomp = pondsFormat.format(e001!!.text.toString().toFloat() * 2.20462262)
        t003!!.text = outcomp
    }
}