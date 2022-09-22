package com.bdreiss.zentodo;
/*
 *   A custom ArrayAdapter<Entry> that creates rows with checkboxes that
 *   when checked remove the associated task.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bdreiss.zentodo.dataManipulation.Data;
import com.bdreiss.zentodo.dataManipulation.Entry;

import java.util.ArrayList;

public class TaskListAdapter extends ArrayAdapter<Entry>{

    private ArrayList<Entry> entries;//list of entries (see Entry.java)

    Context context;

    private Data data;//database from which entries are derived (see Data.java)

    private class ViewHolder {//temporary view

        private LinearLayout linearLayout;
        private LinearLayout linearLayoutAlt;
        private LinearLayout linearLayoutEdit;
        protected CheckBox checkBox;//Checkbox to remove entry
        private TextView task;//Description of the task
        private Button menu;
        private Button edit;
        private Button setDate;
        private Button setList;
        private Button back;
        private EditText editText;
        private Button backEdit;
    }

    public TaskListAdapter(Context context, Data data){
        super(context, R.layout.row,data.getEntries());
        this.context = context;
        this.data = data;
        this.entries = data.getEntries();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row, null, true);

            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linear_layout);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.task = (TextView) convertView.findViewById(R.id.task);
            holder.menu = (Button) convertView.findViewById(R.id.button_menu);

            holder.linearLayoutAlt = (LinearLayout) convertView.findViewById(R.id.linear_layout_alt);
            holder.edit = (Button) convertView.findViewById(R.id.button_edit);
            holder.setDate = (Button) convertView.findViewById(R.id.button_calendar);
            holder.setList = (Button) convertView.findViewById(R.id.button_list);
            holder.back = (Button) convertView.findViewById(R.id.button_back);

            holder.linearLayoutEdit = (LinearLayout) convertView.findViewById(R.id.linear_layout_edit);
            holder.editText = (EditText) convertView.findViewById(R.id.edit_text_list_view);
            holder.backEdit = (Button) convertView.findViewById(R.id.button_back_edit);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        //set Text of the task
        holder.task.setText(entries.get(position).getTask());

        holder.checkBox.setChecked(false);

        holder.checkBox.setTag(position);

        //establish routine to remove task when checkbox is clicked
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer positionTask = (Integer)  holder.checkBox.getTag();
                data.remove(entries.get(positionTask).getID());//remove entry from dataset by ID
                notifyDataSetChanged();//update the adapter

            }
        });

        //setting "normal" row visible and active
        holder.linearLayout.setAlpha(1);
        holder.linearLayout.bringToFront();
        holder.linearLayoutAlt.setAlpha(0);
        holder.linearLayoutAlt.invalidate();
        holder.linearLayoutEdit.setAlpha(0);
        holder.linearLayoutEdit.invalidate();

        //listener that changes to alternative row layout on click
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setting alternative row visible and active
                holder.linearLayoutAlt.bringToFront();
                holder.linearLayoutAlt.setAlpha(1);
                holder.linearLayout.setAlpha(0);
                holder.linearLayout.invalidate();

                holder.back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyDataSetChanged();
                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.linearLayoutEdit.bringToFront();
                        holder.linearLayoutEdit.setAlpha(1);
                        holder.linearLayoutAlt.setAlpha(0);
                        holder.linearLayoutAlt.invalidate();

                        holder.editText.setText(holder.task.getText());

                        holder.backEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Integer positionTask = (Integer)  holder.checkBox.getTag();
                                Entry entry = entries.get(positionTask);
                                String list = entry.getList();
                                int due = entry.getDue();
                                String recurrence = entry.getRecurrence();
                                data.add(holder.editText.getText().toString(), list, due, recurrence,positionTask);
//                                data.add(positionTask.toString()," ", 0," ",entries.get(positionTask).getID());
                                data.remove(positionTask+1);
                                notifyDataSetChanged();
                            }
                        });


                        //
                    }
                });

                holder.setDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyDataSetChanged();
                    }
                });

                holder.setList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyDataSetChanged();
                    }
                });
                holder.back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyDataSetChanged();
                    }
                });
            }
        });

        return convertView;
    }

    //returns the entry at specified position
    public Entry getEntry(int position){
        return entries.get(position);
    }
}