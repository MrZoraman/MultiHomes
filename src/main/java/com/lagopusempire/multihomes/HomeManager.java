package com.lagopusempire.multihomes;

import com.lagopusempire.multihomes.home.Home;
import com.lagopusempire.multihomes.home.LoadResult;
import com.lagopusempire.multihomes.homeIO.HomeIO;
import com.lagopusempire.multihomes.homeIO.HomeListLoadedCallback;
import com.lagopusempire.multihomes.homeIO.HomeLoadedCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author MrZoraman
 */
public class HomeManager implements Listener
{

    private static final int PLAYER_LOGIN_FAIL_TIMEOUT = 20 * 15;//15 seconds

    private final Map<UUID, Map<String, Home>> homes = new HashMap<>();

    private final HomeIO io;
    private final MultiHomes plugin;

    public HomeManager(MultiHomes plugin, HomeIO io)
    {
        this.plugin = plugin;
        this.io = io;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinAsync(AsyncPlayerPreLoginEvent event)
    {
        boolean notifiedConsole = false;
        int failCounter = 0;
        //ASYNC
        while (plugin.isEnabled() == false)
        {
            if (!notifiedConsole)
            {
                plugin.getLogger().warning("Prventing player " + event.getName() + " from joining the server because multihomes isn't loaded yet.");
                notifiedConsole = true;
            }

            failCounter++;

            if (failCounter >= PLAYER_LOGIN_FAIL_TIMEOUT)
            {
                event.setKickMessage("Server is experiencing internal errors. Please try again later.");
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                return;
            }
        }

        final UUID uuid = event.getUniqueId();

        //the plugin has been loaded at this point
        io.loadHomes(event.getUniqueId(), (loadedHomes) ->
        {
            //SYNC
            if (homes.get(uuid) == null)
            {
                homes.put(uuid, new HashMap<>());
            }

            homes.get(uuid).putAll(loadedHomes);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        homes.remove(event.getPlayer().getUniqueId());
    }

    public void setHome(Player player, UUID owner, String homeName, Runnable callback)
    {
        final Home home = new Home(owner, homeName, player.getLocation());
        io.saveHome(home, callback);
        homes.get(owner).put(homeName, home);
    }

    public void getHome(UUID owner, String homeName, HomeLoadedCallback callback)
    {
        if (homes.containsKey(owner)) //True if the player is online
        {
            final Home home = homes.get(owner).get(homeName);
            if (home != null)
            {
                callback.homeLoaded(home);
            }
            callback.homeLoaded(new Home(owner, homeName, LoadResult.DOES_NOT_EXIST));
        }

        //The player is offline
        io.loadHome(owner, homeName, callback);
    }

    public void getHomeList(UUID owner, HomeListLoadedCallback callback)
    {
        if (homes.containsKey(owner))
        {
            final Set<String> homeSet = homes.get(owner).keySet();
            final List<String> homeList = new ArrayList<>(homeSet);
            java.util.Collections.sort(homeList);
            callback.homeListLoaded(homeList);
        }
        
        io.getHomeList(owner, callback);
    }
}
