package pe.area51.notepad.data.sqlite;

import android.arch.lifecycle.LiveData;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pe.area51.notepad.domain.Note;
import pe.area51.notepad.domain.NotesRepository;

public class SqLiteDatabase implements NotesRepository {

    private final DatabaseManager databaseManager;

    public SqLiteDatabase(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @NonNull
    @Override
    public LiveData<List<Note>> subscribeToAllNotes() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @NonNull
    @Override
    public List<Note> getAllNotes() {
        final Cursor cursor = databaseManager.getReadableDatabase().rawQuery("SELECT * FROM notes", null);
        final List<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            final long id = cursor.getLong(cursor.getColumnIndex("id"));
            final String title = cursor.getString(cursor.getColumnIndex("title"));
            final String content = cursor.getString(cursor.getColumnIndex("content"));
            final long creationTimestamp = cursor.getLong(cursor.getColumnIndex("creationTimestamp"));
            notes.add(new Note(
                    String.valueOf(id),
                    title,
                    content,
                    creationTimestamp
            ));
        }
        cursor.close();
        return notes;
    }

    @Override
    public boolean updateNote(@NonNull Note note) {
        return false;
    }

    @Override
    public boolean deleteNote(@NonNull String noteId) {
        return false;
    }

    @NonNull
    @Override
    public Note createNote(@NonNull Note note) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("title", note.getTitle());
        contentValues.put("content", note.getContent());
        contentValues.put("creationTimestamp", note.getCreationTimestamp());
        final long id = databaseManager.getWritableDatabase().insert(
                "notes",
                null,
                contentValues
        );
        return new Note(
                String.valueOf(id),
                note.getTitle(),
                note.getContent(),
                note.getCreationTimestamp()
        );
    }
}
