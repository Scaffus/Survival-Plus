package me.scaffus.survivalplus.menus.skills;

import me.scaffus.survivalplus.Helper;
import me.scaffus.survivalplus.SurvivalData;
import me.scaffus.survivalplus.SurvivalPlus;
import me.scaffus.survivalplus.menus.SkillsMenu;
import me.scaffus.survivalplus.objects.PlayerUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CombatSkillMenu implements Listener {
    private SurvivalPlus plugin;
    private SurvivalData survivalData;
    private Helper helper;
    private SkillsMenu skillsMenu;
    private String inventoryName = "§6§lCombat";
    private static PlayerUpgrade damageUpgrade;
    private static PlayerUpgrade fastAttackUpgrade;

    public CombatSkillMenu(SurvivalPlus plugin, SkillsMenu skillsMenu) {
        this.plugin = plugin;
        this.survivalData = plugin.survivalData;
        this.helper = plugin.helper;
        this.skillsMenu = skillsMenu;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        damageUpgrade = survivalData.getUpgrade("damage");
        fastAttackUpgrade = survivalData.getUpgrade("fast_attack");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTitle().equals(inventoryName))) return;
        event.setCancelled(true);

        Player p = (Player) event.getWhoClicked();
        UUID uuid = p.getUniqueId();
        int slot = event.getSlot();
        if (!survivalData.canPlayerClick(p.getUniqueId())) return;

        if (slot == 20) skillsMenu.buyUpgrade(p, damageUpgrade);
        if (slot == 24) skillsMenu.buyUpgrade(p, fastAttackUpgrade);

        survivalData.setPlayerLastClicked(p.getUniqueId());
        if (slot == event.getInventory().getSize() - 9) p.openInventory(skillsMenu.createSkillMenu(p));
        if (slot == event.getInventory().getSize() - 1) p.closeInventory();
    }

    public Inventory createMenu(Player p) {
        UUID uuid = p.getUniqueId();
        Inventory inventory = helper.createInventoryWithBackground(p, inventoryName, 54, true);

        inventory.setItem(20, skillsMenu.getUpgradeMenuItem(damageUpgrade, uuid));
        inventory.setItem(24, skillsMenu.getUpgradeMenuItem(fastAttackUpgrade, uuid));

        inventory.setItem(49, helper.getHead(p, "§eJetons: §6" + survivalData.getPlayerTokens(p.getUniqueId())));

        return inventory;
    }
}
