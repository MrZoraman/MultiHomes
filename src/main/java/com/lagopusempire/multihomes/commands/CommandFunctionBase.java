package com.lagopusempire.multihomes.commands;

import com.lagopusempire.multihomes.config.ConfigKeys;
import com.lagopusempire.multihomes.config.PluginConfig;
import com.lagopusempire.multihomes.messages.MessageKeys;
import com.lagopusempire.multihomes.messages.Messages;
import com.lagopusempire.multihomes.permissions.Permissions;
import com.lagopusempire.multihomes.util.UUIDFetcher;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author MrZoraman
 */
public class CommandFunctionBase
{
    private static String uuid_regex = null;
    
    static
    {
        uuid_regex = PluginConfig.getString(ConfigKeys.UUID_REGEX);
        System.out.println("UUID REGEX: " + uuid_regex);
    }
    
    @FunctionalInterface
    protected interface PlayerLookupCallback
    {
        public void playerFound(String name, UUID uuid);
    }
    
    protected final JavaPlugin plugin;
    
    public CommandFunctionBase(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    protected boolean checkPerms(CommandSender sender, Permissions perm)
    {
        if(!perm.check(sender))
        {
            sender.sendMessage(getNoPermsMsg(perm));
            return false;
        }
        
        return true;
    }
    
    protected void getPlayer(String playerName, PlayerLookupCallback callback)
    {
        if(playerName.matches(uuid_regex))
        {
            callback.playerFound(playerName, UUID.fromString(playerName));
            return;
        }
        
        final Set<? extends Player> onlinePlayers = new HashSet<>(plugin.getServer().getOnlinePlayers());
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> 
        {
            for(Player player : onlinePlayers)
            {
                if(player.getName().equalsIgnoreCase(playerName))
                {
                    plugin.getServer().getScheduler().runTask(plugin, () -> callback.playerFound(playerName, player.getUniqueId()));
                    return;
                }
            }
            
            final UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(playerName));
            Map<String, UUID> response = null;
            try
            {
                response = fetcher.call();
            }
            catch (Exception e)
            {
                plugin.getLogger().warning("Failed to lookup uuid for player '" + playerName + "'!");
                e.printStackTrace();
                plugin.getServer().getScheduler().runTask(plugin, () -> callback.playerFound(null, null));
                return;
            }
            
            final UUID uuid = response.get(playerName);
            plugin.getServer().getScheduler().runTask(plugin, () -> callback.playerFound(playerName, uuid));
        });
    }
    
    private String getNoPermsMsg(Permissions perm)
    {
        return Messages.getMessage(MessageKeys.NO_PERMISSION)
                .colorize()
                .replace("node", perm.getNode())
                .toString();
    }
}