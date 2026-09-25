package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.EyeShieldPreferencesManager
import com.example.model.EyeShieldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EyeShieldOverlayService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private lateinit var preferencesManager: EyeShieldPreferencesManager

    override fun onCreate() {
        super.onCreate()
        preferencesManager = EyeShieldPreferencesManager.getInstance(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()

        serviceScope.launch {
            preferencesManager.state.collectLatest { state ->
                updateOverlay(state)
                updateNotification(state)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE -> {
                val currentState = preferencesManager.state.value
                preferencesManager.setFilterActive(!currentState.isFilterActive)
            }
            ACTION_STOP -> {
                preferencesManager.setFilterActive(false)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_INCREASE -> {
                val currentState = preferencesManager.state.value
                preferencesManager.setIntensity((currentState.intensity + 0.1f).coerceAtMost(1.0f))
            }
            ACTION_DECREASE -> {
                val currentState = preferencesManager.state.value
                preferencesManager.setIntensity((currentState.intensity - 0.1f).coerceAtLeast(0.0f))
            }
        }

        val notification = buildNotification(preferencesManager.state.value)
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    private fun updateOverlay(state: EyeShieldState) {
        if (!Settings.canDrawOverlays(this)) {
            removeOverlay()
            return
        }

        if (!state.isFilterActive) {
            overlayView?.visibility = View.GONE
            return
        }

        if (overlayView == null) {
            overlayView = View(this).apply {
                isClickable = false
                isFocusable = false
            }

            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )

            try {
                windowManager?.addView(overlayView, layoutParams)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        overlayView?.let { view ->
            view.visibility = View.VISIBLE
            
            // Calculate layered filter color + dimming layer
            val filterColor = state.effectiveFilterColor
            // Tint layer with alpha scaled by intensity (up to ~75% max alpha)
            val tintAlpha = (state.intensity * 0.70f * 255).toInt().coerceIn(0, 255)
            val r = (filterColor.red * 255).toInt()
            val g = (filterColor.green * 255).toInt()
            val b = (filterColor.blue * 255).toInt()
            val tintDrawable = ColorDrawable(AndroidColor.argb(tintAlpha, r, g, b))

            // Dimming black layer scaled by screenDim (up to ~80% alpha)
            val dimAlpha = (state.screenDim * 0.85f * 255).toInt().coerceIn(0, 255)
            val dimDrawable = ColorDrawable(AndroidColor.argb(dimAlpha, 0, 0, 0))

            val layerDrawable = LayerDrawable(arrayOf(tintDrawable, dimDrawable))
            view.background = layerDrawable
        }
    }

    private fun removeOverlay() {
        overlayView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            overlayView = null
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(state: EyeShieldState) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_ID, buildNotification(state))
    }

    private fun buildNotification(state: EyeShieldState): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, EyeShieldOverlayService::class.java).apply { action = ACTION_TOGGLE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val decreaseIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, EyeShieldOverlayService::class.java).apply { action = ACTION_DECREASE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val increaseIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, EyeShieldOverlayService::class.java).apply { action = ACTION_INCREASE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusText = if (state.isFilterActive) {
            val percent = (state.intensity * 100).toInt()
            "${getString(R.string.filter_status_active)} • $percent%"
        } else {
            getString(R.string.filter_status_inactive)
        }

        val toggleLabel = if (state.isFilterActive) {
            getString(R.string.pause_service)
        } else {
            getString(R.string.resume_service)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentIntent(openAppIntent)
            .setOngoing(state.isFilterActive)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_media_pause, toggleLabel, toggleIntent)
            .addAction(android.R.drawable.arrow_down_float, "-10%", decreaseIntent)
            .addAction(android.R.drawable.arrow_up_float, "+10%", increaseIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        removeOverlay()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "eye_shield_filter_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_TOGGLE = "com.example.eyeshield.ACTION_TOGGLE"
        const val ACTION_STOP = "com.example.eyeshield.ACTION_STOP"
        const val ACTION_INCREASE = "com.example.eyeshield.ACTION_INCREASE"
        const val ACTION_DECREASE = "com.example.eyeshield.ACTION_DECREASE"

        fun start(context: Context) {
            val intent = Intent(context, EyeShieldOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, EyeShieldOverlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
