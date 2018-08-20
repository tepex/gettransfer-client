package com.kg.gettransfer.data.repository

import android.content.Context
import android.location.Location

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

import com.google.android.gms.location.FusedLocationProviderClient

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.LocationRepository

import kotlin.coroutines.experimental.suspendCoroutine
import kotlin.system.measureTimeMillis

import timber.log.Timber

class LocationRepositoryImpl(val locationProviderClient: FusedLocationProviderClient,
	                         val googleApiAvailability: GoogleApiAvailability,
	                         val context: Context): LocationRepository {

	private var googleApiAvailable = true
	
	override fun checkPlayServicesAvailable(): Boolean {
		val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
		googleApiAvailable = (status == ConnectionResult.SUCCESS)
		return googleApiAvailable
	}
	
	fun getPlayServicesStatus(): Int = googleApiAvailability.isGooglePlayServicesAvailable(context)
	
	override suspend fun getCurrentLocation(): Result<Point> {
		val location: Location? = try {
			suspendCoroutine { cont ->
				locationProviderClient.lastLocation
					.addOnSuccessListener { location: Location? -> cont.resume(location) }
					.addOnFailureListener { cont.resumeWithException(it) }
			}
		}
		catch(e: Exception) {
			return Result(error = e)
		}
		return if(location != null) Result(Point(location.latitude, location.longitude))
		else Result(error = RuntimeException("Location not found"))
	}
}
