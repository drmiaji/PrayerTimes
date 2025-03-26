package com.drmiaji.prayertimes.domain.usescase


import com.drmiaji.prayertimes.domain.repository.LocalPermissionManagerRepository
import javax.inject.Inject

class SkipButtonUseCase @Inject constructor(
    private val localPermissionManager: LocalPermissionManagerRepository
) {

    suspend operator fun invoke(isEnabled: Boolean) {
        localPermissionManager.setSkipButtonState(isEnabled)
    }

     fun isSkipButtonEnabled(): Boolean {
        return localPermissionManager.isSkipButtonEnabled()
    }
}