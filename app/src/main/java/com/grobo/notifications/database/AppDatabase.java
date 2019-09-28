package com.grobo.notifications.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.grobo.notifications.admin.clubevents.ClubEventDao;
import com.grobo.notifications.admin.clubevents.ClubEventItem;
import com.grobo.notifications.clubs.ClubDao;
import com.grobo.notifications.clubs.ClubItem;
import com.grobo.notifications.feed.Converters;
import com.grobo.notifications.feed.FeedDao;
import com.grobo.notifications.feed.FeedItem;
import com.grobo.notifications.notifications.Notification;
import com.grobo.notifications.notifications.NotificationDao;
import com.grobo.notifications.todolist.Goal;
import com.grobo.notifications.todolist.TodoDao;

@Database(entities = {Notification.class, FeedItem.class, ClubItem.class, ClubEventItem.class, Goal.class}, version = 4, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract NotificationDao notificationDao();

    public abstract FeedDao feedDao();

    public abstract ClubDao clubDao();

    public abstract ClubEventDao eventDao();

    public abstract TodoDao todoDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigrationFrom(1)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `todo` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + " `name` TEXT,"
                    + " `checked` INTEGER NOT NULL DEFAULT 0,"
                    + " `timestamp` INTEGER NOT NULL DEFAULT 0,"
                    + " `alarm` INTEGER NOT NULL DEFAULT 0)");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notification"
                    + " ADD COLUMN link TEXT");
        }
    };
}
