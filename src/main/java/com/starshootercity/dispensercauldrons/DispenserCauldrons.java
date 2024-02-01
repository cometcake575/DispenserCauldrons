package com.starshootercity.dispensercauldrons;

import io.papermc.paper.event.block.BlockPreDispenseEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DispenserCauldrons extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPreDispense(BlockPreDispenseEvent event) {
        if (event.getBlock().getBlockData() instanceof Directional directional) {
            Block block = event.getBlock().getRelative(directional.getFacing());
            if (Tag.CAULDRONS.isTagged(block.getType())) {
                Material material;
                Material itemReplacement;
                Sound sound;
                switch (event.getItemStack().getType()) {
                    case WATER_BUCKET -> {
                        material = Material.WATER_CAULDRON;
                        sound = Sound.ITEM_BUCKET_EMPTY;
                        itemReplacement = Material.BUCKET;
                    }
                    case LAVA_BUCKET -> {
                        material = Material.LAVA_CAULDRON;
                        sound = Sound.ITEM_BUCKET_EMPTY_LAVA;
                        itemReplacement = Material.BUCKET;
                    }
                    case POWDER_SNOW_BUCKET -> {
                        material = Material.POWDER_SNOW_CAULDRON;
                        sound = Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW;
                        itemReplacement = Material.BUCKET;
                    }
                    case BUCKET -> {
                        if (block.getType() == Material.CAULDRON) return;
                        material = Material.CAULDRON;
                        switch (block.getType()) {
                            case LAVA_CAULDRON -> {
                                sound = Sound.ITEM_BUCKET_FILL_LAVA;
                                itemReplacement = Material.LAVA_BUCKET;
                            }
                            case WATER_CAULDRON -> {
                                sound = Sound.ITEM_BUCKET_FILL;
                                itemReplacement = Material.WATER_BUCKET;
                            }
                            case POWDER_SNOW_CAULDRON -> {
                                sound = Sound.ITEM_BUCKET_FILL_POWDER_SNOW;
                                itemReplacement = Material.POWDER_SNOW_BUCKET;
                            }
                            default -> {
                                return;
                            }
                        }
                    }
                    default -> {
                        return;
                    }
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                    BlockState state = event.getBlock().getState(false);
                    if (state instanceof Dispenser dispenser) {
                        dispenser.getWorld().playSound(dispenser.getLocation(), sound, SoundCategory.BLOCKS, 1, 1);
                        dispenser.getInventory().setItem(event.getSlot(), new ItemStack(itemReplacement));
                        block.setType(material);
                        if (block.getBlockData() instanceof Levelled levelled) {
                            levelled.setLevel(levelled.getMaximumLevel());
                            block.setBlockData(levelled);
                        }
                    }
                });
            }
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (event.getBlock().getType() != Material.DISPENSER) return;
        if (event.getBlock().getBlockData() instanceof Directional directional) {
            Block block = event.getBlock().getRelative(directional.getFacing());
            if (Tag.CAULDRONS.isTagged(block.getType())) {
                if (List.of(Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.POWDER_SNOW_BUCKET, Material.BUCKET).contains(event.getItem().getType())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}