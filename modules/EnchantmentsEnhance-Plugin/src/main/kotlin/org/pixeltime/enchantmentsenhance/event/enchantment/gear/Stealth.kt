package org.pixeltime.enchantmentsenhance.event.enchantment.gear

import com.sk89q.worldguard.bukkit.WGBukkit
import com.sk89q.worldguard.protection.flags.DefaultFlag
import com.sk89q.worldguard.protection.flags.StateFlag
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.pixeltime.enchantmentsenhance.listener.EnchantmentListener
import org.pixeltime.enchantmentsenhance.manager.SettingsManager

class Stealth : EnchantmentListener() {
    override fun desc(): Array<String> {
        return arrayOf(
            "When you sneak, nearby enemies aiming near you have a chance to get blindness",
            "当你潜行时，瞄准你的敌人有几率失明"
        )
    }

    override fun lang(): Array<String> {
        return arrayOf("潜伏")
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onSneak(playerToggleSneakEvent: PlayerToggleSneakEvent) {
        val player = playerToggleSneakEvent.player

        if (SettingsManager.enchant.getBoolean("allow-worldguard") && WGBukkit.getRegionManager(player.world)
                .getApplicableRegions(player.location).queryState(null, DefaultFlag.PVP) == StateFlag.State.DENY
        ) {
            return
        }

        try {
            val level = getLevel(player)
            if (level > 0 && (roll(level))) {
                val int1 = SettingsManager.enchant.getInt("stealth.$level.radius")
                for (entity in player.getNearbyEntities(int1.toDouble(), int1.toDouble(), int1.toDouble())) {
                    if (entity is Player) {
                        entity.addPotionEffect(
                            PotionEffect(
                                PotionEffectType.BLINDNESS,
                                SettingsManager.enchant.getInt("stealth.$level.duration") * 20,
                                0
                            )
                        )
                    }
                }
            }
        } catch (ex: Exception) {
        }
    }
}
