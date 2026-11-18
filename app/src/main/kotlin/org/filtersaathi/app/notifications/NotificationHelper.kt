package org.filtersaathi.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.filtersaathi.app.R
import org.filtersaathi.app.domain.model.SampleFilter
import org.filtersaathi.app.ui.MainActivity
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationHelper {

    const val CHANNEL_REQUEST_UPDATES = "channel_request_updates"
    const val CHANNEL_MAINTENANCE_REMINDERS = "channel_maintenance_reminders"

    const val EXTRA_NAVIGATE_TO = "extra_navigate_to"
    const val DESTINATION_TRACK = "track"

    private const val PREFS_NAME = "filtersaathi_notification_prefs"
    private const val KEY_REMINDERS_ENABLED = "pref_reminders_enabled"
    private const val KEY_LAST_STATUS_PREFIX = "pref_last_maint_status_"

    private const val WORK_NAME_DAILY_MAINTENANCE = "filter_maintenance_daily"

    @JvmStatic
    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun isRemindersEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REMINDERS_ENABLED, false)
    }

    @JvmStatic
    fun setRemindersEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REMINDERS_ENABLED, enabled).apply()
        if (enabled) {
            scheduleDailyMaintenanceWorker(context)
        } else {
            cancelMaintenanceWorker(context)
        }
    }

    /**
     * Initializes notification channels with bilingual title and description resources.
     */
    @JvmStatic
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            // Channel 1: Request updates
            val channelRequests = NotificationChannel(
                CHANNEL_REQUEST_UPDATES,
                context.getString(R.string.ui_notif_channel_requests),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.ui_notif_channel_requests_desc)
                enableLights(true)
                enableVibration(true)
            }

            // Channel 2: Maintenance reminders
            val channelMaintenance = NotificationChannel(
                CHANNEL_MAINTENANCE_REMINDERS,
                context.getString(R.string.ui_notif_channel_maint),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.ui_notif_channel_maint_desc)
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(channelRequests)
            notificationManager.createNotificationChannel(channelMaintenance)
        }
    }

    /**
     * Dispatches a stage change notification for the given booking request.
     */
    @JvmStatic
    fun postStageUpdateNotification(context: Context, requestNumber: String, stageIndex: Int) {
        if (!isRemindersEnabled(context)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val stageNameRes = when (stageIndex) {
            1 -> R.string.ui_stg1
            2 -> R.string.ui_stg2
            3 -> R.string.ui_stg3
            4 -> R.string.ui_stg4
            5 -> R.string.ui_stg5
            6 -> R.string.ui_stg6
            7 -> R.string.ui_stg_done
            else -> R.string.ui_stg_done
        }
        val stageName = context.getString(stageNameRes)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO, DESTINATION_TRACK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestNumber.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(R.string.ui_notif_stage_title)
        val body = context.getString(R.string.ui_notif_stage_body, requestNumber, stageName)

        val notification = NotificationCompat.Builder(context, CHANNEL_REQUEST_UPDATES)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(requestNumber.hashCode(), notification)
    }

    /**
     * Checks sample filter service intervals and posts notifications if status has changed.
     * Enforces quiet hours between 21:00 and 07:00 unless ignoreQuietHours = true (for debug/demo).
     */
    @JvmStatic
    fun checkAndNotifyMaintenance(
        context: Context,
        force: Boolean = false,
        ignoreQuietHours: Boolean = false,
        filters: List<SampleFilter>? = null
    ) {
        if (!isRemindersEnabled(context) && !force) return

        if (!ignoreQuietHours) {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (MaintenanceReminderRule.isQuietHour(currentHour)) {
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val sampleFilters = filters ?: listOf(
            SampleFilter("FS-FLT-101", "Hosahalli", "2025-11-20", 5),
            SampleFilter("FS-FLT-102", "Chikkanahalli", "2026-01-10", 18),
            SampleFilter("FS-FLT-103", "Doddabele", "2026-03-01", 75)
        )

        val prefs = getPrefs(context)
        val notificationManager = NotificationManagerCompat.from(context)

        for (filter in sampleFilters) {
            val status = MaintenanceReminderRule.evaluateStatus(filter.daysUntilService)
            val key = KEY_LAST_STATUS_PREFIX + filter.filterId
            val lastStatus = prefs.getString(key, null)

            if (force || status.name != lastStatus) {
                when (status) {
                    MaintenanceStatus.URGENT -> {
                        val title = context.getString(R.string.ui_notif_maint_urgent_title)
                        val body = context.getString(
                            R.string.ui_notif_maint_urgent_body,
                            filter.filterId,
                            filter.villageName,
                            filter.daysUntilService
                        )
                        val notification = NotificationCompat.Builder(context, CHANNEL_MAINTENANCE_REMINDERS)
                            .setSmallIcon(R.drawable.ic_notifications)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify((filter.filterId + "_maint").hashCode(), notification)
                        prefs.edit().putString(key, status.name).apply()
                    }
                    MaintenanceStatus.SOON -> {
                        val title = context.getString(R.string.ui_notif_maint_soon_title)
                        val body = context.getString(
                            R.string.ui_notif_maint_soon_body,
                            filter.filterId,
                            filter.villageName,
                            filter.daysUntilService
                        )
                        val notification = NotificationCompat.Builder(context, CHANNEL_MAINTENANCE_REMINDERS)
                            .setSmallIcon(R.drawable.ic_notifications)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify((filter.filterId + "_maint").hashCode(), notification)
                        prefs.edit().putString(key, status.name).apply()
                    }
                    MaintenanceStatus.OK -> {
                        prefs.edit().putString(key, status.name).apply()
                    }
                }
            }
        }
    }

    /**
     * Schedules daily background maintenance check via WorkManager.
     */
    @JvmStatic
    fun scheduleDailyMaintenanceWorker(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<MaintenanceWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_DAILY_MAINTENANCE,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Cancels daily background maintenance check.
     */
    @JvmStatic
    fun cancelMaintenanceWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DAILY_MAINTENANCE)
    }
}
