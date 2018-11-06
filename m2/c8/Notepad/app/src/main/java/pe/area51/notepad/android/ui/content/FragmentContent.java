package pe.area51.notepad.android.ui.content;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import pe.area51.notepad.R;
import pe.area51.notepad.android.ui.FragmentBase;
import pe.area51.notepad.domain.Note;

public class FragmentContent extends FragmentBase {

    private static final String KEY_ARG_NOTE_ID = "noteId";

    private TextView textViewDate;
    private TextView textViewContent;

    private String noteId;

    private ViewModelContent viewModelContent;

    public static FragmentContent newInstance(final String noteId) {
        final FragmentContent contentFragment = new FragmentContent();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_ARG_NOTE_ID, noteId);
        contentFragment.setArguments(arguments);
        return contentFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelContent = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(ViewModelContent.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        checkArguments(arguments);
        noteId = arguments.getString(KEY_ARG_NOTE_ID);
        viewModelContent.fetchNoteById(noteId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_content, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionDeleteNote) {
            deleteNote();
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_content, container, false);
        textViewDate = view.findViewById(R.id.textViewNoteDate);
        textViewContent = view.findViewById(R.id.textViewNoteContent);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModelContent.getFetchNoteByIdResponse().observe(
                this,
                this::showNote
        );
    }

    private static void checkArguments(final Bundle arguments) {
        if (arguments != null &&
                arguments.containsKey(KEY_ARG_NOTE_ID)) {
            return;
        }
        throw new RuntimeException("Fragment doesn't have needed arguments. Call newInstance() static creation method.");
    }

    private void showNote(final Note note) {
        if (note == null) {
            return;
        }
        final Date noteDate = new Date(note.getCreationTimestamp());
        final DateFormat dateFormat = DateFormat.getInstance();
        textViewDate.setText(dateFormat.format(noteDate));
        textViewContent.setText(note.getContent());
        fragmentInteractionInterface.setTitle(note.getTitle());
    }

    private void deleteNote() {
        viewModelContent.deleteNoteById(noteId);
        fragmentInteractionInterface.closeFragment();
    }
}
