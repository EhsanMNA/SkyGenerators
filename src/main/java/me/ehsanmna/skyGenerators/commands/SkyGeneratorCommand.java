package me.ehsanmna.skyGenerators.commands;

import me.ehsanmna.skyGenerators.SkyGenerators;
import me.ehsanmna.skyGenerators.models.BaseGenerator;
import me.ehsanmna.skyGenerators.models.Generator;
import me.ehsanmna.skyGenerators.utils.InventoryUtils;
import me.ehsanmna.skyGenerators.utils.TextUtils;
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
            sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| TO get help type /sg help!"));
            return true;
        }

        String subcommand = args[0].toLowerCase();


        switch (subcommand){
            case "list":
                if (sender instanceof Player player && !player.hasPermission("skygenerator.command.list")){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!"));
                    return true;
                }

                if (length == 1){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>|"));
                    for (BaseGenerator generator : skyGenerators.getGeneratorManager().getService().getGenerators().values())
                        sender.sendMessage(TextUtils.toComponent("<white> | "+generator.getName()+"<gray> "+generator.getId()));
                }else if (length == 2){
                    if (args[1].equalsIgnoreCase("gui"))
                        if (sender instanceof Player player) openListGUI(player);
                    else sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Command not found!!"));
                }

                break;

            case "reload":
                skyGenerators.reloadConfiguration();
                sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Configuration has been reloaded!"));
                break;

            case "give":
                if (sender instanceof Player player && !player.hasPermission("skygenerator.command.give")){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!"));
                    return true;
                }

                if (length <= 2){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| /sg give <green><player> <yellow><generator>"));
                    return true;
                }

                String targetPlayerId = args[1];
                String providedGeneratorId = args[2].toLowerCase();

                if (Bukkit.getPlayer(targetPlayerId) == null || !Objects.requireNonNull(Bukkit.getPlayer(targetPlayerId)).isOnline()){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| <red>"+targetPlayerId+" is not online!"));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(targetPlayerId);

                if (!skyGenerators.getGeneratorManager().getService().getGenerators().containsKey(providedGeneratorId)){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| <red>"+providedGeneratorId+" is not found!"));
                    return true;
                }

                ItemStack item = skyGenerators.getGeneratorManager().getGenerator(providedGeneratorId).getAsItemStack();
                if (InventoryUtils.hasEmptySlots(targetPlayer.getInventory())){
                    targetPlayer.getInventory().addItem(item);
                }else targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), item);

                sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| <green>Item has been successfully given!"));

                break;

            case "menu":
                if (!(sender instanceof Player player)){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Sorry, but you only players can do this!"));
                    return true;
                }

                if (!player.hasPermission("skygenerator.command.menu")){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!"));
                    return true;
                }

                if (length == 1) skyGenerators.getGuiManager().openGeneratorsMenu(player);
                else if (length == 2) {
                    targetPlayerId = args[1];
                    if (Bukkit.getPlayer(targetPlayerId) == null || !Objects.requireNonNull(Bukkit.getPlayer(targetPlayerId)).isOnline()){
                        sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| <red>"+targetPlayerId+" is not online!"));
                        return true;
                    }


                }

                break;

            case "debug":
                if (!(sender instanceof Player player)){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Sorry, but you only players can do this!"));
                    return true;
                }

                if (!player.hasPermission("skygenerator.command.debug")){
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Sorry, but you don't have permission to do this!"));
                    return true;
                }

                if (SkyGenerators.isDebugMode()){
                    SkyGenerators.getInstance().getConfig().set("debug", false);
                    SkyGenerators.setDebugMode(false);
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>| Debug mode set to false!"));
                }else {
                    SkyGenerators.getInstance().getConfig().set("debug", true);
                    SkyGenerators.setDebugMode(true);
                    sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <green>| Debug mode set to true!"));
                }

                break;

            case "help":
            default:
                sender.sendMessage(TextUtils.toComponent("<dark_red>SkyGenerators <white>|"));
                sender.sendMessage(TextUtils.toComponent("<white> | /sg help"));
                sender.sendMessage(TextUtils.toComponent("<white> | /sg list <gray>[OPTIONAL:gui]"));
                sender.sendMessage(TextUtils.toComponent("<white> | /sg give <green><player> <yellow><generator>"));
                sender.sendMessage(TextUtils.toComponent("<white> | /sg reload"));
                sender.sendMessage(TextUtils.toComponent("<white> | /sg debug"));
                sender.sendMessage(TextUtils.toComponent("<white> | /sg menu <gray>[OPTIONAL:playerName]"));
                break;
        }

        return true;
    }

    private void openListGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, TextUtils.toComponent("         <dark_red>Generators list"));
        for (BaseGenerator generator : skyGenerators.getGeneratorManager().getService().getGenerators().values())
            gui.addItem(skyGenerators.getGeneratorManager().getGenerator(generator).getAsItemStack());

        player.openInventory(gui);
    }


}
