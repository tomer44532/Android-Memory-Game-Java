package com.example.memorygame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.example.memorygame.adapters.InventoryAdapter;
import com.example.memorygame.model.Inventory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InventoryDialog extends DialogFragment {
    private SharedPreferences sp;

    private String response;
    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inventory_dialog, container,
                false);
        getDialog().setTitle("Inventory");


        ListView listView = rootView.findViewById(R.id.inventory_list);




        Bundle bundle = getArguments();
        String response = "";
        if(bundle !=null)
        {
            response = bundle.getString("inventory","");

            if(response !="")
            {

                Inventory inventory = gson.fromJson(response,
                        new TypeToken<Inventory>(){}.getType());
                ListAdapter inventoryAdapter = new InventoryAdapter(inventory,getActivity());
                if(listView ==null)
                {

                }
                listView.setAdapter(inventoryAdapter);

            }
        }




        // Do something else
        return rootView;
    }
}
