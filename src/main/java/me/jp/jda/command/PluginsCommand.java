package me.jp.jda.command;

import me.jp.jda.Main;
import me.jp.jda.data.Plugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginsCommand extends ListenerAdapter {

    private final Map<String, Integer> userPageMap = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.getName().equalsIgnoreCase("plugins")) {
            userPageMap.put(e.getUser().getId(), 0);
            sendPluginsPage(e, 0);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        String userId = e.getUser().getId();
        if (userPageMap.containsKey(userId)) {
            int currentPage = userPageMap.get(userId);
            if (e.getButton().getId().equals("previous_page")) {
                if (currentPage > 0) {
                    currentPage--;
                    userPageMap.put(userId, currentPage);
                    sendPluginsPage(e, currentPage);
                }
            } else if (e.getButton().getId().equals("next_page")) {
                List<Plugin> plugins = Main.pluginCache.getCachedElements();
                int maxPage = plugins.size() - 1;
                if (currentPage < maxPage) {
                    currentPage++;
                    userPageMap.put(userId, currentPage);
                    sendPluginsPage(e, currentPage);
                }
            } else if (e.getButton().getId().equals("buy_plugin_page")) {
                List<Plugin> plugins = Main.pluginCache.getCachedElements();
                Plugin plugin = plugins.get(currentPage);

                Category ticketsCategory = e.getGuild().getCategoriesByName("Tickets", true).stream().findFirst().orElse(null);

                if (ticketsCategory == null) {
                    e.reply("A categoria 'Tickets' não foi encontrada.").setEphemeral(true).queue();
                    return;
                }

                String channelName = e.getUser().getId() + "-" + plugin.getName();
                Guild guild = e.getGuild();
                Member member = e.getMember();
                ticketsCategory.createTextChannel(channelName).addPermissionOverride(guild.getPublicRole(), 0, Permission.VIEW_CHANNEL.getRawValue())
                        .addPermissionOverride(member, Permission.VIEW_CHANNEL.getRawValue(), 0)
                        .addPermissionOverride(guild.getRolesByName("TICKET", true).get(0), Permission.VIEW_CHANNEL.getRawValue(), 0)
                        .queue(channel -> {
                            channel.sendMessage("Olá " + member.getAsMention() + ", o seu ticket será atendido por nossa equipe em breve!")
                                    .addActionRow(
                                            Button.danger("fechar-ticket", "Fechar Ticket"),
                                            Button.secondary("trancar-ticket", "Trancar")
                                    ).queue();
                            e.reply("Canal " + channel.getAsMention() + " criado com sucesso!").setEphemeral(true).queue();
                        });
            }
        }
    }

    private void sendPluginsPage(SlashCommandInteractionEvent e, int page) {
        List<Plugin> plugins = Main.pluginCache.getCachedElements();
        int maxPage = plugins.size() - 1;

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LISTA DE PLUGINS")
                .setThumbnail("https://s.namemc.com/2d/skin/face.png?id=7b83a6f738f4ddbc&scale=12")
                .setColor(Color.BLUE);

        if (plugins.isEmpty()) {
            embedBuilder.setDescription("Nenhum plugin encontrado.");
        } else {
            Plugin plugin = plugins.get(page);
            String link;
            if (!plugin.getLink().contains("https://")) {
                link = "[clique aqui](https://" + plugin.getLink() + ")";
            } else {
                link = "[clique aqui](" + plugin.getLink() + ")";
            }
            embedBuilder
                    .addField("Produto", "**"+plugin.getName()+"**", true);
            embedBuilder
                    .addField("Valor", "**R$" + plugin.getPrice() + "**", true);
            embedBuilder
                    .addField("Link", link, true);
            embedBuilder
                    .addField("Descrição", plugin.getDescription(), false);
            embedBuilder
                    .addField("Versões suportadas", plugin.getVersion(), false);
        }
        Button previousButton = Button.primary("previous_page", Emoji.fromCustom("esquerda", Long.parseLong("1262939589602185236"), false));
        Button nextButton = Button.primary("next_page", Emoji.fromCustom("direita", Long.parseLong("1262939329857196052"), false));
        Button buyPlugin = Button.of(ButtonStyle.SUCCESS, "buy_plugin_page", "Comprar plugin", Emoji.fromCustom("carrinho", Long.parseLong("1262871114187673691"), false));

        if (page == 0) {
            previousButton = previousButton.asDisabled();
        }

        if (plugins.isEmpty()) {
            buyPlugin = buyPlugin.asDisabled();
            nextButton = nextButton.asDisabled();
        }

        if (page == maxPage) {
            nextButton = nextButton.asDisabled();
        }

        e.replyEmbeds(embedBuilder.build())
                .addActionRow(previousButton, nextButton, buyPlugin)
                .queue();
    }

    private void sendPluginsPage(ButtonInteractionEvent e, int page) {
        List<Plugin> plugins = Main.pluginCache.getCachedElements();
        int maxPage = plugins.size() - 1;

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Lista de Plugins")
                .setThumbnail("https://s.namemc.com/2d/skin/face.png?id=7b83a6f738f4ddbc&scale=12")
                .setColor(Color.BLUE);

        if (plugins.isEmpty()) {
            embedBuilder.setDescription("Nenhum plugin encontrado.");
        } else {
            Plugin plugin = plugins.get(page);
            String link;
            if (!plugin.getLink().contains("https://")) {
                link = "[clique aqui](https://" + plugin.getLink() + ")";
            } else {
                link = "[clique aqui](" + plugin.getLink() + ")";
            }
            embedBuilder
                    .addField("Produto", "**"+plugin.getName()+"**", true);
            embedBuilder
                    .addField("Valor", "**R$" + plugin.getPrice() + "**", true);
            embedBuilder
                    .addField("Link", link, true);
            embedBuilder
                    .addField("Descrição", plugin.getDescription(), false);
            embedBuilder
                    .addField("Versões suportadas", plugin.getVersion(), false);
        }
        Button previousButton = Button.primary("previous_page", Emoji.fromCustom("esquerda", Long.parseLong("1262939589602185236"), false));
        Button nextButton = Button.primary("next_page", Emoji.fromCustom("direita", Long.parseLong("1262939329857196052"), false));
        Button buyPlugin = Button.of(ButtonStyle.SUCCESS, "buy_plugin_page", "Comprar plugin", Emoji.fromCustom("carrinho", Long.parseLong("1262871114187673691"), false));

        if (page == 0) {
            previousButton = previousButton.asDisabled();
        }

        if (plugins.isEmpty()) {
            buyPlugin = buyPlugin.asDisabled();
            nextButton = nextButton.asDisabled();
        }

        if (page == maxPage) {
            nextButton = nextButton.asDisabled();
        }

        e.replyEmbeds(embedBuilder.build())
                .addActionRow(previousButton, nextButton, buyPlugin)
                .queue();
    }

}
