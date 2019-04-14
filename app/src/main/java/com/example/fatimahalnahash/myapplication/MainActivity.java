package com.example.fatimahalnahash.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> allergyStringList = new ArrayList<>();
    ArrayList<Allergy> allergyList = new ArrayList<>();
    Button addButton, scanButton;
    ArrayAdapter<Allergy> adapter;

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      allergyStringList.add("Coconuts");
        allergyStringList.add("Peanuts");
        allergyStringList.add("Tomatoes");
        allergyStringList.add("Corn");
        //-----------------------------------
        allergyList.add(new Allergy("Coconuts"));
        allergyList.add(new Allergy("Peanuts"));
        allergyList.add(new Allergy("Tomatoes"));
        allergyList.add(new Allergy("Corn"));

        ListView listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<Allergy>(this, android.R.layout.simple_list_item_1, android.R.id.text1, allergyList);

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Allergy a = adapter.getItem(position);
                adapter.remove(a);
                allergyList.remove(a);
                allergyStringList.remove(a.toString());
                adapter.notifyDataSetChanged();

                return false;
            }
        });
        addButton = findViewById(R.id.buttonAdd);
        scanButton = findViewById(R.id.buttonScan);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.editTextAllergies);
                if( allergyStringList.contains( editText.getText().toString() ) ){
                    Toast.makeText(getApplicationContext(),"This Allergy is already in the list",Toast.LENGTH_LONG).show();
                }else{
                    allergyStringList.add(editText.getText().toString());
                    adapter.add(new Allergy(editText.getText().toString()));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                intent.putExtra("allergyList", allergyStringList);
                startActivity(intent);

            }
        });
    }

    static class Allergy implements Serializable {
        String name;
        public Allergy(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name ;
        }
    }
}
