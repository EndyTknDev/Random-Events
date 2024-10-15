package endytkn.randomEvents.minecraftEventsObservers

import endytkn.randomEvents.utils.Observer
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.jetbrains.annotations.Debug

@Mod.EventBusSubscriber
object MinecraftEventsObservers {
    val onLivingDeathEventObserver = Observer<LivingDeathEvent>()
    val onPlayerTickEventObserver = Observer<TickEvent.PlayerTickEvent>()
    val onTickEventObserver = Observer<TickEvent>()
    val onServerTickEventObserver = Observer<TickEvent.ServerTickEvent>()
    val onLivingDropsEventObserver = Observer<LivingDropsEvent>()
    val onPlayerInteractEntityEventObserver = Observer<PlayerInteractEvent.EntityInteract>()

    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        try {
            onLivingDeathEventObserver.invoke(event)
        } catch (e: Error) {
            println(e)
            println("error on invoke")
        }
    }

    @SubscribeEvent
    fun onPlayerTickEvent(event: TickEvent.PlayerTickEvent) {
        try {
            onPlayerTickEventObserver.invoke(event)
        } catch (e: Error) {
            println(e)
            println("error on invoke")
        }
    }

    @SubscribeEvent
    fun onLivingDropsEvent(event: LivingDropsEvent) {
        try {
            onLivingDropsEventObserver.invoke(event)
        } catch (e: Error) {
            println(e)
            println("error on invoke")
        }
    }

    @SubscribeEvent
    fun onPlayerInteractEntity(event: PlayerInteractEvent.EntityInteract) {
        try {
            onPlayerInteractEntityEventObserver.invoke(event)
        } catch (e: Error) {
            println(e)
            println("error on invoke")
        }
    }

    @SubscribeEvent
    fun onTickEvent(event: TickEvent) {
        try {
            onTickEventObserver.invoke(event)
        } catch (e: Error) {
            println(e)
            println("error on invoke")
        }
    }

    @SubscribeEvent
    fun onServerTickEvent(event: TickEvent.ServerTickEvent) {
        try {
            onServerTickEventObserver.invoke(event)
        } catch (e: Error) {
            println(e)
            println("error on invoke")
        }
    }
}