package endytkn.randomEvents.baseEvents.invasion;

import net.minecraft.world.entity.Mob;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InvasionWaveGroup {
    public final InvasionBaseEvent invasion;
    public final String title;
    public final Map<UUID, Mob> entities;
    public int entitiesLeft;
    public final Map<UUID, Mob> entitiesKilled;

    public InvasionWaveGroup(InvasionBaseEvent invasion, String title, Map<UUID, Mob> entities) {
        this.invasion = invasion;
        this.title = title;
        this.entities = new ConcurrentHashMap<>(entities); // Usando ConcurrentHashMap para thread safety
        this.entitiesLeft = entities.size();
        this.entitiesKilled = new ConcurrentHashMap<>();
    }

    public void addMob(Mob mob) {
        this.entities.put(mob.getUUID(), mob);
        entitiesLeft = entities.size();
    }

    public void killEntity(UUID uuid) {
        Mob killedMob = entities.remove(uuid);
        if (killedMob == null) return;
        entitiesKilled.put(uuid, killedMob);
        entitiesLeft = entities.size();
    }

    public int getEntitiesLeft() {
        return entitiesLeft;
    }

    public Map<UUID, Mob> getEntitiesKilled() {
        return entitiesKilled;
    }
}
