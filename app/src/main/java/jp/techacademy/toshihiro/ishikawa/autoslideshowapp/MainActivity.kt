package jp.techacademy.toshihiro.ishikawa.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.database.Cursor
import android.os.Handler
import android.widget.Toast
import java.util.*

 class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var cursor:Cursor?=null

     private var mTimer: Timer? = null

     private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("ANDROID", "許可されている")
                getContentsinfo()
            } else {
                Log.d("ANDROID", "許可されていない")
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }


        nextButton.setOnClickListener {
            if (onButton.text == "再生") {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    getNextInfo()
                } else {
                    Log.d("ANDROID", "許可されていない")
                    Toast.makeText(this, "今は押せません.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "今は押せません.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getPreviousInfo()
            } else {
                Log.d("ANDROID", "許可されていない")
                Toast.makeText(this, "今は押せません.", Toast.LENGTH_SHORT).show()
            }
        }


        onButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (onButton.text == "再生") {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
                                getNextInfo()
                            }
                        }
                    }, 2000, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定
                    onButton.text = "停止"
                } else {
                    mTimer!!.cancel()
                    onButton.text = "再生"
                }
            } else {
                Log.d("ANDROID", "許可されていない")
                Toast.makeText(this, "今は押せません.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("ANDROID", "許可された")
//        val resolver = contentResolver
//        val cursor = resolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            null,
//            null,
//            null,
//            null
//        )
                    getContentsinfo()
                } else {
                    Log.d("ANDROID", "許可されなかった")
                }
        }
    }

    private fun getContentsinfo() {
        if (MediaStore.Images.Media.EXTERNAL_CONTENT_URI==null) {
            Toast.makeText(this, "File Nothing!", Toast.LENGTH_SHORT).show()
          }else{

            val resolver = contentResolver

            cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )


            if (cursor!!.moveToFirst()) {
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUris = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageView.setImageURI(imageUris)
            }
        }
    }

    private fun getNextInfo() {

            if (!cursor!!.isLast) {
                cursor!!.moveToNext()

                val fieldIndex: Int = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id: Long = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            } else {
                cursor!!.moveToFirst()
                val fieldIndex: Int = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id: Long = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
    }

    private fun getPreviousInfo() {

        if (onButton.text=="再生") {
    if (!cursor!!.isFirst()) {
        cursor!!.moveToPrevious()
        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor!!.getLong(fieldIndex)
        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageView.setImageURI(imageUri)
    } else {
        cursor!!.moveToLast()
        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor!!.getLong(fieldIndex)
        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageView.setImageURI(imageUri)
    }
}else{
    Toast.makeText(this, "今は押せません.", Toast.LENGTH_SHORT).show()
}
    }
}
