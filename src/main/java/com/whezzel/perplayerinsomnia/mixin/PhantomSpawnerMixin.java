package com.whezzel.perplayerinsomnia.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.whezzel.perplayerinsomnia.storage.PhantomSettingsManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
	@ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 0)
	private int perplayerinsomnia$modifyInsomniaTicks(int insomniaTicks, @Local ServerPlayer player) {
		if (!PhantomSettingsManager.resolveEnabled(player)) {
			return 1;
		}

		return insomniaTicks;
	}
}