package {{group}}.{{mod_id}}.fabric;

import {{group}}.{{mod_id}}.{{main_class}};
import net.fabricmc.api.ModInitializer;

public class {{main_class}}Fabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        {{main_class}}.init();
    }
}
