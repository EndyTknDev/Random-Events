package endytkn.randomEvents;

import endytkn.randomEvents.chunkManager.ChunkManager;
import endytkn.randomEvents.network.PacketHandler;
import endytkn.randomEvents.randomEvent.RandomEventRegister;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Objects;

@Mod(RandomEventsMod.ID)
public class RandomEventsMod {
    public static final String ID = "randomevents";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public RandomEventsMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        registerEventListeners(eventBus);
        AllEvents.registerEvents(MinecraftForge.EVENT_BUS);
        AllEvents.registerObservers(MinecraftForge.EVENT_BUS);
    }

    /**
     * Registers the event listeners for the mod.
     *
     * @param eventBus The event bus to which the listeners will be registered.
     */
    private void registerEventListeners(IEventBus eventBus) {
        eventBus.addListener(this::onClientSetup);
        eventBus.addListener(this::onServerSetup);
        eventBus.addListener(this::onLoadRegister);
        eventBus.addListener(this::commonSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Initializing client...");
    }

    private void onServerSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Server starting...");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                LOGGER.info("Initializing network packets...");
                PacketHandler.init();

            } catch (Exception e) {
                LOGGER.error("Failed to initialize network packets", e);
            }
        });
    }

    private void onLoadRegister(FMLLoadCompleteEvent event) {}
}
