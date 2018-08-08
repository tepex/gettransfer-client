package com.kg.gettransfer.data.repository

import android.content.Context
import android.location.Location

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient

import com.kg.gettransfer.domain.repository.LocationRepository

import timber.log.Timber

class LocationRepositoryImpl(val locationProviderClient: FusedLocationProviderClient,
	                         val googleApiAvailability: GoogleApiAvailability,
	                         val context: Context): LocationRepository {

	private var googleApiAvailable = true
	
	override fun checkPlayServicesAvailable(): Boolean {
		val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
		googleApiAvailable = (status == ConnectionResult.SUCCESS)
		Timber.d("checking GPS availability: $googleApiAvailable")
		return googleApiAvailable
	}
	
	fun getPlayServicesStatus(): Int = googleApiAvailability.isGooglePlayServicesAvailable(context)
	
	/*
	
	override fun getCurrentLocation(): Single<Point>
	{
		return getLastLocation().map(
		{
			location ->	Point(location.getLatitude(), location.getLongitude()) 
		})
	}

	
	
	private fun getLastLocation(): Single<Location>
	{
		return Single.create(
		{
			emitter -> 
				try
				{
					locationProviderClient.getLastLocation()
						.addOnSuccessListener(
						{
							location ->
								if(emitter.isDisposed()) return@addOnSuccessListener
								
								if(location != null) emitter.onSuccess(location)
								else emitter.onError(UnknownLocationException("Last location is unknown"))
						})
						.addOnFailureListener({ ex -> emitter.onError(ex) })
				}
				catch(e:SecurityException)
				{
					emitter.onError(e)
				}
		})
	}
	*/
}
