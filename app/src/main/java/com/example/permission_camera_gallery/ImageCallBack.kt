package com.example.permission_camera_gallery

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.*


interface ImageCallBack{
    fun imageSelected(photoPath: String?)
}

class MyLifecycleObserver(
    private val registry: ActivityResultRegistry,
    val imageCallBack: ImageCallBack,
    val context: Context
): DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<Uri>
    var photoPath:String?=null

    override fun onCreate(owner: LifecycleOwner) {
        getContent=
            registry.register("key", owner, ActivityResultContracts.TakePicture()){ bool->
                if (bool){
                    imageCallBack.imageSelected(photoPath)
                }
            }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun imageSelect(){
        val photoURI: Uri = FileProvider.getUriForFile(
            context,
            "com.example.camera.gallery",
            createImageFile()
        )
        getContent.launch(photoURI)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            photoPath = absolutePath
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun selectImage() {
        val photoURI: Uri = FileProvider.getUriForFile(
            context,
            "com.example.cameragallery",
            createImageFile()
        )
        getContent.launch(photoURI)
    }


}