package com.gildedgames.aether.block.utility;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.blockentity.SunAltarBlockEntity;
import com.gildedgames.aether.AetherConfig;
import com.gildedgames.aether.capability.AetherCapabilities;
import com.gildedgames.aether.capability.time.AetherTime;
import com.gildedgames.aether.api.SunAltarWhitelist;
import com.gildedgames.aether.network.AetherPacketHandler;
import com.gildedgames.aether.network.packet.client.OpenSunAltarPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;

import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SunAltarBlock extends BaseEntityBlock {
	public SunAltarBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new SunAltarBlockEntity(pPos, pState);
	}

	@Nonnull
	@Override
	public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			if (AetherConfig.COMMON.sun_altar_whitelist.get() && !player.hasPermissions(4) && !SunAltarWhitelist.INSTANCE.isWhiteListed(player.getGameProfile())) {
				player.displayClientMessage(Component.translatable(Aether.MODID + ".sun_altar.no_permission"), true);
				return InteractionResult.SUCCESS;
			}
			Optional<AetherTime> aetherTimeOptional = level.getCapability(AetherCapabilities.AETHER_TIME_CAPABILITY).resolve();
			if (aetherTimeOptional.isPresent()) {
				if (!aetherTimeOptional.get().getEternalDay() || AetherConfig.COMMON.disable_eternal_day.get()) {
					this.openScreen(level, pos, player);
				} else {
					player.displayClientMessage(Component.translatable(Aether.MODID + ".sun_altar.in_control"), true);
				}
			} else {
				player.displayClientMessage(Component.translatable(Aether.MODID + ".sun_altar.no_power"), true);
			}
			return InteractionResult.SUCCESS;
		}
	}

	protected void openScreen(Level level, @Nonnull BlockPos pos, @Nonnull Player player) {
		if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof SunAltarBlockEntity sunAltar) {
				AetherPacketHandler.sendToPlayer(new OpenSunAltarPacket(sunAltar.getName()), serverPlayer);
			}
		}
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		if (pStack.hasCustomHoverName()) {
			BlockEntity blockentity = pLevel.getBlockEntity(pPos);
			if (blockentity instanceof SunAltarBlockEntity sunAltar) {
				sunAltar.setCustomName(pStack.getHoverName());
				sunAltar.setChanged();
			}
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

}
