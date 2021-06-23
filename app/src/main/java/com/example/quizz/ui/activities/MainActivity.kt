package com.example.quizz.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.quizz.R
import com.example.quizz.data.model.User
import com.example.quizz.databinding.ActivityMainBinding
import com.example.quizz.ui.adapters.MainScreenPagerAdapter
import com.example.quizz.ui.fragments.MainFragment
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainScreenViewModel>()
    private var firstTimeStartedFlag = true
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        viewModel.getUserFromRoomDatabase()
        //sredit jebeni flow/coroutine database access

        setUpViewPager()
        setUpTabLayout()

        viewModel.topThreePlayers.observe(this){
            binding.progressBar.visibility = View.GONE
        }
        viewModel.user.observe(this){
            if(it != null){
                this.username = it.username
                Log.d(TAG,it.username + "boktejebo")
                Log.d(TAG,"BOKTEJEBO")
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == 0 && firstTimeStartedFlag){
                    binding.progressBar.visibility = View.VISIBLE
                    firstTimeStartedFlag = false
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun setUpViewPager(){
        binding.viewPager.adapter = MainScreenPagerAdapter(this)
        if(intent.extras?.getBoolean("leaderboard") == true){
            binding.viewPager.setCurrentItem(0,false)
        } else {
            binding.viewPager.setCurrentItem(1,false)
        }
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL;
        binding.viewPager.isUserInputEnabled = true
    }

    private fun setUpTabLayout(){
        val tabIcons: ArrayList<Int> = arrayListOf(R.drawable.ic_tab_icon1,R.drawable.ic_tab_icon2,R.drawable.ic_tab_icon3)
        val tabLayoutMediator = TabLayoutMediator(tab_layout,viewPager) { tab, position ->
            tab.setIcon(tabIcons[position])
        }
        tabLayoutMediator.attach()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_CANCELED){
            if(requestCode == MainScreenViewModel.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
                CoroutineScope(Dispatchers.IO).launch {
                    try{
                        val file = createFile(username)
                        val out = FileOutputStream(file)
                        (data?.extras?.get("data") as Bitmap).compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()
                        viewModel.updatePhoto(Uri.fromFile(file))
                    } catch (e: Exception){
                        Log.d(TAG,e.message.toString())
                    }
                }

            } else if(requestCode == MainScreenViewModel.REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == Activity.RESULT_OK){
                val imageUri = data?.data
                if (imageUri != null) {
                    viewModel.updatePhoto(imageUri)
                }
            }
        }
    }

    private fun createFile(username: String): File{
        val timestamp = SimpleDateFormat.getDateTimeInstance().format(Date())
        Log.d(TAG,timestamp)
        val storageDir = getExternalFilesDir(null)
        val dir = File(storageDir?.absolutePath + "/images")
        if(!dir.exists()){
            dir.mkdir()
        }
        val photo = File(dir,"$username.jpg")
        try {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (!photo.createNewFile()) {
                    Log.d(TAG, "This file is already exist: " + photo.absolutePath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val imagePath = photo.absolutePath
        Log.d(TAG,imagePath)
        return photo
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getPhoto()
        }
    }

    fun getCurrentUsersUsername(): String{
        return username
    }

}