package me.jp.jda.data.system;

import me.jp.jda.utils.Cache;

public class SystemCache extends Cache<System> {
    public System getLogChannel(String name) {
        return getCached($ -> $.getLogChannel().equalsIgnoreCase(name));
    }
    public System getTicketCategory(String name) {
        return getCached($ -> $.getTicketCategory().equalsIgnoreCase(name));
    }
}
