package endytkn.randomEvents.utils;

import net.minecraftforge.fml.loading.FMLLoader;

public class EnvUtils {
    public static boolean isDevMode() {
        return !FMLLoader.getLaunchHandler().isProduction();
    }
}
