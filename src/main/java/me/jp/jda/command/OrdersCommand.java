package me.jp.jda.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class OrdersCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.getName().equalsIgnoreCase("encomendas")) {
            if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                e.reply("Você não tem permissão para isso!").setEphemeral(true).queue();
                return;
            }

            TextInput titleMenu = TextInput.create("encomendas-title", "Title", TextInputStyle.SHORT)
                    .setPlaceholder("Digite o titulo do menu de encomendas")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .setRequired(true)
                    .build();
            TextInput descriptionMenu = TextInput.create("encomendas-description", "Descrição", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Digite o titulo do menu de encomendas")
                    .setMaxLength(4000)
                    .setRequired(true)
                    .build();
            Modal modal = Modal.create("menu-encomendas-modal", "Menu de encomendas")
                    .addActionRows(
                            ActionRow.of(titleMenu),
                            ActionRow.of(descriptionMenu)
                    )
                    .build();
            e.replyModal(modal).queue();
            return;
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equals("menu-encomendas-modal")) {
            String menuTitle = e.getValue("encomendas-title").getAsString();
            String menuDescription = e.getValue("encomendas-description").getAsString();

            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(menuTitle);
            embedBuilder.setColor(Color.BLUE);
            embedBuilder.setDescription(menuDescription);

            SelectMenu selectMenu = StringSelectMenu
                    .create("encomendas-select-menu")
                    .setPlaceholder("Selecione...")
                    .setMinValues(1)
                    .setMaxValues(1)
                    .addOption("Encomende um Plugin!", "plugins", "Está precisando de um plugin? Clique aqui!", Emoji.fromUnicode("\uD83D\uDCE6"))
                    .addOption("Encomende uma Configuração!", "configuracao", "Está precisando configurar seu servidor? Clique aqui!", Emoji.fromUnicode("\uD83D\uDD27"))
                    .addOption("Encomende um Servidor!", "servidor", "Procurando servidor de minecraft? Clique aqui!", Emoji.fromUnicode("\uD83C\uDF10"))
                    .build();
            Button publicMenu = Button.success("encomendas-criar-button", "Criar menu");
            Button deleteMenu = Button.danger("encomendas-cancelar-button", "Deletar menu");

            e.replyEmbeds(embedBuilder.build())
                    .addActionRow(
                            selectMenu
                    )
                    .addActionRow(
                            deleteMenu, publicMenu
                    )
                    .setEphemeral(true).queue(response -> {
                        e.getHook().retrieveOriginal().queue(originalMessage -> {
                            e.getJDA().addEventListener(new ListenerAdapter() {
                                @Override
                                public void onButtonInteraction(ButtonInteractionEvent buttonEvent) {
                                    if (buttonEvent.getButton().getId().equalsIgnoreCase("encomendas-cancelar-button")) {
                                        originalMessage.delete().queue();
                                        buttonEvent.reply("Você cancelou a criação do menu de encomendas!").setEphemeral(true).queue();
                                    }
                                    if (buttonEvent.getButton().getId().equalsIgnoreCase("encomendas-criar-button")) {
                                        originalMessage.delete().queue();
                                        buttonEvent.getChannel().asTextChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(selectMenu).queue();
                                    }
                                }
                            });
                        });
                    });
        }
    }


    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent e) {
        if (Objects.requireNonNull(e.getSelectMenu().getId()).equalsIgnoreCase("encomendas-select-menu")) {
            String selected = e.getValues().get(0);
            if (selected.equalsIgnoreCase("plugins")) {
                // Procurar a categoria "Tickets"
                Category ticketsCategory = e.getGuild().getCategoriesByName("Tickets", true).stream().findFirst().orElse(null);

                if (ticketsCategory == null) {
                    e.reply("A categoria 'Tickets' não foi encontrada.").setEphemeral(true).queue();
                    return;
                }

                String channelName = e.getUser().getId() + "-plugin";
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
            if (selected.equalsIgnoreCase("configuracao")) {
                // Procurar a categoria "Tickets"
                Category ticketsCategory = e.getGuild().getCategoriesByName("Tickets", true).stream().findFirst().orElse(null);

                if (ticketsCategory == null) {
                    e.reply("A categoria 'Tickets' não foi encontrada.").setEphemeral(true).queue();
                    return;
                }

                String channelName = e.getUser().getId() + "-configuracao";
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
            if (selected.equalsIgnoreCase("servidor")) {
                // Procurar a categoria "Tickets"
                Category ticketsCategory = e.getGuild().getCategoriesByName("Tickets", true).stream().findFirst().orElse(null);

                if (ticketsCategory == null) {
                    e.reply("A categoria 'Tickets' não foi encontrada.").setEphemeral(true).queue();
                    return;
                }

                String channelName = e.getUser().getId() + "-servidor";
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

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equalsIgnoreCase("fechar-ticket")) {
            String user = event.getUser().getName();
            event.getChannel().getHistory().retrievePast(100).queue(messages -> {
                String fileName = "ticket-" + user + ".log";
                try {
                    File logFile = new File(fileName);
                    logFile.createNewFile();
                    FileWriter fw = new FileWriter(logFile);
                    BufferedWriter bw = new BufferedWriter(fw);
                    for (net.dv8tion.jda.api.entities.Message message : messages) {
                        String logMessage = "[" + message.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "] "
                                + message.getAuthor().getAsTag() + ": " + message.getContentDisplay() + "\n";
                        bw.write(logMessage);
                    }
                    bw.close();
                    fw.close();
                    TextChannel logsChannel = event.getGuild().getTextChannelById("1262246767282360321");
                    if (logsChannel != null) {
                        logsChannel.sendFiles(FileUpload.fromData(logFile)).queue(
                                success -> {
                                    // Remover o canal do ticket
                                    event.getChannel().delete().queue(
                                            v -> event.reply("Ticket fechado com sucesso! Log enviado para " + logsChannel.getAsMention()).queue(),
                                            f -> event.reply("Erro ao fechar o ticket.").queue()
                                    );
                                },
                                failure -> {
                                    event.reply("Erro ao enviar o log do ticket.").queue();
                                }
                        );
                    } else {
                        event.reply("Canal de logs não encontrado.").queue();
                    }

                } catch (IOException ex) {
                    event.reply("Erro ao salvar o log do ticket.").queue();
                    ex.printStackTrace();
                }
            });
        }
    }
}
