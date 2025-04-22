package cn.coostack.video.util

object TickHelper {
    internal val ticks = HashSet<TickRunnable>()


    fun doTick() {
        val iterator = ticks.iterator()
        while (iterator.hasNext()) {
            val tick = iterator.next()
            tick.doTick()
            if (tick.canceled) {
                iterator.remove()
            }
        }
    }


    fun runTaskTimer(delay: Int, runnable: Runnable): TickRunnable {
        val tick = TickRunnable(runnable)
        tick.maxTick = delay
        tick.loop()
        ticks.add(tick)
        return tick
    }

    fun runTask(delay: Int, runnable: Runnable): TickRunnable {
        val tick = TickRunnable(runnable)
        tick.maxTick = delay
        ticks.add(tick)
        return tick
    }

    fun runTaskTimerMaxTick(maxLoopTick: Int, runnable: Runnable): TickRunnable {
        val tick = TickRunnable(runnable)
        tick.maxTick = maxLoopTick
        tick.loopTimer()
        ticks.add(tick)
        return tick
    }

    class TickRunnable(val runnable: Runnable) {
        internal var maxTick = 0
        private var currentTick = 0
        var canceled = false
            private set

        // looped为true时 maxTick就是 循环周期
        private var looped = false

        /**
         * 每tick执行一次
         * 执行到maxTick结束
         */
        private var loopTimer = false
        fun loop(): TickRunnable {
            looped = true
            currentTick = maxTick
            return this
        }

        fun loopTimer(): TickRunnable {
            loopTimer = true
            return this
        }

        fun cancel() {
            canceled = true
        }

        fun doTick() {
            if (canceled) {
                return
            }

            if (loopTimer) {
                if (currentTick++ >= maxTick) {
                    canceled = true
                    return
                }
                runnable.run()
            }

            if (looped) {
                if (currentTick++ >= maxTick) {
                    runnable.run()
                    currentTick = 0
                }
                return
            }
            if (currentTick++ >= maxTick) {
                runnable.run()
                canceled = true
            }
            return
        }
    }

}