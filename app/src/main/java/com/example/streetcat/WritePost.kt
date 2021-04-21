package com.example.streetcat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_cat_add.*
import kotlinx.android.synthetic.main.activity_cat_add.input_catPicture
import kotlinx.android.synthetic.main.activity_write_post.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class WritePost : AppCompatActivity() {
    var uriPhoto : Uri? = null

    // 카메라 권한 요청 및 권한 체크
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLERY_TAKE = 2
    lateinit var currentPhotoPath : String

    //갤러리 열기
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_TAKE)
    }


    // onActivityResult 로 이미지 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode){
            // 갤러리를 통해 이미지 가져오는 경우
            2 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_TAKE) {
                    uriPhoto = data?.data

                    // ImageButton 에 가져온 이미지 set
                    input_catPicture.setImageURI(uriPhoto)
                }
            }
        }
    }
    // 카메라 권한 요청
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE)
    }

    // 카메라 권한 체크
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    // 권한요청 결과
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0])
        }else{
            Log.d("TAG", "Not Permission")
        }
    }

    // 카메라 열기
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                // 찍은 사진을 그림파일로 만들기
                val photoFile: File? =
                        try {
                            createImageFile()
                        } catch (ex: IOException) {
                            Log.d("TAG", "그림파일 만드는도중 에러생김")
                            null
                        }

                // 그림파일을 성공적으로 만들었다면 onActivityForResult로 보내기
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this, "com.example.streetcat.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    // 카메라로 촬영한 이미지를 파일로 저장해준다
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("posts").push()  ////////// 새 하위 요소마다 고유 키를 생성하는 push 함수 -> 타임스탬프 시준이므로 시간순으로 자동 정렬된다고 함

        input_catPicture.setOnClickListener{
            if(checkPersmission()){
                openGalleryForImage()
            }else{
                requestPermission()
            }
        }

        writePost_button.setOnClickListener{
            val contents = postContents.editableText.toString()
            val username : String = "pompom_love" // 글쓴 유저 이름 로그인 정보에서 가져와야 할듯

            var imgFileName = username.toString() + ".png"

            var fbStorage = FirebaseStorage.getInstance()

            var storageRef = fbStorage.reference.child(username).child("main").child(imgFileName)
            storageRef.putFile(uriPhoto!!).addOnSuccessListener {
                var img_url = it.uploadSessionUri.toString()

                ref.child("picture").setValue(img_url)
            }

            val info_post = PostClass(username, 0, 0, contents)
            ref.setValue(info_post)

            Toast.makeText(applicationContext, "게시글이 등록되었습니다", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, PostActivity::class.java) // 완성된 post 페이지로 이동하든지, 아니면 post Fragment로 이동하든지. Fragment로 이동하는 방법을 몰라서 일단은 post activity로 이동
            startActivity(intent)
        }
    }
}