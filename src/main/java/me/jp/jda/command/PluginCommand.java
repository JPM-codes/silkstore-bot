package me.jp.jda.command;

import me.jp.jda.Main;
import me.jp.jda.data.Plugin;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

public class PluginCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.getName().equalsIgnoreCase("plugin")) {
            if (!Objects.requireNonNull(e.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
                e.reply("Você não tem permissão para executar esse comando!").setEphemeral(true).queue();
                return;
            }
            switch (Objects.requireNonNull(e.getSubcommandName())) {
                case "add":
                    String pluginName = Objects.requireNonNull(e.getOption("plugin")).getAsString();

                    Plugin pluginNameExist = Main.pluginCache.getByName(pluginName);
                    if (pluginNameExist != null) {
                        e.reply("Esse plugin já está adicionado a lista!").setEphemeral(true).queue();
                        return;
                    }

                    TextInput plugin_name = TextInput.create("plugin-name", "Name", TextInputStyle.SHORT)
                            .setPlaceholder("Digite o nome do plugin")
                            .setMinLength(1)
                            .setValue(pluginName)
                            .setRequired(true)
                            .build();

                    TextInput price = TextInput.create("plugin-price","Preço", TextInputStyle.SHORT)
                            .setPlaceholder("Digite o preço do plugin")
                            .setMaxLength(6)
                            .setRequired(true)
                            .build();
                    TextInput description = TextInput.create("plugin-description","Descrição", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("Digite a descrição do plugin")
                            .setMaxLength(1000)
                            .setRequired(true)
                            .build();
                    TextInput link = TextInput.create("plugin-link","Link", TextInputStyle.SHORT)
                            .setPlaceholder("Digite o link do plugin")
                            .setMaxLength(1000)
                            .setRequired(true)
                            .build();
                    TextInput version = TextInput.create("plugin-version","Version", TextInputStyle.SHORT)
                            .setPlaceholder("Digite a versão do plugin")
                            .setMaxLength(1000)
                            .setRequired(true)
                            .build();
                    Modal modal = Modal.create("plugin-add-modal", "Adicionar Plugin")
                            .addActionRows(
                                    ActionRow.of(plugin_name),
                                    ActionRow.of(price),
                                    ActionRow.of(description),
                                    ActionRow.of(link),
                                    ActionRow.of(version)
                            )
                            .build();
                    e.replyModal(modal).queue();
                    return;
                case "edit":
                    String pluginName2 = Objects.requireNonNull(e.getOption("plugin")).getAsString();

                    Plugin pluginNameExist2 = Main.pluginCache.getByName(pluginName2);
                    if (pluginNameExist2 == null) {
                        e.reply("Esse plugin não está adicionado a lista!").setEphemeral(true).queue();
                        return;
                    }

                    TextInput plugin_name2 = TextInput.create("plugin-name", "Name", TextInputStyle.SHORT)
                            .setPlaceholder("Digite o nome do plugin")
                            .setMinLength(1)
                            .setValue(pluginName2)
                            .setRequired(true)
                            .build();

                    TextInput price2 = TextInput.create("plugin-price","Preço", TextInputStyle.SHORT)
                            .setPlaceholder("Digite o preço do plugin")
                            .setMaxLength(6)
                            .setValue(pluginNameExist2.getPrice())
                            .setRequired(true)
                            .build();
                    TextInput description2 = TextInput.create("plugin-description","Descrição", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("Digite a descrição do plugin")
                            .setMaxLength(1000)
                            .setValue(pluginNameExist2.getDescription())
                            .setRequired(true)
                            .build();
                    TextInput link2 = TextInput.create("plugin-link","Link", TextInputStyle.SHORT)
                            .setPlaceholder("Digite o link do plugin")
                            .setMaxLength(1000)
                            .setValue(pluginNameExist2.getLink())
                            .setRequired(true)
                            .build();
                    TextInput version2 = TextInput.create("plugin-version","Version", TextInputStyle.SHORT)
                            .setPlaceholder("Digite a versão do plugin")
                            .setMaxLength(1000)
                            .setValue(pluginNameExist2.getVersion())
                            .setRequired(true)
                            .build();
                    Modal modal2 = Modal.create("plugin-edit-modal", "Editar Plugin")
                            .addActionRows(
                                    ActionRow.of(plugin_name2),
                                    ActionRow.of(price2),
                                    ActionRow.of(description2),
                                    ActionRow.of(link2),
                                    ActionRow.of(version2)
                            )
                            .build();
                    e.replyModal(modal2).queue();
                    return;
                case "remove":
                    String pluginName1 = Objects.requireNonNull(e.getOption("plugin")).getAsString();

                    Plugin pluginNameExist1 = Main.pluginCache.getByName(pluginName1);
                    if (pluginNameExist1 == null) {
                        e.reply("Esse plugin não está adicionado a lista!").setEphemeral(true).queue();
                        return;
                    }
                    Main.pluginCache.removeCachedElement(pluginNameExist1);
                    e.reply("O plugin " + pluginName1 + " foi removido da lista!").setEphemeral(true).queue();
                    return;
                default:
                    return;
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equals("plugin-add-modal")) {
            String pluginName = e.getValue("plugin-name").getAsString();
            String pluginPrice = e.getValue("plugin-price").getAsString();
            String pluginDescription = e.getValue("plugin-description").getAsString();
            String pluginLink = e.getValue("plugin-link").getAsString();
            String pluginVersion = e.getValue("plugin-version").getAsString();

            Main.pluginCache.addCachedElement(new Plugin(
                    pluginName,
                    pluginPrice,
                    pluginDescription,
                    pluginLink,
                    pluginVersion
            ));

            e.reply("Plugin " + pluginName + " adicionado com sucesso! Preço: " + pluginPrice + ", Link: " + pluginLink).setEphemeral(true).queue();
        }
        if (e.getModalId().equals("plugin-edit-modal")) {
            String pluginName = e.getValue("plugin-name").getAsString();
            String pluginPrice = e.getValue("plugin-price").getAsString();
            String pluginDescription = e.getValue("plugin-description").getAsString();
            String pluginLink = e.getValue("plugin-link").getAsString();
            String pluginVersion = e.getValue("plugin-version").getAsString();

            Plugin plugin = Main.pluginCache.getByName(pluginName);
            if (plugin != null) {
                Main.pluginCache.removeCachedElements(plugin);
            }
            Main.pluginCache.addCachedElement(new Plugin(
                    pluginName,
                    pluginPrice,
                    pluginDescription,
                    pluginLink,
                    pluginVersion
            ));

            e.reply("Plugin " + pluginName + " editado com sucesso! Preço: " + pluginPrice + ", Link: " + pluginLink).setEphemeral(true).queue();
        }
    }

}
