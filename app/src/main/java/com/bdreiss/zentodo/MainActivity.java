package com.bdreiss.zentodo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import com.bdreiss.zentodo.dataManipulation.Data;
import com.bdreiss.zentodo.dataManipulation.SQLite;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bdreiss.zentodo.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

  //      setSupportActionBar(binding.toolbar);

  //      NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
  //      appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
  //      NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    /*    binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

  //      TextView textView = (TextView) findViewById(R.id.text_view_test);

//        Data data = new Data(textView);

        //addData(data);

        setViews(this);

    }

    public void setViews(Context context){
        //TODO when adding tasks empty lines apparently get added too
        TextView textView = (TextView) findViewById(R.id.text_view_test);
        TextView textView2 = (TextView) findViewById(R.id.text_view_test2);
        Data data = new Data(context, textView);//REMOVE TEXTVIEW!!!
        //addData(data);
        //textView.setText(String.valueOf(data.load()));
        //textView2.setText(data.saveFile.load());
        //textView.setText(String.valueOf(data.getEntries().size()));
        final List<String> items = data.getEntriesAsString();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        ListView listView = (ListView) findViewById(R.id.list_view_test);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                //textView.setText(items.get(i));
                int itemId = data.getIdByTask(items.get(i));

                if (id >= 0) {
                    data.remove(itemId);//TODO
                    items.remove(i);//TODO
                    adapter.notifyDataSetChanged();
                }
                setViews(context);

            }
        });
        final EditText editText = (EditText) findViewById(R.id.edit_text_test);
        Button button = (Button) findViewById(R.id.button_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = editText.getText().toString();
                editText.setText("");
                if (!task.equals("")) {
                    data.add(task, " ", data.getDate(), " ");
                    items.add(task);
                    adapter.notifyDataSetChanged();
                }
                setViews(context);

            }
        });

    }

    public void setListView(Context context){



    }
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    public static void addData(Data data){

        data.add("Waschen","Haushalt",20220908, "");

        data.add("Buegeln","Haushalt",20220909,"");

        data.add("Gleichungen ueben","Mathe",20220910,"");

        data.add("Computer einschalten","Computersysteme",20210910,"");

    }

}