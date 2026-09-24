package org.example.project.domain.useCases.remote

import org.example.project.domain.repostories.remote.LocationRepo

class ObserveAllFriendsLatLongsUseCase(
    private val locationRepo: LocationRepo
) {
    operator fun invoke() = locationRepo.observeMessages()
}