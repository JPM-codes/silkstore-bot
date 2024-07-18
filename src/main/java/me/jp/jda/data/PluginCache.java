package me.jp.jda.data;

import me.jp.jda.utils.Cache;

public class PluginCache extends Cache<Plugin> {
    public Plugin getByName(String name) {
        return getCached($ -> $.getName().equalsIgnoreCase(name));
    }
}
