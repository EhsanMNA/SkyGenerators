package me.ehsanmna.skyGenerators.commands;

import me.ehsanmna.skyGenerators.SkyGenerators;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SkyGeneratorTabCompleter implements TabCompleter {

    private final SkyGenerators plugin;

    public SkyGeneratorTabCompleter() {
        this.plugin = SkyGenerators.getInstance();
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: subcommands
            String input = args[0].toLowerCase();
            List<String> subcommands = Arrays.asList("list", "reload", "give", "menu", "debug", "help");
            for (String sub : subcommands) {
                if (sub.startsWith(input)) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            String partial = args[1].toLowerCase();

            switch (sub) {
                case "list":
                    if ("gui".startsWith(partial)) {
                        completions.add("gui");
                    }
                    break;

                case "give":
                case "menu":
                    // Suggest online player names
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(partial)) {
                            completions.add(player.getName());
                        }
                    }
                    break;

                default:
                    // reload, debug, help take no second argument
                    break;
            }
            return completions;
        }

        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("give")) {
                String partial = args[2].toLowerCase();

                // Generator IDs
                if (plugin.getGeneratorManager() != null
                        && plugin.getGeneratorManager().getService() != null
                        && plugin.getGeneratorManager().getService().getGenerators() != null) {
                    plugin.getGeneratorManager().getService().getGenerators().keySet()
                            .stream()
                            .filter(id -> id.toLowerCase().startsWith(partial))
                            .forEach(completions::add);
                }

                // Upgrade IDs
                if (plugin.getGeneratorUpgradeManager() != null
                        && plugin.getGeneratorUpgradeManager().getService() != null
                        && plugin.getGeneratorUpgradeManager().getService().getGeneratorUpgradeMap() != null) {
                    plugin.getGeneratorUpgradeManager().getService().getGeneratorUpgradeMap().keySet()
                            .stream()
                            .filter(id -> id.toLowerCase().startsWith(partial))
                            .forEach(completions::add);
                }
            }
            // list subcommand accepts at most "gui", no third argument
            return completions;
        }

        return completions; // empty for lengths > 3
    }
}