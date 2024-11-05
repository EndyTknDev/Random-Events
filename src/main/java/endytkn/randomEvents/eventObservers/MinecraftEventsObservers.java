package endytkn.randomEvents.eventObservers;

import endytkn.randomEvents.utils.Observer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class MinecraftEventsObservers {
    public static final Observer<LivingDeathEvent> livingDeathObserver = new Observer<>();
    public static final Observer<TickEvent.PlayerTickEvent> playerTickObserver = new Observer<>();
    public static final Observer<TickEvent> tickObserver = new Observer<>();
    public static final Observer<TickEvent.ServerTickEvent> serverTickObserver = new Observer<>();
    public static final Observer<LivingDropsEvent> livingDropsObserver = new Observer<>();
    public static final Observer<PlayerInteractEvent.EntityInteract> playerInteractEntityObserver = new Observer<>();

    private void onLivingDeath(LivingDeathEvent event) {
        notifyObserver(this.livingDeathObserver, event);
    }

    private void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        notifyObserver(playerTickObserver, event);
    }

    private void onLivingDropsEvent(LivingDropsEvent event) {
        notifyObserver(livingDropsObserver, event);
    }

    private void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        notifyObserver(playerInteractEntityObserver, event);
    }

    private void onTickEvent(TickEvent event) {
        notifyObserver(tickObserver, event);
    }

    private void onServerTickEvent(TickEvent.ServerTickEvent event) {
        notifyObserver(serverTickObserver, event);
    }

    private <T> void notifyObserver(Observer<T> observer, T event) {
        try {
            observer.notify(event);
        } catch (Exception e) {
            e.printStackTrace(); // Log stack trace for better debugging
            System.err.println("Error while invoking observer for: " + event.getClass().getSimpleName());
        }
    }

    public void register(IEventBus eventBus) {
        eventBus.addListener(this::onServerTickEvent);
        eventBus.addListener(this::onTickEvent);
        eventBus.addListener(this::onPlayerTickEvent);
        eventBus.addListener(this::onLivingDropsEvent);
        eventBus.addListener(this::onPlayerInteractEntity);
        eventBus.addListener(this::onLivingDeath);
    }
}