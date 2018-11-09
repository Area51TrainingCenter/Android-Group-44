package pe.area51.notepad.domain;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public interface NotesRepository {

    @NonNull
    List<Note> getAllNotes();

    @NonNull
    LiveData<List<Note>> subscribeToAllNotes();

    boolean updateNote(@NonNull final Note note);

    boolean deleteNote(@NonNull final String noteId);

    @NonNull
    Note createNote(@NonNull final Note note);

    @NonNull
    LiveData<Note> subscribeToNoteById(@NonNull final String noteId);
}
