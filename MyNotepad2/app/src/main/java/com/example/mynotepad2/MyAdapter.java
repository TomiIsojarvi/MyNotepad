package com.example.mynotepad2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;
import java.util.Observable;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Note> list;
    NoteModel noteModel;
    boolean isEnable = false;
    boolean isSelectAll = false;
    ArrayList<Note> selectList = new ArrayList<Note>();

    public MyAdapter(Activity activity, ArrayList<Note> list) {

        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);

        // Initialize view model
        noteModel = ViewModelProviders.of((FragmentActivity) activity).get(NoteModel.class);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note note = list.get(position);
        holder.note.setText(note.getNote());
        holder.dateAndTime.setText(note.getCurrentDate() + ' ' +  note.getCurrentTime());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                if (!isEnable) {
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu, menu);

                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            isEnable = true;
                            ClickItem(holder);

                            // Set an observer on get note method
                            noteModel.getNote().observe((LifecycleOwner) activity, new Observer<Note>() {
                                @Override
                                public void onChanged(Note note) {
                                    // When note change, set note on action mode title
                                    //actionMode.setTitle
                                }
                            });

                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            // When clicked on action mode item, get item id
                            int id = menuItem.getItemId();

                            // Delete
                            if (id == R.id.menu_delete) {

                                for (Note note : selectList) {
                                    list.remove(note);
                                }

                                // Finish action mode
                                actionMode.finish();
                            } else if (id == R.id.menu_select_all) {
                                if (selectList.size() == list.size()) {
                                    isSelectAll = false;
                                    selectList.clear();
                                } else {
                                    isSelectAll = true;
                                    selectList.clear();
                                    selectList.addAll(list);
                                }

                                // Notify adapter
                                notifyDataSetChanged();
                            }

                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            // Action mode off
                            isEnable = false;
                            isSelectAll = false;
                            selectList.clear();
                            notifyDataSetChanged();
                        }
                    };

                    // Start action mode
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    ClickItem(holder);
                }

                return true;
            }
        });

        if (isSelectAll) {
            holder.ivCheckBox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.ivCheckBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isEnable) {
                    ClickItem(holder);
                }
            }
        });
    }

    private void ClickItem(MyViewHolder holder) {
        // Get selected item value
        Note note = list.get(holder.getAdapterPosition());

        // Check checkmark visibility
        if (holder.ivCheckBox.getVisibility() == View.GONE) {
            holder.ivCheckBox.setVisibility(View.VISIBLE);
            // Change background color
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            // Add to the selected items list
            selectList.add(note);
        } else {
            holder.ivCheckBox.setVisibility(View.GONE);
            // Change background color back to transparent
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            // Remove from the selected items list
            selectList.remove(note);
        }

        // Set view model
        noteModel.setNote(note);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView note, dateAndTime;
        ImageView ivCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            note = itemView.findViewById(R.id.tvNote);
            dateAndTime = itemView.findViewById(R.id.tvDateTime);
            ivCheckBox = itemView.findViewById(R.id.iv_check_box);
        }
    }
}