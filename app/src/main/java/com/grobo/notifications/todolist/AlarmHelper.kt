package com.grobo.notifications.todolist

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import apps.jizzu.simpletodo.service.alarm.AlarmReceiver

class AlarmHelper private constructor() {
    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mContext: Context

    fun init(context: Context) {
        this.mContext = context
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setAlarm(task: Goal) {
        val intent = Intent(mContext, AlarmReceiver::class.java)
            .putExtra("title", task.name)
            .putExtra("time_stamp", task.timestamp)
        val pendingIntent = PendingIntent.getBroadcast(mContext, task.timestamp.toInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, task.alarm, pendingIntent)
    }

    fun removeNotification(taskTimeStamp: Long, context: Context) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskTimeStamp.toInt())
    }

    fun removeAlarm(taskTimeStamp: Long) {
        val intent = Intent(mContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(mContext, taskTimeStamp.toInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.cancel(pendingIntent)
    }

    companion object {
        private var mInstance: AlarmHelper? = null

        fun getInstance(): AlarmHelper {
            if (mInstance == null) {
                mInstance = AlarmHelper()
            }
            return mInstance as AlarmHelper
        }
    }
}
