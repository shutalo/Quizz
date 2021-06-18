package com.example.quizz.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.ArrayMap
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.viewpager2.widget.ViewPager2
import com.example.quizz.R
import com.example.quizz.data.model.User
import com.example.quizz.data.repository.Repository
import com.example.quizz.databinding.ActivityMainBinding
import com.example.quizz.ui.adapters.MainScreenPagerAdapter
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainScreenViewModel>()
    private var firstTimeStartedFlag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        setUpViewPager()
        setUpTabLayout()

        viewModel.topThreePlayers.observe(this){
            binding.progressBar.visibility = View.GONE
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
        binding.viewPager.setCurrentItem(1,false)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_CANCELED){
            if(requestCode == MainScreenViewModel.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
//                val bitmap: Bitmap = data?.extras?.get("data") as Bitmap
//                viewModel.updatePhoto(createScaledBitmap(bitmap,150,150,true))
            } else if(requestCode == MainScreenViewModel.REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == Activity.RESULT_OK){
                val imageUri = data?.data
                if (imageUri != null) {
                    viewModel.updatePhoto(imageUri)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getPhoto()
        }
    }


}