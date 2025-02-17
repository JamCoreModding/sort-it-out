package io.github.jamalam360.sort_it_out.neoforge;

import io.github.jamalam360.sort_it_out.SortItOut;
import io.github.jamalam360.sort_it_out.client.SortItOutClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = SortItOut.MOD_ID, dist = Dist.CLIENT)
public class SortItOutClientNeoForge {
    public SortItOutClientNeoForge() {
        SortItOutClient.init();
    }
}
