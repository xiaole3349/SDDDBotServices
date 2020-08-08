package com.mrwhoami.qqservices

import mu.KotlinLogging
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.*

class MuteMenu {
    private val logger = KotlinLogging.logger {}

    suspend fun onGrpMsg (event: GroupMessageEvent) {
        // Check if this is text message
        val msg = event.message
        if (!msg.all{ block -> block.isContentEmpty() || block.isPlain() }) {
            return
        }
        // Check if this is an order
        val msgContent = msg.content
        if (!msgContent.contains("我要休息")) {
            return
        }
        // Check for the unit
        val customer = event.sender
        val scale = when {
            msgContent.contains("小时") -> 60 * 60
            msgContent.contains("分钟") -> 60
            else -> {
                event.group.sendMessage(customer.at() + "请设置合适的时间。目前不支持除了小时与分钟以外的时间单位，不支持小数点，且不能混用，否则……嘿嘿……")
                return
            }
        }
        // Parse time and check.
        val time = msgContent.filter { it.isDigit() }
        if (time.isEmpty() || time.length > 5) {
            event.group.sendMessage(customer.at() + "你这个时间不对劲")
            return
        }
        val timeNum = time.toInt() * scale
        if (timeNum >= 2592000) {
            event.group.sendMessage(customer.at() + "你这个时间不对劲")
            return
        }
        // Now, complete the order
        customer.mute(timeNum)
        event.group.sendMessage("欢迎使用禁言套餐，您的餐齐了，请慢用")
    }
}