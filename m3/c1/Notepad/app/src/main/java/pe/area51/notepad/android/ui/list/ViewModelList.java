package pe.area51.notepad.android.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pe.area51.notepad.domain.Note;
import pe.area51.notepad.domain.NotesRepository;

public class ViewModelList extends ViewModel {

    private final NotesRepository notesRepository;
    private final MediatorLiveData<List<Note>> fetchAllNotesResponse;
    private final MutableLiveData<Note> createNoteResponse;

    public ViewModelList(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
        fetchAllNotesResponse = new MediatorLiveData<>();
        createNoteResponse = new MutableLiveData<>();
    }

    public void fetchAllNotes() {
        Log.d("ViewModelList", "fetchAllNotes");
        AsyncTask.execute(() -> {
            final LiveData<List<Note>> result = notesRepository.subscribeToAllNotes();
            //Debemos llamar a "postValue" si no estamos en el MainThread.
            //Este observador se ejecuta en el MainThread,
            //por lo que podemos utilizar "setValue".
            fetchAllNotesResponse.addSource(result, fetchAllNotesResponse::setValue);
        });
    }

    public void createNote(final String title,
                           final String content,
                           final long creationTimestamp) {
        final Note note = new Note(
                title,
                content,
                creationTimestamp
        );
        AsyncTask.execute(() -> {
            final Note createdNote = notesRepository.createNote(note);
            createNoteResponse.postValue(createdNote);
        });
    }

    public LiveData<List<Note>> getFetchAllNotesResponse() {
        return fetchAllNotesResponse;
    }

    public MutableLiveData<Note> getCreateNoteResponse() {
        return createNoteResponse;
    }
}
