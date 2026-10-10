package mc.xalitoria.util.bukkit

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class TaskContext(val task: BukkitTask) {
    fun cancel() = task.cancel()
    val isCancelled: Boolean get() = task.isCancelled
    val taskId: Int get() = task.taskId
}

inline fun Plugin.runTask(crossinline block: TaskContext.() -> Unit): BukkitTask {
    lateinit var task: BukkitTask
    val context by lazy { TaskContext(task) }
    task = server.scheduler.runTask(this, Runnable { context.block() })
    return task
}

inline fun Plugin.runTaskTimer(delay: Long, period: Long, crossinline block: TaskContext.() -> Unit): BukkitTask {
    lateinit var task: BukkitTask
    val context by lazy { TaskContext(task) }
    task = server.scheduler.runTaskTimer(this, Runnable { context.block() }, delay, period)
    return task
}

inline fun Plugin.runTaskLater(delay: Long, crossinline block: TaskContext.() -> Unit): BukkitTask {
    lateinit var task: BukkitTask
    val context by lazy { TaskContext(task) }
    task = server.scheduler.runTaskLater(this, Runnable { context.block() }, delay)
    return task
}

inline fun Plugin.runTaskAsynchronously(crossinline block: TaskContext.() -> Unit): BukkitTask {
    lateinit var task: BukkitTask
    val context by lazy { TaskContext(task) }
    task = server.scheduler.runTaskAsynchronously(this, Runnable { context.block() })
    return task
}

inline fun Plugin.runTaskTimerAsynchronously(delay: Long, period: Long, crossinline block: TaskContext.() -> Unit): BukkitTask {
    lateinit var task: BukkitTask
    val context by lazy { TaskContext(task) }
    task = server.scheduler.runTaskTimerAsynchronously(this, Runnable { context.block() }, delay, period)
    return task
}

inline fun Plugin.runTaskLaterAsynchronously(delay: Long, crossinline block: TaskContext.() -> Unit): BukkitTask {
    lateinit var task: BukkitTask
    val context by lazy { TaskContext(task) }
    task = server.scheduler.runTaskLaterAsynchronously(this, Runnable { context.block() }, delay)
    return task
}