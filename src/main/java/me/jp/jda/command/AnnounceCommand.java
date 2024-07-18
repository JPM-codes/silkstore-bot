package me.jp.jda.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.util.Objects;

public class AnnounceCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("anunciar")) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("Você não tem permissão para isso!").setEphemeral(true).queue();
                return;
            }
            GuildChannelUnion canal = Objects.requireNonNull(event.getOption("canal")).getAsChannel();

            TextInput title = TextInput.create("announce-title", "Titulo", TextInputStyle.SHORT)
                    .setPlaceholder("Digite o titulo do anuncio")
                    .setRequired(true)
                    .build();
            TextInput description = TextInput.create("announce-description", "Descrição", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Digite a descrição do anuncio")
                    .setMaxLength(4000)
                    .setRequired(true)
                    .build();
            TextInput image = TextInput.create("announce-image", "Imagem", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Digite o url da imagem para o anuncio")
                    .setMaxLength(4000)
                    .setRequired(false)
                    .build();
            TextInput channel = TextInput.create("announce-channel", "Canal", TextInputStyle.PARAGRAPH)
                    .setMaxLength(4000)
                    .setValue(canal.getId())
                    .setRequired(false)
                    .build();

            Modal modal = Modal.create("announce-create-modal", "Criar anuncio")
                    .addActionRows(
                            ActionRow.of(title),
                            ActionRow.of(description),
                            ActionRow.of(image),
                            ActionRow.of(channel)
                    )
                    .build();
            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("announce-create-modal")) {
            String title = event.getValue("announce-title").getAsString();
            String description = event.getValue("announce-description").getAsString();
            String image = event.getValue("announce-image").getAsString();
            String canal = event.getValue("announce-channel").getAsString();

            TextChannel channelText = event.getGuild().getTextChannelById(canal);
            NewsChannel channelNews = event.getGuild().getNewsChannelById(canal);

            if (channelNews == null && channelText == null) {
                event.reply("Canal inválido! Por favor, forneça um ID de canal válido.").setEphemeral(true).queue();
                return;
            }
            ;

            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(title);
            embedBuilder.setColor(Color.BLUE);
            embedBuilder.setDescription(description);
            embedBuilder.setFooter("Anuncio criado por " + event.getInteraction().getUser().getEffectiveName(), event.getInteraction().getUser().getAvatarUrl());
            if (!image.isEmpty()) embedBuilder.setImage("https://" + image);

            Button publicAnnounce = Button.success("announce-create-button", "Criar anuncio");
            Button deleteAnnounce = Button.danger("announce-cancel-button", "Deletar anuncio");


            event.replyEmbeds(embedBuilder.build())
                    .addActionRow(
                            deleteAnnounce, publicAnnounce
                    ).setEphemeral(true)
                    .queue(response -> {
                        event.getHook().retrieveOriginal().queue(originalMessage -> {
                            event.getJDA().addEventListener(new ListenerAdapter() {
                                @Override
                                public void onButtonInteraction(ButtonInteractionEvent buttonEvent) {
                                    if (buttonEvent.getButton().getId().equalsIgnoreCase("announce-cancel-button")) {
                                        originalMessage.delete().queue();
                                        buttonEvent.reply("Você cancelou a criação do anuncio!").setEphemeral(true).queue();
                                        event.getJDA().removeEventListener(this);
                                    }
                                    if (buttonEvent.getButton().getId().equalsIgnoreCase("announce-create-button")) {
                                        originalMessage.delete().queue();
                                        if (channelText != null) {
                                            channelText.sendMessageEmbeds(embedBuilder.build()).queue();
                                            buttonEvent.reply("O anuncio foi postado no canal " + channelText.getAsMention() + "!").setEphemeral(true).queue();
                                        } else if (channelNews != null) {
                                            channelNews.sendMessageEmbeds(embedBuilder.build()).queue();
                                            buttonEvent.reply("O anuncio foi postado no canal " + channelNews.getAsMention() + "!").setEphemeral(true).queue();
                                        }
                                        event.getJDA().removeEventListener(this);
                                    }
                                }
                            });
                        });
                    });
        }
    }
}