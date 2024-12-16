package com.wuc.lib_base.utils

import android.os.Handler
import android.os.Looper
import com.wuc.lib_base.log.WLogUtils
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * 线程池组件
 */
object AppExecutors {

    // 线程工厂，用于创建具有异常处理的后台优先级线程
    private val threadFactory = ThreadFactory {
        Thread(it).apply {
            priority = android.os.Process.THREAD_PRIORITY_BACKGROUND
            setUncaughtExceptionHandler { t, e ->
                WLogUtils.e("Thread<${t.name}> has uncaughtException", e)
            }
        }
    }

    // 用于CPU密集型任务的执行器，适用于需要大量计算的操作，如图像处理或大数据处理
    val cpuIO: Executor = CpuIOThreadExecutor(threadFactory)
    // 用于磁盘IO操作的执行器，适用于文件读写、数据库操作等磁盘访问活动
    val diskIO: Executor = DiskIOThreadExecutor(threadFactory)
    // 主线程执行器，用于执行UI操作
    val mainThread = MainThreadExecutor()

    // 主线程执行器类，提供在主线程上执行任务的方法
    class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

        // 提供延迟执行任务的方法
        fun executeDelay(command: Runnable, delayMillis: Long) {
            mainThreadHandler.postDelayed(command, delayMillis)
        }
    }

    // 磁盘IO线程执行器类，用于执行磁盘IO操作，适用于文件读写、数据库操作等磁盘访问活动
    private class DiskIOThreadExecutor(threadFactory: ThreadFactory) : Executor {

        private val diskIO = Executors.newSingleThreadExecutor(threadFactory)

        override fun execute(command: Runnable) {
            val className = Throwable().stackTrace[1]?.className ?: "Undefined"
            val methodName = Throwable().stackTrace[1]?.methodName ?: "Undefined"
            diskIO.execute(RunnableWrapper("$className#$methodName", command))
        }
    }

    // CPU IO线程执行器类，用于执行CPU密集型任务，适用于需要大量计算的操作，如图像处理或大数据处理
    private class CpuIOThreadExecutor(threadFactory: ThreadFactory) : Executor {

        private val cpuIO = ThreadPoolExecutor(
            2,
            max(2, Runtime.getRuntime().availableProcessors()),
            30,
            TimeUnit.SECONDS,
            ArrayBlockingQueue<Runnable>(128),
            threadFactory,
            object : ThreadPoolExecutor.DiscardOldestPolicy() {
                override fun rejectedExecution(r: Runnable?, e: ThreadPoolExecutor?) {
                    super.rejectedExecution(r, e)
                    WLogUtils.e("CpuIOThreadExecutor#rejectedExecution => Runnable <$r>")
                }
            }
        )

        override fun execute(command: Runnable) {
            val name = Throwable().stackTrace[1].className
            cpuIO.execute(RunnableWrapper(name, command))
        }
    }

}

// Runnable包装类，用于设置线程名称并执行Runnable
private class RunnableWrapper(private val name: String, private val runnable: Runnable) : Runnable {
    override fun run() {
        Thread.currentThread().name = name
        runnable.run()
    }
}

