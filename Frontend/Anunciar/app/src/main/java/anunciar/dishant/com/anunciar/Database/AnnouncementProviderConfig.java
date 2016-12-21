package anunciar.dishant.com.anunciar.Database;


import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

@SimpleSQLConfig(
        name = "AnnouncementProvider",
        authority = "anunciar.dishant.com.anunciar.Database",
        database = "anuncement.db",
        version = 1)
public class AnnouncementProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}