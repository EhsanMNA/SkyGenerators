package me.ehsanmna.skygenerators.commands;

import me.ehsanmna.skygenerators.SkyGenerators;
import me.ehsanmna.skygenerators.models.BaseGenerator;
import me.ehsanmna.skygenerators.models.upgrade.BaseGeneratorUpgrade;
import me.ehsanmna.skygenerators.utils.InventoryUtils;
import me.ehsanmna.skygenerators.utils.MessageUtils;
import me.ehsanmna.skygenerators.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SkyGeneratorCommand implements CommandExecutor {

    SkyGenerators skyGenerators = SkyGenerators.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int length = args.length;

        if (length == 0){
            sender.sendMessage(TextUtils.toComponent(
                    MessageUtils.getMessage("command-generator-info","<dark_red>SkyGenerators <white>| To get help type /sg help!")));
            return true;
        }

        String subcommand = args[0].toLowerCase();


        switch (subcommand){
            case "list":
                if (sender instanceof Player player && !player.hasPermission("skygenerator.command.list")){
                    sender.sendMessage(TextUtils.toComponent(
                            MessageUtils.getMessage("command-no-permission", "<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!")));
                    return true;
                }

                if (length == 1){
                    sender.sendMessage(TextUtils.toComponent("<dark_red><bold>SkyGenerators <white>|"));
                    for (BaseGeneratorUpgrade generatorUpgrade : skyGenerators.getGeneratorUpgradeManager().getService().getGeneratorUpgradeMap().values())
                        sender.sendMessage(TextUtils.toComponent("<red> | <white>"+generatorUpgrade.getName()+"<gray> "+generatorUpgrade.getId()));
                    for (BaseGenerator generator : skyGenerators.getGeneratorManager().getService().getGenerators().values())
                        sender.sendMessage(TextUtils.toComponent("<white> | "+generator.getName()+"<gray> "+generator.getId()));
                }else if (length == 2){
                    if (args[1].equalsIgnoreCase("gui"))
                        if (sender instanceof Player player) openListGUI(player);
                        else sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-not-found","<dark_red>SkyGenerators <white>| Command not found!")));
                }

                break;

            case "reload":
                skyGenerators.reloadConfiguration();
                sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-reload-success", "<dark_red>SkyGenerators <white>| Configuration has been reloaded!")));
                break;

            case "give":
                if (sender instanceof Player player && !player.hasPermission("skygenerator.command.give")){
                    sender.sendMessage(TextUtils.toComponent(
                            MessageUtils.getMessage("command-no-permission", "<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!")));
                    return true;
                }

                if (length <= 2){
                    sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-give-usage", "<white>| /sg give <green><player> <yellow><generator, generator-upgrade>")));
                    return true;
                }

                String targetPlayerId = args[1];
                String providedGeneratorId = args[2].toLowerCase();

                if (Bukkit.getPlayer(targetPlayerId) == null || !Objects.requireNonNull(Bukkit.getPlayer(targetPlayerId)).isOnline()){
                    sender.sendMessage(TextUtils.toComponent(
                            MessageUtils.getMessage("command-player-not-found","<dark_red>SkyGenerators <white>| <red>%player_name% is not online!").replace("%player_name%",targetPlayerId)));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(targetPlayerId);

                if (!skyGenerators.getGeneratorManager().getService().getGenerators().containsKey(providedGeneratorId) &&
                        skyGenerators.getGeneratorUpgradeManager().getService().getGeneratorUpgrade(providedGeneratorId) == null){
                    sender.sendMessage(TextUtils.toComponent(
                            MessageUtils.getMessage("command-generator-not-found","<dark_red>SkyGenerators <white>| <red>%generator_name% is not found!").replace("%generator_name%",providedGeneratorId)));
                    return true;
                }

                ItemStack item = skyGenerators.getGeneratorManager().getService().getGenerators().containsKey(providedGeneratorId) ?
                        skyGenerators.getGeneratorManager().getGenerator(providedGeneratorId).getAsItemStack() :
                        skyGenerators.getGeneratorUpgradeManager().getService().getGeneratorUpgrade(providedGeneratorId).getAsGenerator().getAsItemStack();
                if (InventoryUtils.hasEmptySlots(targetPlayer.getInventory())){
                    targetPlayer.getInventory().addItem(item);
                }else targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), item);

                sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-give-success",
                        "<white>| <green>%item% has been successfully given to %player%!".replace("%item%", item.getItemMeta().getDisplayName())
                                .replace("%player%", targetPlayerId))));
                break;

            case "menu":
                if (!(sender instanceof Player player)){
                    sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-player-only", "<white>| Sorry, but only players can do this!")));
                    return true;
                }

                if (!player.hasPermission("skygenerator.command.menu")){
                    sender.sendMessage(TextUtils.toComponent(
                            MessageUtils.getMessage("command-no-permission", "<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!")));
                    return true;
                }

                if (length == 1) skyGenerators.getGuiManager().openGeneratorsMenu(player);
                else if (length == 2) {
                    String targetPlayerIdMenu = args[1];
                    if (Bukkit.getPlayer(targetPlayerIdMenu) == null || !Objects.requireNonNull(Bukkit.getPlayer(targetPlayerIdMenu)).isOnline()){
                        sender.sendMessage(TextUtils.toComponent(
                                MessageUtils.getMessage("command-player-not-found","<dark_red>SkyGenerators <white>| <red>%player_name% is not online!").replace("%player_name%",targetPlayerIdMenu)));
                        return true;
                    }
                    if (!player.hasPermission("skygenerator.command.admin")){
                        sender.sendMessage(TextUtils.toComponent(
                                MessageUtils.getMessage("command-no-permission", "<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!")));
                        return true;
                    }

                    skyGenerators.getGuiManager().openGeneratorMenuOfPlayer(player ,Bukkit.getPlayer(targetPlayerIdMenu));
                }

                break;

            case "debug":
                if (!(sender instanceof Player player)){
                    sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-player-only", "<white>| Sorry, but only players can do this!")));
                    return true;
                }

                if (!player.hasPermission("skygenerator.command.debug")){
                    sender.sendMessage(TextUtils.toComponent(
                            MessageUtils.getMessage("command-no-permission", "<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!")));
                    return true;
                }

                if (SkyGenerators.isDebugMode()){
                    SkyGenerators.getInstance().getConfig().set("debug", false);
                    SkyGenerators.setDebugMode(false);
                    sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-debug-false", "<red>| Debug mode set to false!")));
                }else {
                    SkyGenerators.getInstance().getConfig().set("debug", true);
                    SkyGenerators.setDebugMode(true);
                    sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-debug-true", "<green>| Debug mode set to true!")));
                }

                break;

            case "help":
            default:
                sender.sendMessage(TextUtils.toComponent(MessageUtils.getMessage("command-help",
                        "<dark_red>SkyGenerators <white>|\n<white> | /sg help\n<white> | /sg list <gray>[OPTIONAL:gui]\n<white> | /sg give <green><player> <yellow><generator>\n<white> | /sg reload\n<white> | /sg debug\n<white> | /sg menu <gray>[OPTIONAL:playerName]")));
                break;
        }

        return true;
    }

    private void openListGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, TextUtils.toComponent(MessageUtils.getMessage("gui-generator-list","         <dark_red>Generators list")));
        for (BaseGenerator generator : skyGenerators.getGeneratorManager().getService().getGenerators().values())
            gui.addItem(skyGenerators.getGeneratorManager().getGenerator(generator).getAsItemStack());

        for (BaseGeneratorUpgrade baseGeneratorUpgrade : skyGenerators.getGeneratorUpgradeManager().getService().getGeneratorUpgradeMap().values())
            gui.addItem(baseGeneratorUpgrade.getAsGenerator().getAsItemStack());

        player.openInventory(gui);
    }
}