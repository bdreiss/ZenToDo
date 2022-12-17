package com.bdreiss.zentodo.adapters.listeners;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;

import com.bdreiss.zentodo.R;
import com.bdreiss.zentodo.adapters.PickTaskListAdapter;
import com.bdreiss.zentodo.adapters.TaskListAdapter;
import com.bdreiss.zentodo.dataManipulation.Entry;

public class SetDateListenerPick extends SetDateListener{

    private PickTaskListAdapter pickAdapter = PickTaskListAdapter.getPickAdapter();
    private PickTaskListAdapter doNowAdapter = PickTaskListAdapter.getDoNowAdapter();
    private PickTaskListAdapter doLaterAdapter = PickTaskListAdapter.getDoLaterAdapter();
    private PickTaskListAdapter moveToListAdapter = PickTaskListAdapter.getMoveToListAdapter();

    public SetDateListenerPick(TaskListAdapter adapter, TaskListAdapter.ViewHolder holder, int position){
        super(adapter, holder, position);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public DatePickerDialog getDatePickerDialog(Entry entry, int entryDay, int entryMonth, int entryYear, TaskListAdapter.ViewHolder holder, int position){

        //DatePickerDialog to be returned
        DatePickerDialog datePickerDialog;

        //initialize DatePickerDialog
        datePickerDialog= new DatePickerDialog(adapter.context, (view, year, month, day) -> {

            //Encode format "YYYYMMDD"
            int date = year*10000+(month+1)*100+day;

            Entry e = adapter.entries.get(position);
            //Write back data
            adapter.data.editReminderDate(entry.getId(), date);
            e.setReminderDate(date);

            doLaterAdapter.entries.add(e);
            doLaterAdapter.notifyDataSetChanged();
            adapter.entries.remove(e);
            adapter.notifyDataSetChanged();

        }, entryYear, entryMonth, entryDay);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,adapter.context.getResources().getString(R.string.cancel), (dialog, which) -> {

            //set date when task is due to 0
            adapter.data.editReminderDate(entry.getId(),0);
            adapter.entries.get(position).setReminderDate(0);

            //change color of reminder date Button marking if Date is set
            adapter.markSet(holder,entry);

            //notify adapter
            adapter.notifyItemChanged(position);

            //return to original layout
            adapter.setOriginal(holder);

            Entry e = adapter.entries.get(position);

            if (e.getList() != null){
                moveToListAdapter.entries.add(e);
                moveToListAdapter.notifyDataSetChanged();

                adapter.entries.remove(e);
                adapter.notifyDataSetChanged();
            }
            else {
                if (doLaterAdapter.entries.contains(e)) {
                    doLaterAdapter.entries.remove(e);
                    doLaterAdapter.notifyDataSetChanged();
                    pickAdapter.entries.add(e);
                    pickAdapter.notifyDataSetChanged();
                }
            }
        });

        return datePickerDialog;
    }

}