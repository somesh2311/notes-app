package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> implements Filterable {
    Context context;
    private List<Note> notes = new ArrayList<>();
    private List<Note> notesfull = new ArrayList<>();
    private OnItemClickListener listener;
    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

          View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
           return  new NoteHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int position) {
        Note currentnote = notes.get(position);
        holder.title.setText(currentnote.getTitle());
        holder.description.setText(currentnote.getDescription());
        holder.priority.setText(String.valueOf(currentnote.getDate()));
       setFadeAnimation(holder.itemView);

    }
    private void setFadeAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(400);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
         return  notes == null ? 0 : notes.size();
    }
    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notesfull = new ArrayList<>(notes);
        notifyDataSetChanged();
    }
    public void restoreItem(Note note,int position){
        notes.add(position,note);
        notifyItemInserted(position);
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    @Override
    public Filter getFilter() {

        return mfilter;
    }
    private Filter mfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(notesfull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Note item : notesfull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };




    public class NoteHolder extends RecyclerView.ViewHolder {
        private TextView title, description, priority;
//        private ImageView imgcall;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            description = itemView.findViewById(R.id.text_description);
            priority = itemView.findViewById(R.id.text_priority);
//            imgcall = itemView.findViewById(R.id.image_call);
//            imgcall.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String number = "9500803958";
//                    String dial = "tel:" + number;
//                   v.getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
//                }
//            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notes.get(position));
                    }
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



}