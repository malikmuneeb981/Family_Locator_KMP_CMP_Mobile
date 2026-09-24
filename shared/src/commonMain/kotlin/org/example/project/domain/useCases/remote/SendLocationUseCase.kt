package org.example.project.domain.useCases.remote

import org.example.project.domain.models.remote.MyLatLng
import org.example.project.domain.repostories.remote.LocationRepo

class SendLocationUseCase(private val locationRepo: LocationRepo) {
    suspend operator fun invoke(myLatLng: MyLatLng) = locationRepo.sendUserLocation(myLatLng = myLatLng)
}