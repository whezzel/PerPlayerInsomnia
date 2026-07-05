package com.whezzel.perplayerinsomnia.mixin;

import com.whezzel.perplayerinsomnia.PhantomAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements PhantomAccess {
	@Unique
	private Boolean perplayerinsomnia$phantomsEnabled;

	@Override
	public boolean perplayerinsomnia$arePhantomsEnabled() {
		return perplayerinsomnia$phantomsEnabled != null && perplayerinsomnia$phantomsEnabled;
	}

	@Override
	public boolean perplayerinsomnia$hasOverride() {
		return perplayerinsomnia$phantomsEnabled != null;
	}

	@Override
	public void perplayerinsomnia$setPhantomsEnabled(boolean enabled) {
		perplayerinsomnia$phantomsEnabled = enabled;
	}

	@Override
	public void perplayerinsomnia$clearOverride() {
		perplayerinsomnia$phantomsEnabled = null;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	private void perplayerinsomnia$writeCustomData(ValueOutput output, CallbackInfo ci) {
		if (perplayerinsomnia$phantomsEnabled != null) {
			output.putInt("perplayerinsomnia:phantomsOverride", perplayerinsomnia$phantomsEnabled ? 1 : 0);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	private void perplayerinsomnia$readCustomData(ValueInput input, CallbackInfo ci) {
		input.getInt("perplayerinsomnia:phantomsOverride").ifPresent(value ->
				perplayerinsomnia$phantomsEnabled = value != 0);
	}
}