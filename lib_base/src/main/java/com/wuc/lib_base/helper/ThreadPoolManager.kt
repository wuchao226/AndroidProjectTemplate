package com.wuc.lib_base.helper

import androidx.annotation.IntRange
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author: wuc
 * @date: 2025/5/12
 * @description: 线程池管理类, 提供针对不同场景优化的线程池实现
 */
class ThreadPoolManager private constructor() {

    companion object {
        @Volatile
        private var instance: ThreadPoolManager? = null
        fun get(): ThreadPoolManager = instance ?: synchronized(this) {
            instance ?: ThreadPoolManager().also { instance = it }
        }

        // 定义任务优先级常量
        const val PRIORITY_HIGH = 10
        const val PRIORITY_NORMAL = 5
        const val PRIORITY_LOW = 1
    }

    // CPU密集型线程池
    private val cpuThreadPool: ThreadPoolExecutor

    // IO密集型线程池
    private val ioThreadPool: ThreadPoolExecutor

    // 定时任务线程池
    private val scheduledThreadPool: ScheduledThreadPoolExecutor

    // 优先级线程池
    private val priorityThreadPool: ThreadPoolExecutor

    init {
        val cpuCount = Runtime.getRuntime().availableProcessors()

        // 初始化CPU密集型线程池
        cpuThreadPool = ThreadPoolExecutor(
            cpuCount + 1, // 核心线程数
            cpuCount + 1, // 最大线程数
            30L, // 空闲线程存活时间
            TimeUnit.SECONDS, // 时间单位
            ArrayBlockingQueue(128), // 任务队列
            NamedThreadFactory("cpu-pool"), // 线程工厂
            ThreadPoolExecutor.CallerRunsPolicy() // 主线程兜底，防止雪崩
        ).apply {
            // 允许核心线程超时
            allowCoreThreadTimeOut(true)
        }

        // 初始化IO密集型线程池
        val maxThreads = maxOf(cpuCount * 2, 16) // 至少16个线程
        ioThreadPool = ThreadPoolExecutor(
            cpuCount * 2,
            maxThreads,
            30L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(256),
            NamedThreadFactory("io-pool"),
            CustomRejectedExecutionHandler()
        )

        // 初始化定时任务线程池
        scheduledThreadPool = ScheduledThreadPoolExecutor(
            cpuCount,
            NamedThreadFactory("schedule-pool"),
            ThreadPoolExecutor.DiscardPolicy()
        )
        // 初始化优先级线程池 - 使用PriorityBlockingQueue实现任务优先级
        priorityThreadPool = ThreadPoolExecutor(
            cpuCount,
            maxOf(cpuCount * 2, 8),
            30L,
            TimeUnit.SECONDS,
            PriorityBlockingQueue(128), // 优先级队列
            NamedThreadFactory("priority-pool"),
            CustomRejectedExecutionHandler()
        )
    }

    // 执行CPU密集型任务
    fun executeCpuTask(task: Runnable) {
        cpuThreadPool.execute(task)
    }

    // 执行IO密集型任务
    fun executeIoTask(task: Runnable) {
        ioThreadPool.execute(task)
    }

    // 执行定时任务
    fun scheduleTask(task: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        return scheduledThreadPool.schedule(task, delay, unit)
    }

    /**
     * 提交带优先级的任务
     * @param task 任务
     * @param priority 优先级 (值越大优先级越高)
     */
    fun executePriorityTask(task: Runnable, @IntRange(from = 0, to = 10) priority: Int = PRIORITY_NORMAL) {
        val priorityTask = PriorityRunnable(task, priority)
        priorityThreadPool.execute(priorityTask)
    }

    /**
     * 提交带优先级的任务并返回Future
     * @param task 任务
     * @param priority 优先级 (值越大优先级越高)
     * @return Future对象
     */
    fun <T> submitPriorityTask(task: Callable<T>, @IntRange(from = 0, to = 10) priority: Int = PRIORITY_NORMAL): Future<T> {
        val priorityTask = PriorityCallable(task, priority)
        return priorityThreadPool.submit(priorityTask)
    }

    /**
     * 提交高优先级的CPU密集型任务
     */
    fun executeHighPriorityCpuTask(task: Runnable) {
        executePriorityTask(task, PRIORITY_HIGH)
    }

    /**
     * 提交低优先级的IO密集型任务
     */
    fun executeLowPriorityIoTask(task: Runnable) {
        executePriorityTask(task, PRIORITY_LOW)
    }

