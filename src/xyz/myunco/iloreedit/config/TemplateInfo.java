package xyz.myunco.iloreedit.config;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.myunco.iloreedit.ILoreEdit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TemplateInfo {
    private final YamlConfiguration templates;
    private final File file;

    public TemplateInfo(ILoreEdit plugin) {
        file = new File(plugin.getDataFolder(), "templates.yml");
        if (!file.exists()) {
            plugin.saveResource("templates.yml", false);
        }
        templates = Config.loadConfiguration(file);
    }

    public boolean exists(String name) {
        return templates.contains(name);
    }

    public List<String> getTemplateList() {
        return new ArrayList<>(templates.getKeys(false));
    }

    public void delete(String name) {
        templates.set(name, null);
    }

    public String getDisplayName(String name) {
        return templates.getString(name + ".name");
    }

    public List<String> getLore(String name) {
        return templates.getStringList(name + ".lore");
    }

    public int getCustomModelData(String name) {
        return templates.getInt(name + ".customModelData");
    }

    public boolean hasCustomModelData(String name) {
        return templates.contains(name + ".customModelData");
    }

    public void setDisplayName(String name, String displayName) {
        templates.set(name + ".name", displayName);
    }

    public void setLore(String name, List<String> lore) {
        templates.set(name + ".lore", lore);
    }

    public void setCustomModelData(String name, Integer data) {
        templates.set(name + ".customModelData", data);
    }

    public void save() {
        Config.saveConfiguration(templates, file);
    }

}
