package endytkn.randomEvents.randomEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class RandomEventScene {
    private final boolean underground;
    private final RandomEventsCategory weatherCategory;
    private final RandomEventsCategory timeCategory;

    public RandomEventScene(
        ServerLevel level,
        BlockPos blockpos
    ) {
        this.underground = setPlayerUnderground(level, blockpos);
        this.weatherCategory = setWeatherCategory(level);
        this.timeCategory = setTimeCategory(level);
    }

    public static RandomEventsCategory setWeatherCategory(ServerLevel level) {
        if (level.isThundering())
            return RandomEventsCategory.WEATHER_THUNDER;
        if (level.isRaining())
            return RandomEventsCategory.WEATHER_RAIN;
        return RandomEventsCategory.WEATHER_CLEAR;
    }

    public static RandomEventsCategory setTimeCategory(ServerLevel level) {
        long dayTime = level.getDayTime();
        if (dayTime < 6000)
            return RandomEventsCategory.DAY_MORNING;
        if (dayTime < 12000)
            return RandomEventsCategory.DAY_AFTERNOON;
        if (dayTime < 18000)
            return RandomEventsCategory.DAY_EVENING;
        return RandomEventsCategory.DAY_NIGHT;
    }

    public static boolean setPlayerUnderground(ServerLevel level, BlockPos blockPos) {
        return level.dimensionType().hasSkyLight() && !level.canSeeSky(blockPos);
    }

    public boolean isUnderground() {
        return this.underground;
    }

    public RandomEventsCategory getWeatherCategory() {
        return weatherCategory;
    }

    public RandomEventsCategory getTimeCategory() {
        return timeCategory;
    }
}
