package com.drmiaji.prayertimes.domain.usescase


import com.drmiaji.prayertimes.domain.repository.LocalPermissionManagerRepository
import javax.inject.Inject

class NotificationStatusUseCase @Inject constructor(
    private val repository: LocalPermissionManagerRepository
) {

    suspend operator fun invoke(status: String) {
        repository.setNotificationPermissionStatus(status)
    }
}