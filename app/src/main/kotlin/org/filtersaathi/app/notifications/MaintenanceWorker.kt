package org.filtersaathi.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Daily background worker to evaluate water filter maintenance schedules
 * and dispatch notifications for filters requiring attention.
 */
class MaintenanceWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.checkAndNotifyMaintenance(applicationContext)
        return Result.success()
    }
}
