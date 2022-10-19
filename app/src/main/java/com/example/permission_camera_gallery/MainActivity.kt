package com.example.permission_camera_gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.permission_camera_gallery.databinding.ActivityMainBinding
import com.example.permission_camera_gallery.utils.MySharedPreference
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), ImageCallBack {
    private lateinit var binding: ActivityMainBinding
    private lateinit var observer: MyLifecycleObserver
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observer= MyLifecycleObserver(activityResultRegistry, this, this)
        lifecycle.addObserver(observer)

        binding.images.setOnClickListener {
            val popupMenu=
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
                    PopupMenu(
                        this,
                        it,
                        Gravity.NO_GRAVITY,
                        R.style.popupBGStyle,
                        R.style.popupBGStyle1
                    )
                }else{
                    TODO("VERSION.SDK_INT < LOLLIPOP_MR1")
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(false)
            }

            popupMenu.inflate(R.menu.my_menu_rasm_qoshish)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            }

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.kameradan->{
                        cameraPermission()
                    }
                    R.id.galereyadan->{
                        startActivityForResult(
                            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "image/*"
                            }, 1
                        )
                    }
                }
                true
            }
            popupMenu.show()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1 && resultCode==Activity.RESULT_OK){
            val uri=data?.data?:return
            binding.images.setImageURI(uri)

            MySharedPreference.init(binding.root.context)

            val l=ArrayList<String>()
            l.addAll(MySharedPreference.contactList)
            l.add(uri.toString())
            MySharedPreference.contactList=l

            val inputStream=contentResolver?.openInputStream(uri)
            val localDateTime=LocalDateTime.now()
            val file=File(filesDir, "$localDateTime images.jpg")
            val outputStream=FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun cameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {

            requestPermissions()

        } else {

            observer.selectImage()
        }
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
    }



    override fun imageSelected(photoPath: String?) {
        binding.images.setImageURI(photoPath!!.toUri())
    }


}