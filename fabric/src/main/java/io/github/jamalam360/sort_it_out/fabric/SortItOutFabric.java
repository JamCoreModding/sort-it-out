package io.github.jamalam360.sort_it_out.fabric;

import io.github.jamalam360.sort_it_out.SortItOut;
import net.fabricmc.api.ModInitializer;

public class SortItOutFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        SortItOut.init();
    }
}
