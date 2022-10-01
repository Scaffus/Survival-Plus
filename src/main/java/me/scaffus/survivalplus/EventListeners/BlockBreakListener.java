package me.scaffus.survivalplus.EventListeners;

import me.scaffus.survivalplus.Helper;
import me.scaffus.survivalplus.SkillsConfig;
import me.scaffus.survivalplus.SurvivalPlus;
import me.scaffus.survivalplus.sql.DatabaseGetterSetter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.Set;

public class BlockBreakListener implements Listener {
    private SurvivalPlus plugin;
    private DatabaseGetterSetter data;
    private SkillsConfig skillsConfig;
    private Helper helper;
    private Set blocks;
    private Map points;

    public BlockBreakListener(SurvivalPlus plugin) {
        this.plugin = plugin;
        this.data = plugin.data;
        this.skillsConfig = plugin.skillsConfig;
        this.helper = plugin.helper;
        blocks = skillsConfig.get().getConfigurationSection("mining.blocks").getKeys(false);
        points = skillsConfig.get().getConfigurationSection("mining.blocks").getValues(false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (blocks.contains(event.getBlock().getType().toString())) {
            Double pointsGained = helper.round((Double) points.get(event.getBlock().getType().toString()), 2);
            data.incrementPlayerSkill(event.getPlayer().getUniqueId(), "mining", pointsGained);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    plugin.getConfig().getString("skills.gained")
                            .replace("%amount%", String.valueOf(pointsGained)).replace("%skill%", "minage")));
        }
    }
}