package org.example.project.commons

interface GetNativeLatLongInfo{
   suspend fun getLatLongInfo(lat:Double,long: Double): String?
}