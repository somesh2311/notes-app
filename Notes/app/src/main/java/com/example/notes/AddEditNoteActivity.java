package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditNoteActivity extends AppCompatActivity {
    private EditText editTitle, editDescription,editDate;
    private NumberPicker numberpickerpriority;


    public static final String EXTRA_ID = "com.example.notes.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.notes.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.notes.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRORITY = "com.example.notes.EXTRA_PRORITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editDescription = (EditText) findViewById(R.id.edit_description);
        editDate = (EditText)findViewById(R.id.date);



        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if(intent.hasExtra( EXTRA_ID)){
            setTitle("Edit Note");
            editTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
           editDate.setText(intent.getStringExtra(EXTRA_PRORITY));

        }else {
            setTitle("Add Note");
        }


        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                editDate.setText(String.valueOf(sdf.format(myCalendar.getTime())));
            }

        };

        editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddEditNoteActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveNote() {
        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();
        String date = editDate.getText().toString();

        if(title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this, "Please Insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE,title);
        data.putExtra(EXTRA_DESCRIPTION,description);
        data.putExtra(EXTRA_PRORITY,date);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id != -1){
            data.putExtra(EXTRA_ID,id);
        }

        setResult(RESULT_OK,data);
        finish();

    }
}
