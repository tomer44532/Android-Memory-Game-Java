package com.example.memorygame.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.memorygame.interfaces.HandleGameCards;
import com.example.memorygame.model.Inventory;
import com.example.memorygame.model.InventorySlot;
import com.example.memorygame.interfaces.NotGameInventory;
import com.example.memorygame.R;
import com.google.gson.Gson;

import java.util.List;

public class InventoryAdapter extends BaseAdapter {
    private Context context;
    private SharedPreferences sp;
    private List<InventorySlot> slots;
    private Inventory inventory;

    public InventoryAdapter( Inventory inventory,Context context) {
        this.context = context;
        this.slots = inventory.getUseables();


        sp = context.getSharedPreferences("info",Context.MODE_PRIVATE);

        this.inventory = inventory;
    }

    @Override
    public int getCount() {
        int sum = 0;
        for (InventorySlot slot: slots)
        {
            //sum = sum +slot.getItemCount();
            if(slot.getItemCount()!=0)
            {
                sum++;
            }
        }

        return sum;
    }

    @Override
    public Object getItem(int position) {
        return slots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if(view ==null)
        {
            LayoutInflater layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.inventory_slot,parent,false);
        }
        final InventorySlot slot = slots.get(position);
        final ImageView slotImg =view.findViewById(R.id.buyable_iv);
        final TextView slotTv = view.findViewById(R.id.name_tv);
        final Button slotBtn = view.findViewById(R.id.use_btn);
        final TextView slotAmount = view.findViewById(R.id.amount_tv);
        slotAmount.setTag(slot.getItemCount());
        if(slot.getItemCount()>0)
        {
            slotTv.setText(slot.getBuyable().getName());



        }
        slotImg.setImageResource(slot.getBuyable().getPicResId());
        slotAmount.setText("X"+ slot.getItemCount());
        slotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useItem(slot,position,slotAmount,slot.getItemCount());
            }
        });
        if(slot.getItemCount() <= 0) {
            slots.remove(position);
            notifyDataSetChanged();
            inventory.getUseables().remove(slot);
        }

        ImageButton infoBtn = view.findViewById(R.id.info_btn);
        infoBtn.setTag(slot.getBuyable().getId());
        infoBtn.setOnClickListener(new OnInfoClickListener());

        return view;
    }

    private void useItem(InventorySlot slot,int position,TextView itemAmount,int amount)
    {
        if(slot.getBuyable().getId()==4&& context instanceof HandleGameCards)//skip stage
        {
            ((HandleGameCards)context).victoryManager(0);
            ((HandleGameCards)context).closeInventory();
            //update inventory
            slot.setItemCount(slot.getItemCount()-1);


            Gson gson = new Gson();
            String json = gson.toJson(inventory);
            sp.edit().remove("bought items").commit();
            sp.edit().putString("bought items",json).commit();
        }
        else if(slot.getBuyable().getId()==4&& !(context instanceof HandleGameCards))
        {
            Toast.makeText(context, context.getResources().getString(R.string.use_during_game), Toast.LENGTH_SHORT).show();
        }
        else if(slot.getBuyable().getId()==3&& context instanceof HandleGameCards)
        {
            ((HandleGameCards)context).cancelShuffle();
            ((HandleGameCards)context).closeInventory();
            //update inventory
            slot.setItemCount(slot.getItemCount()-1);

            Gson gson = new Gson();
            String json = gson.toJson(inventory);
            sp.edit().remove("bought items").commit();
            sp.edit().putString("bought items",json).commit();
        }
        else if(slot.getBuyable().getId()==3&& !(context instanceof HandleGameCards))
        {
            Toast.makeText(context, context.getResources().getString(R.string.use_during_game), Toast.LENGTH_SHORT).show();
        }
        else if(slot.getBuyable().getId()==1&& (context instanceof NotGameInventory))
        {
            ((NotGameInventory)context).doubleCoins();
            //update inventory
            slot.setItemCount(slot.getItemCount()-1);
            Gson gson = new Gson();
            String json = gson.toJson(inventory);
            sp.edit().remove("bought items").commit();
            sp.edit().putString("bought items",json).commit();


            if((slot.getItemCount())==0)
            {
                slots.remove(position);
                notifyDataSetChanged();
            }
            else
            {

                //amount--;
                itemAmount.setText("X"+(slot.getItemCount()));
            }
        }
        else if(slot.getBuyable().getId()==1&&context instanceof HandleGameCards)
        {
            Toast.makeText(context, context.getResources().getString(R.string.cant_use_in_game_str), Toast.LENGTH_SHORT).show();
        }
        else if(slot.getBuyable().getId()==0)
        {
            Toast.makeText(context, context.getResources().getString(R.string.use_after_lose), Toast.LENGTH_SHORT).show();
        }
    }

    private class OnInfoClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View alertView = inflater.inflate(R.layout.item_info_dialog, null);
            alertDialog.setView(alertView);
            alertDialog.setCancelable(true);



            TextView nameTxt = alertView.findViewById(R.id.name_tv);
            TextView descTxt = alertView.findViewById(R.id.desc_tv);

            switch (Integer.parseInt(v.getTag().toString()))
            {
                case 0:
                {
                    nameTxt.setText(R.string.extra_life);
                    descTxt.setText(R.string.desc_extra_life);
                    break;
                }
                case 1:
                {
                    nameTxt.setText(R.string.double_coins);
                    descTxt.setText(R.string.desc_double_coins);
                    break;
                }
                case 3:
                {
                    nameTxt.setText(R.string.cancel_shuffle);
                    descTxt.setText(R.string.desc_cancel_shuffle);
                    break;
                }
                case 4:
                {
                    nameTxt.setText(R.string.skip_stage);
                    descTxt.setText(R.string.desc_skip_stage);
                    break;
                }
            }


            AlertDialog show = alertDialog.show();
            show.getWindow().getDecorView().setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
    }
}
