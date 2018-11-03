package pe.area51.notepad.data.ram_database;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pe.area51.notepad.domain.Note;
import pe.area51.notepad.domain.NotesRepository;

public class RamDatabase implements NotesRepository {

    private final List<Note> notes;

    public RamDatabase() {
        this.notes = new ArrayList<>();
    }

    @NonNull
    @Override
    public List<Note> getAllNotes() {
        return notes;
    }

    @NonNull
    @Override
    public LiveData<List<Note>> subscribeToAllNotes() {
        throw new UnsupportedOperationException("Not implemented!");
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
        final int id = notes.size() + 1;
        final Note createdNote = new Note(
                String.valueOf(id),
                note.getTitle(),
                note.getContent(),
                note.getCreationTimestamp()
        );
        notes.add(createdNote);
        return createdNote;
    }
}
