package io.github.chakyl.cobblemonfarmers.items;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class WorkerTypeItem extends Item {
    private static List<ElementalType> GARDENING_STATION_TYPES = List.of(ElementalTypes.INSTANCE.getWATER(), ElementalTypes.INSTANCE.getGRASS(), ElementalTypes.INSTANCE.getDARK(), ElementalTypes.INSTANCE.getFAIRY(), ElementalTypes.INSTANCE.getNORMAL(), ElementalTypes.INSTANCE.getFLYING());
    ElementalType type;

    public WorkerTypeItem(Properties pProperties, ElementalType type) {
        super(pProperties);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
        String typeName = type.getName();
        if (type == ElementalTypes.INSTANCE.getELECTRIC()) {
            list.add(Component.translatable("block.cobblemon_farmers.energy_pylon").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
            list.add(Component.translatable("info.cobblemon_farmers.energy_pylon.type." + typeName).withStyle(ChatFormatting.GRAY));

            list.add(Component.empty());
        }
        if (type == ElementalTypes.INSTANCE.getPSYCHIC()) {
            list.add(Component.translatable("block.cobblemon_farmers.crystal_ball").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
            list.add(Component.translatable("info.cobblemon_farmers.crystal_ball.type." + typeName).withStyle(ChatFormatting.GRAY));
            list.add(Component.empty());
        }
        if (GARDENING_STATION_TYPES.contains(type)) {
            list.add(Component.translatable("block.cobblemon_farmers.gardening_station").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
            list.add(Component.translatable("info.cobblemon_farmers.gardening_station.type." + typeName).withStyle(ChatFormatting.GRAY));
            list.add(Component.empty());
        }
        list.add(Component.translatable("block.cobblemon_farmers.craft_station").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.translatable("info.cobblemon_farmers.craft_station.type." + typeName).withStyle(ChatFormatting.GRAY));
        list.add(Component.empty());
        list.add(Component.translatable("block.cobblemon_farmers.mystery_mine").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE));
        list.add(Component.translatable("info.cobblemon_farmers.mystery_mine.type." + typeName).withStyle(ChatFormatting.GRAY));


    }

}