package endytkn.randomEvents.minecraftEventsObservers

import endytkn.randomEvents.utils.Observer
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object MinecraftEventsObservers {
    val onLivingDeathEventObserver = Observer<LivingDeathEvent>()
    val onPlayerTickEventObserver = Observer<TickEvent.PlayerTickEvent>()
    val onLivingDropsEventObserver = Observer<LivingDropsEvent>()
    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        try {
            onLivingDeathEventObserver.invoke(event)
        } catch (e: Error) {
            println("error on invoke")
        }
    }

    @SubscribeEvent
    fun onPlayerTickEvent(event: TickEvent.PlayerTickEvent) {
        try {
            onPlayerTickEventObserver.invoke(event)
        } catch (e: Error) {
            println("error on invoke")
        }
    }

    @SubscribeEvent
    fun onLivingDropsEvent(event: LivingDropsEvent) {
        try {
            onLivingDropsEventObserver.invoke(event)
        } catch (e: Error) {
            println("error on invoke")
        }
    }
}