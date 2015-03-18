package com.lagopusempire.multihomes;

import com.lagopusempire.multihomes.config.ConfigKeys;
import com.lagopusempire.multihomes.config.PluginConfig;
import javax.persistence.PersistenceException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author MrZoraman
 */
public class MultiHomes extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        boolean success = reload();
        if(success == false)
        {
            getLogger().severe("Something went wrong while enabling or reloading " + getDescription().getName() + "! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable()
    {

    }

    public boolean reload()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();

        PluginConfig.setConfig(getConfig());
        
        if(PluginConfig.getBoolean(ConfigKeys.USE_DATABASE) || PluginConfig.getBoolean(ConfigKeys.USE_DATABASE))
        {
            boolean result = setupDatabase();
            if(result == false)
            {
                return false;
            }
        }
        
        return true;
    }

    private boolean setupDatabase()
    {
        try
        {
            getDatabase().find(com.lagopusempire.multihomes.homeIO.database.DBHome.class).findRowCount();
        }
        catch (PersistenceException ignored)
        {
            getLogger().info("Installing database for " + getDescription().getName() + " due to first time usage");
            try
            {
                installDDL();
            }
            catch (RuntimeException e)
            {
                getLogger().severe(e.getMessage());
                return false;
            }
        }
        
        return true;
    }
}