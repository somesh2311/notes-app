package com.example.notes;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
@Database(entities = {Note.class},version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
        private static NoteDatabase instance;
      public abstract NoteDao noteDao();
        public static synchronized NoteDatabase getInstance(Context context) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                       NoteDatabase.class, "note_database")
                        .fallbackToDestructiveMigration()
                        .addCallback(roomcallback)
                        .build();

            }
            return instance;
        }
           private static RoomDatabase.Callback roomcallback = new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    new PopulateDBAsyncTask(instance).execute();
                }
            };

          private static class PopulateDBAsyncTask extends AsyncTask<Void,Void,Void>{
           private NoteDao noteDao;

              private PopulateDBAsyncTask(NoteDatabase db) {

                  noteDao = db.noteDao();
              }

              @Override
              protected Void doInBackground(Void... voids) {
                  noteDao.insert(new Note("Title1","Description1", "24/11/18" ));
                  noteDao.insert(new Note("Title2","Description2","23/11/19"));
                  noteDao.insert(new Note("Title3","Description3","23/11/15"));
                  return null;
              }
          }
}
