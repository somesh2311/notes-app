package com.example.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dingmouren.layoutmanagergroup.banner.BannerLayoutManager;
import com.dingmouren.layoutmanagergroup.skidright.SkidRightLayoutManager;
import com.dingmouren.layoutmanagergroup.slide.ItemTouchHelperCallback;
import com.dingmouren.layoutmanagergroup.slide.SlideLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_REQUEST_CODE = 1;
    private static final int EDIT_REQUEST_CODE = 2;
    private NoteViewModel noteViewModel;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    NoteAdapter noteAdapter;
    androidx.appcompat.widget.SearchView searchView;
    Button button;
    private static final int REQUEST_CALL = 1;


    ItemTouchHelperCallback mItemTouchHelperCallback;
    ItemTouchHelper  mItemTouchHelper;
    SlideLayoutManager mSlideLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.emptyview);
        button = (Button) findViewById(R.id.add_notes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddEditNoteActivity.class), ADD_REQUEST_CODE);
            }
        });
//        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
//    }
    floatingActionButton = (FloatingActionButton) findViewById(R.id.add);
        searchView = (androidx.appcompat.widget.SearchView) findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        noteAdapter = new NoteAdapter();
        //empty recyclerview
        noteAdapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, linearLayout));
        recyclerView.setAdapter(noteAdapter);
        // set action on fab
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.setNotes(notes);


            }
        });
       SwipeToDeleteHelper swipeToDeleteHelper = new SwipeToDeleteHelper(this) {
           @Override
           public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

               AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
               alertdialog.setMessage("Do You Want to delete this appplication?")
                       .setCancelable(false)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                               Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                               noteViewModel.getAllNotes().observe(MainActivity.this, new Observer<List<Note>>() {
                                   @Override
                                   public void onChanged(List<Note> notes) {
                                       noteAdapter.setNotes(notes);

                                   }
                               });
                           }
                       });
             AlertDialog alert = alertdialog.create();
             alert.setTitle("Notes");
             alert.show();

           }
       };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper( swipeToDeleteHelper );
        itemTouchHelper.attachToRecyclerView( recyclerView );
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRORITY, note.getDate());
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }

//        @Override
//        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//            if (requestCode == REQUEST_CALL) {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            String title = Objects.requireNonNull(data).getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
            String date = data.getStringExtra(AddEditNoteActivity.EXTRA_PRORITY);
            Note note = new Note(title, description, date);
            noteViewModel.insert(note);
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Note cann't be updated", Toast.LENGTH_LONG).show();
                return;
            }
            String title = Objects.requireNonNull(data).getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String date = data.getStringExtra(AddEditNoteActivity.EXTRA_PRORITY);
            Note note = new Note(title, description, date);
            note.setId(id);
            noteViewModel.update(note);
        } else {
            Toast.makeText(this, "Note not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteALLNotes();
                Toast.makeText(this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
