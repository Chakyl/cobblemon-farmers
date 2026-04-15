package io.github.chakyl.cobblemonfarmers.items;

import com.cobblemon.mod.common.api.types.ElementalType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class WorkerTypeItem extends Item {
    ElementalType type;
    public WorkerTypeItem(Properties pProperties, ElementalType type) {
        super(pProperties);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        String typeName = type.getName();
        list.add(Component.translatable("block.cobblemon_farmers.craft_station").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.translatable("info.cobblemon_farmers.craft_station.type." + typeName).withStyle(ChatFormatting.GRAY));
        list.add(Component.empty());
        list.add(Component.translatable("block.cobblemon_farmers.mystery_mine").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.translatable("info.cobblemon_farmers.mystery_mine.type." + typeName).withStyle(ChatFormatting.GRAY));;
        list.add(Component.empty());
        list.add(Component.translatable("block.cobblemon_farmers.gardening_station").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.translatable("info.cobblemon_farmers.gardening_station.type." + typeName).withStyle(ChatFormatting.GRAY));
    }

}