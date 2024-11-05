package endytkn.randomEvents.baseEvents.groupFight;

import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GroupFight {
    private final GroupFightBaseEvent groupFightEvent;
    public final String groupName;
    public final Map<UUID, Mob> entities;
    public final String groupHates;
    public int entitiesLeft;
    public final Map<UUID, Mob> entitiesKilled;
    public int playersKill;

    public GroupFight(GroupFightBaseEvent groupFightEvent, String groupName, Map<UUID, Mob> entities, String groupHates) {
        this.groupFightEvent = groupFightEvent;
        this.groupName = groupName;
        this.entities = entities;
        this.groupHates = groupHates;
        this.entitiesLeft = entities.size();
        this.entitiesKilled = new HashMap<>();
        this.playersKill = 0;
    }

    public void killEntity(UUID uuid, Boolean killedByPlayer) {
        Mob killedMob = entities.remove(uuid);
        if (killedMob == null) return;

        entitiesKilled.put(uuid, killedMob);
        entitiesLeft = entities.size();
        if (Boolean.TRUE.equals(killedByPlayer)) {
            playersKill++;
        }
    }

    public int getEntitiesLeft() {
        return entitiesLeft;
    }

    public Map<UUID, Mob> getEntitiesKilled() {
        return entitiesKilled;
    }

    public int getPlayersKill() {
        return playersKill;
    }
}
