package tw.tcnr20.m1705
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.GpsStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class M1705 : AppCompatActivity(), LocationListener,
    View.OnClickListener {
    private var webView: WebView? = null
    private var mSpnLocation: Spinner? = null
    private var Lat: String? = null
    private var Lon: String? = null
    private var jcontent: String? = null
    private var locationMgr: LocationManager? = null
    private val permissionsList: MutableList<String> =
        ArrayList()
    private var txtOutput: TextView? = null
    private lateinit var provider: String  // 提供資料
    private val TAG = "tcnr12=>"
    private var bNav: Button? = null
    private var Navon = "off"
    var Navstart = "24.172127,120.610313"
    var Navend = "24.144671,120.683981" // 結束點
    private var iSelect = 0
    private var aton: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m1705)
        checkRequiredPermission(this)
        setupViewcomponent()
    }

    private fun setupViewcomponent() {
        webView = findViewById<View>(R.id.webview) as WebView
        mSpnLocation = findViewById<View>(R.id.spnLocation) as Spinner
        mSpnLocation!!.background.alpha = 100
        webView!!.settings.javaScriptEnabled = true
        bNav = findViewById<View>(R.id.tNavigation) as Button
        webView!!.addJavascriptInterface(this@M1705, "AndroidFunction")
        //        webView.loadUrl(MAP_URL1);
        txtOutput = findViewById<View>(R.id.txtOutput) as TextView
        val adapter =
            ArrayAdapter<String>(this, R.layout.spinner_style)
        for (i in locations.indices) {
            adapter.add(locations[i][0])
        }
        adapter.setDropDownViewResource(R.layout.spinner_style)
        mSpnLocation!!.adapter = adapter
        mSpnLocation!!.onItemSelectedListener = mSpnLocationOnitemSelLis
        bNav!!.setOnClickListener(this)
        //        findViewById(R.id.tNavigation).setOnClickListener(this);
    }

    override fun onClick(v: View) {
        if (Navon === "off") {
            bNav!!.setTextColor(getColor(R.color.blue))
            Navon = "on"
            bNav!!.text = "關閉路徑規劃"
            setMapLocation()
        } else {
            bNav!!.setTextColor(getColor(R.color.red))
            Navon = "off"
            bNav!!.text = getString(R.string.tNavigation)
            setMapLocation()
        }
    }

    // private JSONArray ArryToJson() {
    private fun ArryToJson(): String {
        val jArry = JSONArray()
        for (i in 1 until locations.size) {
            val jObj = JSONObject() // 一定要放在這裡
            val arr =
                locations[i][1].split(",").toTypedArray()
            try {
                jObj.put("title", locations[i][0])
                jObj.put("jlat", arr[0])
                jObj.put("jlon", arr[1])
                jArry.put(jObj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return jArry.toString()
        // return jArry;
    }

    private val mSpnLocationOnitemSelLis: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View,
            position: Int,
            id: Long
        ) {
            setMapLocation()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun setMapLocation() {
        iSelect = mSpnLocation!!.selectedItemPosition
        val sLocation =
            locations[iSelect][1].split(",").toTypedArray()
        Lat = sLocation[0] //南北緯
        Lon = sLocation[1] //東西經
        jcontent = locations[iSelect][0] //地名
        //  ==========判斷是否導航===============
        if (Navon === "on" && iSelect != 0) {
            Navstart = locations[0][1]
            Navend = locations[iSelect][1]
            val deleteOverlays = "javascript: RoutePlanning()"
            webView!!.loadUrl(deleteOverlays)
        } else {
//            把亂七八糟的線清掉
            webView!!.settings.javaScriptEnabled = true
            webView!!.addJavascriptInterface(this@M1705, "AndroidFunction")
            webView!!.loadUrl(MAP_URL1)
        }
        //
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.addJavascriptInterface(M1705.this, "AndroidFunction");
//        webView.loadUrl(MAP_URL1);
    }

    override fun onLocationChanged(location: Location) {
        //定位改變時
        updateWithNewLocation(location)
        //將畫面移至定位點的位置
        aton = location.latitude.toString() + "," + location.longitude
        val centerURL = "javascript:centerAt(" +
                location.latitude + "," + location.longitude + ")"
        webView!!.loadUrl(centerURL)
        val deleteOverlays = "javascript:deleteOverlays()"
        webView!!.loadUrl(deleteOverlays)
        // 定位改變時
//        updateWithNewLocation(location);
//// --- 呼叫 Map JS
//        Navstart = locations[0][1];
//// webView.loadUrl(MAP_URL);
////---------增加判斷是否規畫路徑------------------
//        if (Navon == "on" && iSelect != 0) {
//            final String deleteOverlays = "javascript: RoutePlanning()";
//            webView.loadUrl(deleteOverlays);
//        }else{
//            webView.getSettings().setJavaScriptEnabled(true);
//            webView.addJavascriptInterface(M1705.this, "AndroidFunction");
//            webView.loadUrl(MAP_URL1);
//        }
//// ---
//        Log.d(TAG, "onLocationChanged");
    }

    override fun onStatusChanged(
        provider: String,
        status: Int,
        extras: Bundle
    ) {
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    private fun updateWithNewLocation(location: Location?) {
        var where = ""
        if (location != null) {
            val lng = location.longitude // 經度
            val lat = location.latitude // 緯度
            val speed = location.speed // 速度
            val time = location.time // 時間
            val timeString = getTimeString(time)
            where =
                """
                經度: $lng
                緯度: $lat
                速度: $speed
                時間: $timeString
                Provider: $provider
                """.trimIndent()
            Lat = java.lang.Double.toString(lat)
            Lon = java.lang.Double.toString(lng)
            locations[0][1] = "$lat,$lng"
            //            webView.loadUrl(MAP_URL1);
            // 標記"我的位置"
//            showMarkerMe(lat, lng);
//            cameraFocusOnMe(lat, lng);
        } else {
            where = "*位置訊號消失*"
        }
        // 位置改變顯示
        txtOutput!!.text = where

//        ***********************************************************
    }

    private fun getTimeString(timeInMilliseconds: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(timeInMilliseconds)
    }

    private fun initLocationProvider(): Boolean {
        locationMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (locationMgr!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER
            true
        } else {
            false
        }
    }

    private fun nowaddress() {
        //檢查是否有權限-------------------------------------------
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = locationMgr!!.getLastKnownLocation(provider)
        updateWithNewLocation(location)
        // 監聽 GPS Listener
        locationMgr!!.addGpsStatusListener(gpsListener)
        // Location Listener
        val minTime: Long = 5000 // ms
        val minDist = 5.0f // meter
        locationMgr!!.requestLocationUpdates(
            provider, minTime, minDist,
            this
        ) //開始座標移動
        //        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            Location location = locationManager.getLastKnownLocation(provider);
///******************************************************** */
//            updateWithNewLocation(location); //*****開啟GPS定位
///********************************************************* */
//            return;
//        }
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        Location location = null;
//        if (!(isGPSEnabled || isNetworkEnabled))
//            tmsg.setText("GPS 未開啟");
//        else {
//            if (isNetworkEnabled) {
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                        minTime, minDist, locationListener);
//                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                tmsg.setText("使用網路GPS");
//            }
//            if (isGPSEnabled) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                        minTime, minDist, locationListener);
//                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                tmsg.setText("使用精確GPS");
//            }
//        }
    }

    // -------------------------------
    var gpsListener = GpsStatus.Listener { event ->

        /* 監聽GPS 狀態 */
        when (event) {
            GpsStatus.GPS_EVENT_STARTED -> Log.d(TAG, "GPS_EVENT_STARTED")
            GpsStatus.GPS_EVENT_STOPPED -> Log.d(TAG, "GPS_EVENT_STOPPED")
            GpsStatus.GPS_EVENT_FIRST_FIX -> Log.d(TAG, "GPS_EVENT_FIRST_FIX")
            GpsStatus.GPS_EVENT_SATELLITE_STATUS -> Log.d(
                TAG,
                "GPS_EVENT_SATELLITE_STATUS"
            )
        }
    }

    //    --------------------------------------------
    override fun onStart() {
        super.onStart()
        if (initLocationProvider()) {
            nowaddress()
        } else {
            txtOutput!!.text = "GPS未開啟,請先開啟定位！"
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        locationMgr!!.removeUpdates(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationMgr!!.removeUpdates(this)
    }

    //    --------------------menu----------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun checkRequiredPermission(activity: Activity) {
        for (permission in permissionsArray) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsList.add(permission)
            }
        }
        if (permissionsList.size != 0) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsList.toTypedArray(),
                REQUEST_CODE_ASK_PERMISSIONS
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_001 -> webView!!.loadUrl(MAP_URL1)
            R.id.menu_002 -> webView!!.loadUrl(MAP_URL2)
            R.id.action_settings -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    // ============丟資料到html時要做街口=================================================
    @JavascriptInterface
    fun GetLat(): String? {
        return Lat
    }

    @JavascriptInterface
    fun GetLon(): String? {
        return Lon
    }

    @JavascriptInterface
    fun Getjcontent(): String? {
        return jcontent
    }

    @JavascriptInterface
    fun GetJsonArry(): String {
        return ArryToJson()
    }

    @JavascriptInterface
    fun Navon(): String {
        return Navon
    }

    @JavascriptInterface
    fun Getstart(): String? {
        return aton
    }

    @JavascriptInterface
    fun Getend(): String {
        return Navend
    } //    @JavascriptInterface

    //    public String Getaton(){
    //        return aton;
    //    }
    companion object {
        private val locations =
            arrayOf(
                arrayOf("現在位置", "0,0"),
                arrayOf("中區職訓", "24.172127,120.610313"),
                arrayOf("東海大學路思義教堂", "24.179051,120.600610"),
                arrayOf("台中公園湖心亭", "24.144671,120.683981"),
                arrayOf("秋紅谷", "24.1674900,120.6398902"),
                arrayOf("台中火車站", "24.136829,120.685011"),
                arrayOf("國立科學博物館", "24.1579361,120.6659828")
            )
        private const val MAP_URL1 = "file:///android_asset/GoogleMap.html" // 自建的html檔名
        private const val MAP_URL2 =
            "https://city2farmer.com/tcnr12/web/20210305_makmap.html" // 自建的html檔名
        private val permissionsArray = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        //申請權限後的返回碼
        private const val REQUEST_CODE_ASK_PERMISSIONS = 1
    }
}