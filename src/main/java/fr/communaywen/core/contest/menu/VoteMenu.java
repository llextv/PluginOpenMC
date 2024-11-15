package fr.communaywen.core.contest.menu;

import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.contest.cache.ContestCache;
import fr.communaywen.core.contest.managers.ColorConvertor;
import fr.communaywen.core.contest.managers.ContestManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import dev.xernas.menulib.Menu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VoteMenu extends Menu {
    private final AywenCraftPlugin plugin;

    private final ContestManager contestManager;
    private final ContestCache contestCache;
    public VoteMenu(Player owner, AywenCraftPlugin plugins, ContestManager manager) {
        super(owner);
        this.plugin=plugins;
        this.contestManager = manager;
        this.contestCache= plugins.getManagers().getContestCache();
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-48%%img_contest_menu%");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}


    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();

        String camp1Name = contestCache.getCamp1Cache();
        String camp2Name = contestCache.getCamp2Cache();

        String camp1Color = contestCache.getColor1Cache();
        String camp2Color = contestCache.getColor2Cache();

        ChatColor color1 = ChatColor.valueOf(camp1Color);
        ChatColor color2 = ChatColor.valueOf(camp2Color);
        Material m1 = ColorConvertor.getMaterialFromColor(color1);
        Material m2 = ColorConvertor.getMaterialFromColor(color2);

        int camp1Slot = 11;
        int camp2Slot = 15;

        List<String> lore1 = new ArrayList<String>();
        List<String> lore2 = new ArrayList<String>();
        boolean ench1;
        boolean ench2;
        if(contestCache.getPlayerCampsCache(getOwner()) <= 0) {
            ench1 = false;
            ench2 = false;
            lore1.add("§7Votez pour la Team " + color1 + camp1Name);
            lore1.add("§7Faites la gagner en déposant le plus de points");
            lore1.add("§c§lATTENTION! Le choix est définitif!");

            lore2.add("§7Votez pour " + color2 + "La Team " + camp2Name);
            lore2.add("§7Faites la gagner en déposant le plus de points");
            lore2.add("§c§lATTENTION! Le choix est définitif!");

        } else if(contestCache.getPlayerCampsCache(getOwner()) == 1) {
            lore1.add("§7Vous avez votez pour la Team " + color1 + camp1Name);
            lore1.add("§7Faites la gagner en déposant le plus de points!");
            ench1 = true;

            lore2.add("§7Faites perdre la Team " + color2 + camp2Name);
            lore2.add("§7En Apportant le plus de points que vous pouvez!");
            ench2 = false;
        } else if(contestCache.getPlayerCampsCache(getOwner()) == 2) {
            lore1.add("§7Faites perdre la Team " + color1 + camp1Name);
            lore1.add("§7En Apportant le plus de points que vous pouvez!");
            ench1 = false;

            lore2.add("§7Vous avez votez pour la Team " + color2 + camp2Name);
            lore2.add("§7Faites la gagner en déposant le plus de points!");
            ench2 = true;
        } else {
            ench2 = false;
            ench1 = false;
        }

        List<String> loreinfo = new ArrayList<String>();

        loreinfo.add("§7Apprenez en plus sur les Contest !");
        loreinfo.add("§7Le déroulement..., Les résultats, ...");
        loreinfo.add("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!");

                inventory.put(camp1Slot, new ItemBuilder(this, m1, itemMeta -> {
                    itemMeta.setDisplayName("§r" + color1 + camp1Name);
                    itemMeta.setLore(lore1);
                    itemMeta.setEnchantmentGlintOverride(ench1);
                }).setOnClick(inventoryClickEvent -> {
                    if (contestCache.getPlayerCampsCache(getOwner()) <= 0) {
                        ConfirmMenu menu = new ConfirmMenu(getOwner(), plugin, "camp1", "color1", contestManager);
                        menu.open();
                    }
                }));

                inventory.put(camp2Slot, new ItemBuilder(this, m2, itemMeta -> {
                    itemMeta.setDisplayName("§r" + color2 + camp2Name);
                    itemMeta.setLore(lore2);
                    itemMeta.setEnchantmentGlintOverride(ench2);
                }).setOnClick(inventoryClickEvent -> {
                    if (contestCache.getPlayerCampsCache(getOwner()) <= 0) {
                        ConfirmMenu menu = new ConfirmMenu(getOwner(), plugin,"camp2", "color2", contestManager);
                        menu.open();
                    }
                }));
                inventory.put(35, new ItemBuilder(this, Material.EMERALD, itemMeta -> {
                    itemMeta.setDisplayName("§r§aPlus d'info !");
                    itemMeta.setLore(loreinfo);}).setNextMenu(new MoreInfoMenu(getOwner(), contestManager)));

        return inventory;
    }
}
