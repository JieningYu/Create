package com.simibubi.create.content.logistics.trains.management.schedule;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraption;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import org.jetbrains.annotations.Nullable;

public class ScheduleItemRetrieval {

	public static InteractionResult removeScheduleFromConductor(Player player, InteractionHand hand, Entity entity) {
		if (player == null || entity == null)
			return null;
		if (player.isSpectator())
			return null;

		Entity rootVehicle = entity.getRootVehicle();
		if (!(rootVehicle instanceof CarriageContraptionEntity))
			return null;

		ItemStack itemStack = player.getItemInHand(hand);
		if (AllItems.SCHEDULE.isIn(itemStack) && entity instanceof Wolf wolf) {
			itemStack.getItem()
				.interactLivingEntity(itemStack, player, wolf, hand);
			return null;
		}

		if (player.level.isClientSide)
			return null;
		if (hand == InteractionHand.OFF_HAND)
			return null;

		CarriageContraptionEntity cce = (CarriageContraptionEntity) rootVehicle;
		Contraption contraption = cce.getContraption();
		if (!(contraption instanceof CarriageContraption cc))
			return null;

		Train train = cce.getCarriage().train;
		if (train == null)
			return null;
		if (train.runtime.getSchedule() == null)
			return null;

		Integer seatIndex = contraption.getSeatMapping()
			.get(entity.getUUID());
		if (seatIndex == null)
			return null;
		BlockPos seatPos = contraption.getSeats()
			.get(seatIndex);
		Couple<Boolean> directions = cc.conductorSeats.get(seatPos);
		if (directions == null)
			return null;

		if (train.runtime.paused && !train.runtime.completed) {
			train.runtime.paused = false;
			AllSoundEvents.CONFIRM.playOnServer(player.level, player.blockPosition(), 1, 1);
			player.displayClientMessage(Lang.translateDirect("schedule.continued"), true);
			return InteractionResult.SUCCESS;
		}

		ItemStack itemInHand = player.getItemInHand(hand);
		if (!itemInHand.isEmpty()) {
			AllSoundEvents.DENY.playOnServer(player.level, player.blockPosition(), 1, 1);
			player.displayClientMessage(Lang.translateDirect("schedule.remove_with_empty_hand"), true);
			return InteractionResult.SUCCESS;
		}

		AllSoundEvents.playItemPickup(player);
		player.displayClientMessage(
			Lang.translateDirect(
				train.runtime.isAutoSchedule ? "schedule.auto_removed_from_train" : "schedule.removed_from_train"),
			true);

		player.getInventory()
			.placeItemBackInInventory(train.runtime.returnSchedule());
//		player.setItemInHand(event.getHand(), train.runtime.returnSchedule());
		return InteractionResult.SUCCESS;
	}

}
