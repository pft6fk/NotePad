package com.example.notepad

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notepad.database.MyDbManager
import com.example.notepad.database.MyItemConstants
import com.example.notepad.databinding.ActivityEditBinding
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*


class EditActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditBinding
    val myDbManager = MyDbManager(this)
    val imageRequestCode = 10
    var id = 0
    var tempImageURI = "empty"

    var isEditState = false
//    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//        result: ActivityResult ->
//            if(result.resultCode == Activity.RESULT_OK && result.resultCode == imageRequestCode){
//                //
//                    val data: Intent? = result.data
//                binding.imageView.setImageURI(data?.data)
//            }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        binding.addPic.setOnClickListener{
            showImageContainer()
        }

        binding.imageEdit.setOnClickListener {
            onClickChooseImage()
        }

        binding.save.setOnClickListener {
            onCheckContent()
        }

        binding.imageDelete.setOnClickListener {
            deleteImage()
        }

        getMyIntent()
    }

    //попробовал через resultLauncher но там не выгружало картинку, поэтому пришлось пользоваться ею
    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode){
            //here android generates temporarily link for it to make some operations (like copy)
//            binding.imageView.setImageURI(data?.data)
            //перезаписывается переменная чтобы далее внести в БД
            tempImageURI = data?.data.toString()
            binding.imageViewResult.setImageURI(Uri.parse(tempImageURI))
       //next line means for making query with persistable ling for system which provides permanent link
            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            ContentResolver(this).takePersistableUriPermission(Uri.parse(tempImageURI), intent.flags )
        }
    }

    private fun onClickChooseImage() {
        //intent is for opening gallery
        //this intent is opening app which can open img
        //ACTION_PICK takes only temporary link for picture
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        //this is for showing all images
        intent.type = "image/*"
//попробовал через resultLauncher но там не выгружало картинку, поэтому пришлось пользоваться ею
        startActivityForResult(intent, imageRequestCode)
//        resultLauncher.launch(intent, ActivityOptionsCompat.makeBasic())
    }

    private fun showImageContainer() {
        binding.imageContainer.visibility = View.VISIBLE
        binding.addPic.visibility = View.GONE
    }

    //function for checking if user here for editing or creating note
    fun onCheckContent() {
        val myTitle = binding.edTitle.text.toString()
        val myContent = binding.edContent.text.toString()

        if (myContent != "" && myContent != "") {
            if (isEditState) {//checking is it was opened for editing or not
                myDbManager.updateItem(myTitle, myContent, tempImageURI, id, getCurrentTime())
            } else {
                myDbManager.insertDb(myTitle, myContent, tempImageURI, getCurrentTime())
            }
            finish()
        } else {
            Toast.makeText(applicationContext, "empty", Toast.LENGTH_SHORT).show()
        }
    }

    //deletes image from note and DB
    private fun deleteImage() {
        binding.imageContainer.visibility = View.GONE
        binding.addPic.visibility = View.VISIBLE
        tempImageURI = "empty"
        if (tempImageURI == "empty")
            return
    }


    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    //function for editing the content of recycler
    fun getMyIntent() {
        val i = intent
        //when data comes within intent, it will work
        if (i != null) {
            //intent is not null, so we check if the content of recycler is null (does it have data or not)
            //getStringExtra() is used because we have passed parameters by using putExtra() with STRING VALUES
            if (i.getStringExtra(MyItemConstants.I_TITLE_KEY) != null) {
                binding.addPic.visibility = View.GONE
                binding.editContent.visibility = View.VISIBLE

                binding.editContent.setOnClickListener {
                    editContent()
                }
                //to identify if we want to rewrite(edit) or create note
                isEditState = true

                //not allow to modify by default
                binding.edTitle.isEnabled = false
                binding.edContent.isEnabled = false

                //puts the data inside the editText inside of EditActivity()
                binding.edTitle.setText(i.getStringExtra(MyItemConstants.I_TITLE_KEY))
                binding.edContent.setText(i.getStringExtra(MyItemConstants.I_CONTENT_KEY))
                id = i.getIntExtra(MyItemConstants.I_ID_KEY, 0)
                //in case if link is not empty it shows the image
                if (i.getStringExtra(MyItemConstants.I_URI_KEY) != "empty") {
                    binding.imageContainer.visibility = View.VISIBLE
                    tempImageURI = i.getStringExtra(MyItemConstants.I_URI_KEY)!!
                    binding.imageViewResult.setImageURI(Uri.parse(tempImageURI))
                    binding.imageEdit.visibility = View.GONE
                    binding.imageDelete.visibility = View.GONE

                }

            }
        }
    }

    //function to allow to edit the whole content
    private fun editContent() {
        binding.imageDelete.visibility = View.VISIBLE
        binding.imageEdit.visibility = View.VISIBLE
        binding.editContent.visibility = View.GONE
        binding.addPic.visibility = View.VISIBLE
        binding.edTitle.isEnabled = true
        binding.edContent.isEnabled = true
    }

    private fun getCurrentTime(): String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())

        return formatter.format(time)
    }
}