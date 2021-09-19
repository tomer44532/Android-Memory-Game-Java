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

import com.example.memorygame.model.Buyable;
import com.example.memorygame.interfaces.HaveShop;
import com.example.memorygame.model.Inventory;
import com.example.memorygame.model.InventorySlot;
import com.example.memorygame.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class BuyableAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private SharedPreferences sp;
    private List<Buyable> buyables;
    private ArrayList<Buyable> ownedBuyables;
    private Inventory inventory;

    public BuyableAdapter(List<Buyable> buyables, Context context) {
        this.buyables = buyables;
        this.context = context;
        sp = context.getSharedPreferences("info",Context.MODE_PRIVATE);
        Gson gson = new Gson();



        String inventoryResponse = sp.getString("bought items","");

        if(inventoryResponse != "")
        {

            inventory = gson.fromJson(inventoryResponse,new TypeToken<Inventory>(){}.getType());

        }
        else
        {
            inventory = new Inventory(context);

        }

    }

    @Override
    public int getCount() {
        return buyables.size();
    }

    @Override
    public Object getItem(int position) {
        return buyables.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view ==null)
        {
            LayoutInflater layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view = layoutInflater.inflate(R.layout.shop_cell,parent,false);

        }
        Buyable buyable = buyables.get(position);
        ImageView buyableIv =view.findViewById(R.id.buyable_iv);
        TextView buyableTv = view.findViewById(R.id.name_tv);
        Button buyableBtn = view.findViewById(R.id.buy_btn);

        buyableTv.setText(buyable.getName());

        buyableBtn.setTag(position);
        buyableBtn.setOnClickListener(this);

        buyableIv.setImageResource(buyable.getPicResId());
        TextView priceTv = view.findViewById(R.id.price_tv);
        priceTv.setText(context.getString(R.string.price_str) +": " + buyable.getPrice());

        TextView amountTv = view.findViewById(R.id.amount_tv);
        if(buyable.getAmount()>1)
        {
            amountTv.setText("X" + buyable.getAmount());
        }
        else
        {
            amountTv.setText("X"+buyable.getAmount());
        }



        ImageButton infoBtn = view.findViewById(R.id.info_btn);
        infoBtn.setTag(buyable.getId());
        infoBtn.setOnClickListener(new OnInfoClickListener());

        return view;
    }

    @Override
    public void onClick(View v) {
        int money = sp.getInt("money",0);

        int position =(Integer)((Button)v).getTag();
        Buyable buyable = buyables.get(position);

        if(money >= buyable.getPrice())
        {
            money = money - buyable.getPrice();
            sp.edit().putInt("money",money).commit();
            ((HaveShop)context).updateMoney(money);
            if(buyable.getType()==0)//useable
            {
                boolean foundSlot = false;
                for (InventorySlot slot:inventory.getUseables())
                {

                    if(slot.getBuyable() !=null &&slot.getBuyable().getId() ==buyable.getId())
                    {

                        int index = inventory.getUseables().indexOf(slot);
                        inventory.getUseables().get(index).setItemCount(slot.getItemCount()+buyable.getAmount());
                        Gson gson = new Gson();

                        String json = gson.toJson(inventory);
                        sp.edit().remove("bought items").commit();
                        sp.edit().putString("bought items",json).commit();
                        foundSlot = true;

                    }

                }
                if(!foundSlot)
                {


                    for (InventorySlot slot:inventory.getUseables())
                    {

                        if(slot.getItemCount()==0)
                        {
                            slot.setItemCount(slot.getItemCount()+buyable.getAmount());
                            slot.setBuyable(buyable);
                            Gson gson = new Gson();

                            Gson gson2 =  new Gson();
                            ArrayList<InventorySlot> inventorySlots = inventory.getUseables();
                            String json = gson2.toJson(inventory);
                            sp.edit().remove("bought items").commit();
                            sp.edit().putString("bought items",json).commit();


                            foundSlot = true;
                            break;
                        }
                    }
                    if(!foundSlot)
                    {
                        Toast.makeText(context, context.getResources().getString(R.string.no_space_inventory_str), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
        else
        {
            String string;
            Toast.makeText(context, context.getString(R.string.not_enough_money)+" "+ buyable.getName() , Toast.LENGTH_SHORT).show();
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
