package jp.techachademy.hiroshi.autoslideshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest
import android.net.Uri
import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 10
    private var imageArrayList = arrayListOf<Uri>()
    private var imageUrlNumber:Int = 0
    private var mHandler = Handler()
    private var mTimer: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       0
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


        start_button.setOnClickListener {

            Log.d("UI_PARTS", "ボタンをタップしました")
            if (mTimer == null) {
                mTimer = Timer()
                Log.d("image2","${imageArrayList}")
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        imageUrlNumber += 1
                        Log.d("image3","${imageUrlNumber}")
                        if (imageUrlNumber == imageArrayList.size - 1) {
                            imageUrlNumber = 0
                        }
                        mHandler.post {
                            slide.setImageURI(imageArrayList[imageUrlNumber])
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定
                start_button.text="停止"

            }else{
                mTimer!!.cancel()
                mTimer = null
                start_button.text="再生"
            }
        }
        next_button.setOnClickListener {
            if (mTimer == null) {
                Log.d("UI_PARTS", "ボタンをタップしました")
                imageUrlNumber += 1
                if (imageUrlNumber == imageArrayList.size) {
                    imageUrlNumber = 0
                }
                slide.setImageURI(imageArrayList[imageUrlNumber])
            }
        }
        return_button.setOnClickListener {
            if (mTimer == null) {
                Log.d("UI_PARTS", "ボタンをタップしました")
                if (imageUrlNumber == 0) {
                    imageUrlNumber = imageArrayList.size
                }
                imageUrlNumber -= 1
                slide.setImageURI(imageArrayList[imageUrlNumber])
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }else{
                    Log.d("test","許可してください")
                    notgetContentInfo()
                }
        }


    }
    private fun notgetContentInfo(){
        imageArrayList = arrayListOf<Uri>()
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageArrayList.add(imageUri)

            } while (cursor.moveToNext())
        }
        cursor.close()
        if(imageArrayList[0] != null){
            slide.setImageURI(imageArrayList[0])
        }
    }
}