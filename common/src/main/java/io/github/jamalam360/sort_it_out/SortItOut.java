package io.github.jamalam360.sort_it_out;

import io.github.jamalam360.jamlib.JamLib;
import io.github.jamalam360.sort_it_out.command.SortItOutCommands;
import io.github.jamalam360.sort_it_out.network.PacketHandlers;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SortItOut {
	public static final String MOD_ID = "sort_it_out";
	public static final String MOD_NAME = "Sort It Out!";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static void init() {
		JamLib.checkForJarRenaming(SortItOut.class);
		SortItOutCommands.register();
		PacketHandlers.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void playSortSound(Player player) {
		float vol = 0.4f + (0.5f * player.level().random.nextFloat());
		float pitch = 0.75f + (0.5f * player.level().random.nextFloat());

		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, player.getX(), player.getY(), player.getZ(), vol, pitch, player.level().random.nextLong()));
		} else {
			player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), vol, pitch);
		}
	}
}
