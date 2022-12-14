package com.bdreiss.zentodo.adapters.listeners;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.DatePicker;

import com.bdreiss.zentodo.R;
import com.bdreiss.zentodo.adapters.TaskListAdapter;
import com.bdreiss.zentodo.dataManipulation.Entry;

import java.util.Calendar;

/*
 *
 *  Implements listener for the setting the reminder date of a task.
 *
 *  Gets current reminder date from task and shows datePickerDialog.
 *
 */


public class SetDateListener extends BasicListener implements View.OnClickListener {

    public SetDateListener(TaskListAdapter adapter, TaskListAdapter.ViewHolder holder, int position){
        super(adapter, holder, position);
    }

    @Override
    public void onClick(View v){

        //Get entry
        Entry entry = adapter.entries.get(position);

        //get current date when task is due
        int entryDate = entry.getReminderDate();

        //variables for setting picker
        int entryDay;
        int entryMonth;
        int entryYear;

        //if current date greater then 0 set value, otherwise set today
        if(entryDate>0) {

            //resolve format "yyyymmdd"
            entryDay = entryDate%100;
            entryMonth = ((entryDate%10000)-entryDay)/100-1;
            entryYear = (entryDate-entryMonth*100-entryDay)/10000;
        }
        else {

            //get todays date
            Calendar c = Calendar.getInstance();
            entryYear = c.get(Calendar.YEAR);
            entryMonth = c.get(Calendar.MONTH);
            entryDay = c.get(Calendar.DAY_OF_MONTH);
        }

        //create DatePickerDialog and set listener
        DatePickerDialog datePickerDialog = getDatePickerDialog(entry, entryDay,entryMonth,entryYear,holder,position);

        //show the dialog
        datePickerDialog.show();

    }

    //return DatePickerDialog
    public DatePickerDialog getDatePickerDialog(Entry entry, int entryDay, int entryMonth, int entryYear, TaskListAdapter.ViewHolder holder, int position){

        //DatePickerDialog to be returned
        DatePickerDialog datePickerDialog;

        //initialize DatePickerDialog
        datePickerDialog= new DatePickerDialog(adapter.context, (view, year, month, day) -> {

            //Encode format "YYYYMMDD"
            int date = year*10000+(month+1)*100+day;

            //Write back data
            adapter.data.editReminderDate(entry.getId(), date);
            adapter.entries.get(position).setReminderDate(date);

            //change color of reminder date Button marking if Date is set
            adapter.markSet(holder,entry);

            //notify adapter
            adapter.notifyItemChanged(position);

            //return to original row layout
            adapter.setOriginal(holder);

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
        });

        return datePickerDialog;
    }

}


