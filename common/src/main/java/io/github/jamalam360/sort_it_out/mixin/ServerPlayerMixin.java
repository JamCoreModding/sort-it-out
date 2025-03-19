package io.github.jamalam360.sort_it_out.mixin;

import io.github.jamalam360.sort_it_out.mixinsupport.ServerPlayerLanguageAccessor;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerLanguageAccessor {
	@Unique
	private String sort_it_out$language = "en_us";

	@Inject(
			method = "updateOptions",
			at = @At("TAIL")
	)
	private void sort_it_out$captureLanguage(ServerboundClientInformationPacket packet, CallbackInfo ci) {
		this.sort_it_out$language = packet.language();
	}

	@Override
	public String sort_it_out$getLanguage() {
		return this.sort_it_out$language;
	}
}
