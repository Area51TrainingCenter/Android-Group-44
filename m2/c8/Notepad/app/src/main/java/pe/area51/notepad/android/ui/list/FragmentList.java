package pe.area51.notepad.android.ui.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import pe.area51.notepad.R;
import pe.area51.notepad.android.Application;
import pe.area51.notepad.android.ViewModelFactory;
import pe.area51.notepad.android.ui.FragmentBase;
import pe.area51.notepad.domain.Note;

public class FragmentList extends FragmentBase {

    public static final String TAG = "ListFragment";

    private ArrayAdapter<Note> notesArrayAdapter;

    private ViewModelList viewModelList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModelList = ViewModelProviders
                .of(this, viewModelFactory)
                .get(ViewModelList.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionCreateNote:
                createNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list, container, false);
        final ListView listViewElements = view.findViewById(R.id.listView);
        notesArrayAdapter = new NoteAdapter(getActivity());
        listViewElements.setAdapter(notesArrayAdapter);
        listViewElements.setOnItemClickListener((adapterView, view1, i, l) -> {
            final Note note = notesArrayAdapter.getItem(i);
            fragmentInteractionInterface.showNoteContent(note.getId());
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentInteractionInterface.setTitle(getString(R.string.app_name));
    }

    private void getNotes() {
        viewModelList.fetchAllNotes();
        viewModelList.getFetchAllNotesResponse()
                .observe(this, this::onNotesChanged);
    }

    private void onNotesChanged(final List<Note> notes) {
        notesArrayAdapter.clear();
        notesArrayAdapter.addAll(notes);
    }

    private void createNote() {
        final Random random = new Random();
        final int randomInt = random.nextInt();
        final String title = "Title " + randomInt;
        final String content = getString(R.string.lorem_ipsum);
        final long creationTimestamp = System.currentTimeMillis();
        viewModelList.createNote(title, content, creationTimestamp);
    }

    private static class NoteAdapter extends ArrayAdapter<Note> {

        private final LayoutInflater layoutInflater;

        public NoteAdapter(final Context context) {
            super(context, 0);
            layoutInflater = LayoutInflater.from(getContext());
        }

        private static class ViewHolder {
            TextView titleTextView;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "Position: " + position + "; convertView " + (convertView == null ? "== null" : "!= null"));
            final Note note = getItem(position);
            final View view;
            final ViewHolder viewHolder;
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.element_note, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = view.findViewById(R.id.textViewNoteTitle);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.titleTextView.setText(note.getTitle());
            return view;
        }
    }
}
