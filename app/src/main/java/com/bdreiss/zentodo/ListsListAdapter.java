package com.bdreiss.zentodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bdreiss.zentodo.dataManipulation.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ListsListAdapter extends ArrayAdapter<String> {

    private Context context;
    private ListView listView;
    private ArrayList<String> lists;
    private Data data;

    private class ViewHolder{
        private Button button;
        private ListView listView;
    }
    public ListsListAdapter(Context context, ListView listview, Data data, ArrayList<String> lists){
        super(context,R.layout.lists_row,lists);
        this.listView = listview;
        this.context=context;
        this.data = data;
        this.lists = lists;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if(convertView==null){
            holder = new ListsListAdapter.ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lists_row, null, true);

            holder.button = (Button) convertView.findViewById(R.id.button_lists_row);
            holder.listView = (ListView) convertView.findViewById(R.id.list_view_lists);

            convertView.setTag(holder);

        }else{
            holder = (ListsListAdapter.ViewHolder)convertView.getTag();
        }

        holder.button.setText(lists.get(position));

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.button.getText().equals(context.getResources().getString(R.string.allTasks))){

                    TaskListAdapter adapter =new TaskListAdapter(context,data, data.getEntries()," ");
                    listView.setAdapter(adapter);

                } else{
                    TaskListAdapter adapter = new TaskListAdapter(context, data, data.getFromList(holder.button.getText().toString())," ");
                    listView.setAdapter(adapter);
                }
            }
        });

        return convertView;
    }

    }
