package endytkn.randomEvents.minecraftEventsObservers

import endytkn.randomEvents.utils.Observer
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object MinecraftEventsObservers {
    val onLivingDeathEventObserver = Observer<LivingDeathEvent>()
    val onPlayerTickEventObserver = Observer<TickEvent.PlayerTickEvent>()
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
}