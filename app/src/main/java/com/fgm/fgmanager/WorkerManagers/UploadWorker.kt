package com.fgm.fgmanager.WorkerManagers

import android.content.Context
import android.util.Log
import androidx.work.*
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams){
    override fun doWork(): Result {
        try {
            //val count : Int inputData.getInt(MainActivity.KEY_COUNT_VALUE, 0)
            for(i in 0..10) {
                Log.d("TAG", "WORKER is working $i")
            }
            return Result.success()

        } catch (e: Exception) {
            return Result.failure()
        }
    }

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val uploadWorkRequest: WorkRequest =
        OneTimeWorkRequestBuilder<UploadWorker>()
            .build()

    val workManager = WorkManager.getInstance(appContext).enqueue(uploadWorkRequest)


    /*            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
    */
}