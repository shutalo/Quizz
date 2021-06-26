package com.example.quizz.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.databinding.ActivityWelcomeBinding
import com.example.quizz.ui.fragments.ChangePasswordFragment
import com.example.quizz.ui.fragments.ChooseUsernameFragment
import com.example.quizz.ui.fragments.LoginFragment
import com.example.quizz.ui.fragments.RegisterFragment
import com.example.quizz.ui.viewmodels.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.ArrayList
import java.util.jar.Manifest

class WelcomeActivity :  AppCompatActivity(), EasyPermissions.PermissionCallbacks{

    private val TAG = "WelcomeActivity"
    private val viewModel by viewModel<RegisterViewModel>()
    private val loginFragment: LoginFragment = LoginFragment()
    private val registerFragment: RegisterFragment = RegisterFragment()
    private val changePasswordFragment: ChangePasswordFragment = ChangePasswordFragment()
    private val chooseUsernameFragment: ChooseUsernameFragment = ChooseUsernameFragment()

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        this.supportActionBar?.hide()

        Log.d(TAG,"Started")
        //check for permissions
        askForPermissions()


        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        registerFragment.setUpLoginButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,loginFragment).commit()
        }
        loginFragment.setUpRegisterButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container, registerFragment).commit()
        }
        loginFragment.setUpChangePasswordListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,changePasswordFragment).commit()
        }
        changePasswordFragment.setUpBackToLoginListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,loginFragment).commit()
        }

        viewModel.isUsernameChosen.observe(this){
            if(it){
                startMainActivity()
            }
        }
        viewModel.isPasswordChangeRequested.observe(this){
            if(it) {
                Log.d(TAG,"password change requested")
                supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container, loginFragment).commit()
            }
        }
        viewModel.isUserRegisteredSuccessfully.observe(this){
            if(it){
                supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,chooseUsernameFragment).commit()
            }
        }
        viewModel.isSigningInSuccessful.observe(this){
            if(it){
                startMainActivity()
            }
        }
        viewModel.isUserSignedIn.observe(this){
            if(it == true){
                startMainActivity()
            } else if(it == false){
                supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container, loginFragment).commit()
            }
        }
    }

    private fun startMainActivity(){
        CoroutineScope(Dispatchers.IO).launch {
            val intent: Intent = Intent(Quizz.context,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkIfUserIsSignedIn()
    }

    //@AfterPermissionGranted(Companion.REQUEST_CODE_PERMISSIONS)
    private fun askForPermissions(){

        if(EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA,android.Manifest.permission.INTERNET,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.d(TAG,"permissions already granted!",)
            Toast.makeText(this,"permission granted already!",Toast.LENGTH_SHORT).show()
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions for managing your account and progress", Companion.REQUEST_CODE_PERMISSIONS, android.Manifest.permission.CAMERA,android.Manifest.permission.INTERNET,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

//        if(EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)){
//            Log.d(TAG,"Camera permissions already granted!",)
//            Toast.makeText(this,"Camera permission granted already!",Toast.LENGTH_SHORT).show()
//        } else {
//            EasyPermissions.requestPermissions(this, "We need permissions for managing your account and progress", Companion.REQUEST_CODE_PERMISSIONS, android.Manifest.permission.CAMERA)
//        }
//
//        if(EasyPermissions.hasPermissions(this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE)){
//            Log.d(TAG,"Read external storage permission already granted!",)
//            Toast.makeText(this,"Read external storage permissions granted already!",Toast.LENGTH_SHORT).show()
//        } else {
//            EasyPermissions.requestPermissions(this,
//                "We need permissions for managing your account and progress",
//                Companion.REQUEST_CODE_PERMISSIONS,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//
//        if(EasyPermissions.hasPermissions(this,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            Log.d(TAG,"Write external storage permission already granted!",)
//            Toast.makeText(this,"Write external storage permissions granted already!",Toast.LENGTH_SHORT).show()
//        } else {
//            EasyPermissions.requestPermissions(this,
//                "We need permissions for managing your account and progress",
//                Companion.REQUEST_CODE_PERMISSIONS,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//
//        if(EasyPermissions.hasPermissions(this,
//                android.Manifest.permission.INTERNET)){
//            Log.d(TAG,"Internet permission already granted!",)
//            Toast.makeText(this,"Internet permissions granted already!",Toast.LENGTH_SHORT).show()
//        } else {
//            EasyPermissions.requestPermissions(this,
//                "We need permissions for managing your account and progress",
//                Companion.REQUEST_CODE_PERMISSIONS,
//                android.Manifest.permission.INTERNET)
//        }
//
//        if(EasyPermissions.hasPermissions(this,
//                android.Manifest.permission.ACCESS_NETWORK_STATE)){
//            Log.d(TAG,"Access network state permission already granted!",)
//            Toast.makeText(this,"Access network state permissions granted already!",Toast.LENGTH_SHORT).show()
//        } else {
//            EasyPermissions.requestPermissions(this,
//                "We need permissions for managing your account and progress",
//                Companion.REQUEST_CODE_PERMISSIONS,
//                android.Manifest.permission.ACCESS_NETWORK_STATE)
//        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG,"permissions granted!",)
        Toast.makeText(this,"permissions granted!",Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            Log.d(TAG,"permissions denied!",)
            AppSettingsDialog.Builder(this).build().show()
            Toast.makeText(this,"permissions denied!",Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            askForPermissions()
        }
    }
}