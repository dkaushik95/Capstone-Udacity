package anunciar.dishant.com.anunciar.Database;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by dishantkaushik on 12/18/16.
 */

@SimpleSQLTable(table = "announcement", provider = "AnnouncementProvider")
public class AnnouncementTableDefinition {

    @SimpleSQLColumn(value = "_id", primary = true)
    public static int _ID;

    @SimpleSQLColumn(value = "title")
    public static String TITLE;

    @SimpleSQLColumn("description")
    public static String DESCRIPTION;

    @SimpleSQLColumn("deadline")
    public static String DEADLINE;

    @SimpleSQLColumn("tags")
    public static String TAGS;

    @SimpleSQLColumn("created_at")
    public static String CREATED;

    @SimpleSQLColumn("updated_at")
    public static String UPDATED;
}