package me.jp.jda.command;

import lombok.RequiredArgsConstructor;
import me.jp.jda.data.system.System;
import me.jp.jda.data.system.SystemCache;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

@RequiredArgsConstructor
public class SystemCommand extends ListenerAdapter {

    private final SystemCache systemCache;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("system")) {
            if (event.getSubcommandGroup().equalsIgnoreCase("canal")) {
                if (event.getSubcommandName().equalsIgnoreCase("logs")) {
                    GuildChannelUnion log = Objects.requireNonNull(event.getOption("canal")).getAsChannel();
                    System logChannel = systemCache.getLogChannel(log.getId());
                    if (logChannel == null) {
                        systemCache.addCachedElements(new System(
                                "",
                                log.getId()
                        ));
                        return;
                    }
                    if (logChannel.getLogChannel().isEmpty()) {
                        logChannel.setLogChannel(log.getId());
                    } else {
                        logChannel.setLogChannel(log.getId());
                    }
                    event.reply("O canal de logs foi definido para " + log.getAsMention()).setEphemeral(true).queue();

                }
                if (event.getSubcommandName().equalsIgnoreCase("ticket")) {
                    String category = Objects.requireNonNull(event.getOption("category")).getAsString();
                    System logChannel = systemCache.getTicketCategory(category);
                    if (logChannel == null) {
                        systemCache.addCachedElements(new System(
                                category,
                                ""
                        ));
                        event.reply("A categoria ticket foi definido para " + event.getGuild().getCategoriesByName(category, true)).setEphemeral(true).queue();
                        return;
                    }
                    if (logChannel.getTicketCategory().isEmpty()) {
                        logChannel.setTicketCategory(category);
                    } else {
                        logChannel.setTicketCategory(category);
                    }
                    event.reply("A categoria ticket foi definido para " + event.getGuild().getCategoriesByName(category, true)).setEphemeral(true).queue();
                }
            }
        }
    }
}
