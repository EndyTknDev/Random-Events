package endytkn.randomEvents;

import endytkn.randomEvents.randomEvent.RandomEventCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandRegister {
    static {
        MinecraftForge.EVENT_BUS.register(CommandRegister.class);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        RandomEventCommands.register(event.getDispatcher());
    }
}
