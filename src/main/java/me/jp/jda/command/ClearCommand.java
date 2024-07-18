package me.jp.jda.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ClearCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("limpar")) {
            // Verifica se o usuário tem permissão de administrador
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
                return;
            }

            TextChannel channel;
            if (event.getOption("canal") != null) {
                channel = event.getOption("canal").getAsChannel().asTextChannel();
            } else {
                channel = event.getChannel().asTextChannel();
            }

            int amount = event.getOption("quantidade").getAsInt();

            // Limita a quantidade de mensagens a serem deletadas a 100
            if (amount < 1 || amount > 100) {
                event.reply("A quantidade de mensagens deve ser entre 1 e 100.").setEphemeral(true).queue();
                return;
            }

            // Obtém as mensagens e as deleta
            channel.getHistory().retrievePast(amount).queue(messages -> {
                channel.purgeMessages(messages);
                event.reply("Deletadas " + amount + " mensagens de " + channel.getAsMention()).setEphemeral(true).queue();
            });
        }
    }
}
