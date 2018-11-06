package pe.area51.notepad.data.room;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import pe.area51.notepad.domain.Note;
import pe.area51.notepad.domain.NotesRepository;

public class RoomNotesRepository implements NotesRepository {

    private final RoomDatabase roomDatabase;

    public RoomNotesRepository(RoomDatabase roomDatabase) {
        this.roomDatabase = roomDatabase;
    }

    @NonNull
    @Override
    public List<Note> getAllNotes() {
        final List<pe.area51.notepad.data.room.Note> roomNotes
                = roomDatabase.getNoteDao().getAll();
        final List<Note> domainNotes = new ArrayList<>();
        for (final pe.area51.notepad.data.room.Note roomNote : roomNotes) {
            domainNotes.add(createDomainNote(roomNote));
        }
        return domainNotes;
    }

    @NonNull
    @Override
    public LiveData<List<Note>> subscribeToAllNotes() {
        final LiveData<List<pe.area51.notepad.data.room.Note>> source =
                roomDatabase.getNoteDao().subscribeToAll();
        return Transformations.map(
                source, input -> {
                    final List<Note> domainNotes = new ArrayList<>();
                    for (final pe.area51.notepad.data.room.Note roomNote : input) {
                        domainNotes.add(createDomainNote(roomNote));
                    }
                    return domainNotes;
                }
        );
    }

    @Override
    public boolean updateNote(@NonNull Note note) {
        return roomDatabase.getNoteDao().update(createRoomNote(note))
                != 0;
    }

    @Override
    public boolean deleteNote(@NonNull String noteId) {
        return roomDatabase.getNoteDao().delete(new pe.area51.notepad.data.room.Note(
                Long.valueOf(noteId)
        )) != 0;
    }

    @NonNull
    @Override
    public Note createNote(@NonNull Note note) {
        final long createdNoteId = roomDatabase.getNoteDao().insert(createRoomNote(note));
        return new Note(
                String.valueOf(createdNoteId),
                note.getTitle(),
                note.getContent(),
                note.getCreationTimestamp()
        );
    }

    @NonNull
    @Override
    public LiveData<Note> subscribeToNoteById(@NonNull String noteId) {
        final LiveData<pe.area51.notepad.data.room.Note> roomNote =
                roomDatabase.getNoteDao().subscribeById(Long.valueOf(noteId));
        return Transformations.map(roomNote, this::createDomainNote);
    }

    public Note createDomainNote(final pe.area51.notepad.data.room.Note roomNote) {
        return new Note(
                String.valueOf(roomNote.id),
                roomNote.title,
                roomNote.content,
                roomNote.creationTimestamp
        );
    }

    private pe.area51.notepad.data.room.Note createRoomNote(final Note domainNote) {
        long id;
        try {
            id = Long.valueOf(domainNote.getId());
        } catch (NumberFormatException e) {
            id = 0;
        }
        return new pe.area51.notepad.data.room.Note(
                id,
                domainNote.getTitle(),
                domainNote.getContent(),
                domainNote.getCreationTimestamp()
        );
    }
}
