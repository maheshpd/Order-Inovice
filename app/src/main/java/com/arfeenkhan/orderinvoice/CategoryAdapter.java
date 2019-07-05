package com.arfeenkhan.orderinvoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<ProductModel> {

    public CategoryAdapter(Context context, ArrayList<ProductModel> countryList) {
        super(context, 0, countryList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.country_spinner_row, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text_view_name);

        ProductModel currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getName());
        }

        return convertView;
    }
}
