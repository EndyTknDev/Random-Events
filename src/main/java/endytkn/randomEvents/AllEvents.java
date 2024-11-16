package endytkn.randomEvents;

import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.randomEvent.RandomEventRegister;
import net.minecraftforge.eventbus.api.IEventBus;

public class AllEvents {
    public static void registerObservers(IEventBus eventBus) {
        new MinecraftEventsObservers().register(eventBus);
    }
    public static void registerEvents(IEventBus eventBus) {
        RandomEventRegister.registerEvents();
    }
}
