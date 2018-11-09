package pe.area51.notepad.android.ui.content;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import pe.area51.notepad.domain.Note;
import pe.area51.notepad.domain.NotesRepository;

public class ViewModelContent extends ViewModel {

    private final NotesRepository notesRepository;
    private final MediatorLiveData<Note> fetchNoteByIdResponse;

    public ViewModelContent(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
        fetchNoteByIdResponse = new MediatorLiveData<>();
    }

    public void fetchNoteById(@NonNull final String noteId) {
        AsyncTask.execute(() -> fetchNoteByIdResponse.addSource(
                notesRepository.subscribeToNoteById(noteId),
                fetchNoteByIdResponse::setValue
        ));
    }

    public void deleteNoteById(@NonNull final String noteId) {
        AsyncTask.execute(() -> notesRepository.deleteNote(noteId));
    }

    public void updateNote(final Note note) {
        AsyncTask.execute(() -> notesRepository.updateNote(note));
    }

    public LiveData<Note> getFetchNoteByIdResponse() {
        return fetchNoteByIdResponse;
    }
}
