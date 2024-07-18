package me.jp.jda;

import me.jp.jda.command.ClearCommand;
import me.jp.jda.command.OrdersCommand;
import me.jp.jda.command.PluginCommand;
import me.jp.jda.command.PluginsCommand;
import me.jp.jda.data.PluginCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

public class Main {

    public static PluginCache pluginCache;

    public static void main(String[] args) {

        pluginCache = new PluginCache();

        JDA jda = JDABuilder.createDefault("YOUR TOKEN", EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(
                        new PluginCommand(),
                        new PluginsCommand(),
                        new ClearCommand(),
                        new OrdersCommand()
                )
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("plugin", "Manage plugins")
                        .addSubcommands(new SubcommandData("add", "Adicionar plugin")
                                .addOption(OptionType.STRING, "plugin", "Nome do plugin", true))
                        .addSubcommands(new SubcommandData("edit", "Editar plugin")
                                .addOption(OptionType.STRING, "plugin", "Nome do plugin", true))
                        .addSubcommands(new SubcommandData("remove", "Remover plugin")
                                .addOption(OptionType.STRING, "plugin", "Nome do plugin", true)),
                Commands.slash("plugins", "Veja a lista de plugins"),
                Commands.slash("encomendas", "Crie o menu de encomendas"),
                Commands.slash("limpar", "Limpa um certo número de mensagens em um canal")
                        .addOption(OptionType.INTEGER, "quantidade", "Quantidade de mensagens a serem apagadas", true)
                        .addOption(OptionType.CHANNEL, "canal", "Selecione um canal", false)
        ).queue();
    }
}