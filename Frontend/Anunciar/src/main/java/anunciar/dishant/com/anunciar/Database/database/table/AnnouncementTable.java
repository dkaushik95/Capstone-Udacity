package anunciar.dishant.com.anunciar.Database.database.table;

public interface AnnouncementTable {
    String TABLE_NAME = "announcement";

    String _ID = "_id";

    String ID = "id";
    String TITLE = "title";
    String DESCRIPTION = "description";
    String DEADLINE = "deadline";
    String TAGS = "tags";
    String CREATED_AT = "created_at";
    String UPDATED_AT = "updated_at";
    String[] ALL_COLUMNS = new String[] {_ID, ID, TITLE, DESCRIPTION, DEADLINE, TAGS, CREATED_AT, UPDATED_AT};

    String SQL_CREATE = "CREATE TABLE announcement ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, title TEXT, description TEXT, deadline TEXT, tags TEXT, created_at TEXT, updated_at TEXT )";

    String SQL_INSERT = "INSERT INTO announcement ( id, title, description, deadline, tags, created_at, updated_at ) VALUES ( ?, ?, ?, ?, ?, ?, ? )";

    String SQL_DROP = "DROP TABLE IF EXISTS announcement";

    String WHERE_ID_EQUALS = _ID + "=?";

}