package io.github.jamalam360.sort_it_out.forge;

import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(SortItOut.MOD_ID)
public class SortItOutForge {
    public SortItOutForge() {
        SortItOut.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> SortItOutClient::init);
    }
}
