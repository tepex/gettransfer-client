package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.pm.PackageManager
import android.content.Intent

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import android.support.v7.app.AppCompatActivity

import timber.log.Timber

class SplashActivity: AppCompatActivity() {
	companion object {
		@JvmField val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
		@JvmField val PERMISSION_REQUEST = 2211
	}
	
	@CallSuper
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		/*
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
		   (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
		    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
		    */
				
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && 
			(!check(Manifest.permission.ACCESS_FINE_LOCATION) || 
			 !check(Manifest.permission.ACCESS_COARSE_LOCATION))) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST)
			// show splash
			Timber.d("Splash screen")
			return
		}

		Timber.d("Permissions granted!")
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
	
	private fun check(permission: String) =
		ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		if(requestCode != PERMISSION_REQUEST) return
		if(grantResults.size == 2 && 
			grantResults[0] == PackageManager.PERMISSION_GRANTED &&
			grantResults[1] == PackageManager.PERMISSION_GRANTED) recreate()
		else finish()
	}
}