    // 添加动态调整线程池参数的方法
    fun adjustThreadPoolSize(cpuCoreSize: Int? = null, ioCoreSize: Int? = null, ioMaxSize: Int? = null) {
        cpuCoreSize?.let {
            cpuThreadPool.corePoolSize = it
            cpuThreadPool.maximumPoolSize = it
        }

        ioCoreSize?.let {
            ioThreadPool.corePoolSize = it
        }

        ioMaxSize?.let {
            ioThreadPool.maximumPoolSize = it
        }
    }


    // 关闭线程池
    fun shutdown() {
        cpuThreadPool.shutdown()
        ioThreadPool.shutdown()
        scheduledThreadPool.shutdown()
        priorityThreadPool.shutdown()
        // 可选：等待终止
        try {
            cpuThreadPool.awaitTermination(60, TimeUnit.SECONDS)
            ioThreadPool.awaitTermination(60, TimeUnit.SECONDS)
            scheduledThreadPool.awaitTermination(60, TimeUnit.SECONDS)
            priorityThreadPool.awaitTermination(60, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt() // 恢复中断状态
        }
    }

    // 获取线程池状态
    fun getPoolStatus(): String {
        return """
            CPU Pool: [核心线程数:${cpuThreadPool.corePoolSize}, 活跃线程:${cpuThreadPool.activeCount}, 完成任务:${cpuThreadPool.completedTaskCount}, 队列任务:${cpuThreadPool.queue.size}]
            IO Pool: [核心线程数:${ioThreadPool.corePoolSize}, 活跃线程:${ioThreadPool.activeCount}, 完成任务:${ioThreadPool.completedTaskCount}, 队列任务:${ioThreadPool.queue.size}]
        """.trimIndent()
    }

    // 自定义线程工厂
    private class NamedThreadFactory(private val namePrefix: String) : ThreadFactory {
        private val threadNumber = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            val thread = Thread(r, "$namePrefix-${threadNumber.getAndIncrement()}")
            // 设置为非守护线程
            if (thread.isDaemon) {
                thread.isDaemon = false
            }
            // 设置普通优先级
            thread.priority = Thread.NORM_PRIORITY
            return thread
        }
    }

    /**
     * 自定义拒绝策略
     * 实现任务降级、重试和日志记录
     */
    private class CustomRejectedExecutionHandler : RejectedExecutionHandler {
        // 记录拒绝次数，用于监控
        private val rejectionCount = AtomicInteger(0)

        override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
            val count = rejectionCount.incrementAndGet()

            try {
                // 尝试将任务放入队列，最多等待100ms
                if (!executor.queue.offer(r, 100, TimeUnit.MILLISECONDS)) {
                    // 如果仍然失败，记录日志并降级处理
                    logRejection(r, count)

                    // 判断线程池负载情况
                    if (executor.activeCount < executor.maximumPoolSize * 0.9) {
                        // 如果负载不是特别高，尝试再次提交
                        try {
                            // 短暂延迟后重试
                            Thread.sleep(50)
                            executor.execute(r)
                            return
                        } catch (e: Exception) {
                            // 重试失败，继续降级处理
                        }
                    }

                    // 降级处理：在当前线程执行（类似CallerRunsPolicy）
                    // 但增加了异常保护，避免影响主线程
                    try {
                        r.run()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: InterruptedException) {
                // 线程被中断，记录日志
                Thread.currentThread().interrupt()
                logRejection(r, count)
            }
        }

        private fun logRejection(r: Runnable, count: Int) {
            println("任务被拒绝(总计$count 次): $r")
            // 实际项目中应该使用正式的日志框架记录
            // 也可以考虑添加监控上报逻辑
        }
    }

    /**
     * 带优先级的Runnable包装类
     */
    private class PriorityRunnable(
        private val runnable: Runnable,
        private val priority: Int
    ) : Runnable, Comparable<PriorityRunnable> {

        override fun run() {
            runnable.run()
        }

        override fun compareTo(other: PriorityRunnable): Int {
            // 优先级高的排在前面（降序）
            return other.priority - this.priority
        }
    }

    /**
     * 带优先级的Callable包装类
     */
    private class PriorityCallable<T>(
        private val callable: Callable<T>,
        private val priority: Int
    ) : Callable<T>, Comparable<PriorityCallable<*>> {

        override fun call(): T {
            return callable.call()
        }

        override fun compareTo(other: PriorityCallable<*>): Int {
            // 优先级高的排在前面（降序）
            return other.priority - this.priority
        }
    }
}