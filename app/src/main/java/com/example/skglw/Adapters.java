package com.example.skglw;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Adapters {


 /*   public static class CardAdapter extends ArrayAdapter<User.Check> {
        private Activity context;
        ArrayList<User.Check> data = null;

        public CardAdapter(Activity context, int resource, ArrayList<User.Check> data) {
            super(context, resource, data);
            this.context = context;
            this.data = data;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) { // этот код выполняется, когда вы нажимаете на спиннер
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.spinner_item, parent, false);
            }

            User.Check item = data.get(position);

            if (item != null) { // парсим данные с каждого объекта
                ImageView myFlag = (ImageView) row.findViewById(R.id.imageIcon);
                TextView myCountry = (TextView) row.findViewById(R.id.countryName);
                if (myFlag != null) {
                    myFlag.setBackground(context.getResources().getDrawable(item.getImage(), context.getTheme()));
                }
                if (myCountry != null)
                    myCountry.setText(item.getNumber());
            }

            return row;
        }
    }
*/
}
