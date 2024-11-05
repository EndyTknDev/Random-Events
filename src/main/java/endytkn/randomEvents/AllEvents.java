package endytkn.randomEvents;

import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import net.minecraftforge.eventbus.api.IEventBus;

public class AllEvents {
    public static void register(IEventBus eventBus) {
        new MinecraftEventsObservers().register(eventBus);
    }
}