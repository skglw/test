package com.example.skglw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

import static com.example.skglw.User.payList;

public class Fragment0 extends Fragment {


    public static Fragment0 newInstance() {
        Fragment0 fragmentFirst = new Fragment0();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment0, container, false);
        Intent intent = getActivity().getIntent();
        JSONObject json = new JSONObject();
        try {
            json.put("token", intent.getStringExtra("token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestCallable callable= new RequestCallable("POST", "http://mobile-api.fxnode.ru:18888/category", json);
        FutureTask task = new FutureTask(callable);
        Thread request = new Thread(task);
        request.start();
        try {
            request.join();
            String jsonString = String.valueOf(task.get());
            JSONArray arr = new JSONArray(jsonString);
            for (int i=0;i<arr.length();i++)
            {
                JSONObject obj= new JSONObject(arr.get(i).toString());
                payList.add(new User.Pay(obj.get("name").toString() , obj.get("check").toString()));
            }


            //JSONObject obj = new JSONObject(jsonString);
          //  String name = " " + obj.get("name").toString();
            //String check = obj.get("check").toString();

          //tvUsername.setText(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PaysAdapter adapter = new PaysAdapter(this.getContext(), payList);
        ListView lv =  view.findViewById(R.id.lv);
        lv.setAdapter(adapter);
        lv.setDividerHeight(15);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.tv1)).getText().toString();
                Intent intent = new Intent(getActivity(), RefillActivity.class);
                intent.putExtra("action","payTo");
                intent.putExtra("name",((TextView) view.findViewById(R.id.tv1)).getText().toString());
                intent.putExtra("check",((TextView) view.findViewById(R.id.tv2)).getText().toString());
                startActivity(intent);
            }
        });

        Button refillBtn = view.findViewById(R.id.btnRefill);
            refillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RefillActivity.class);
                    intent.putExtra("action","refill");
                    startActivity(intent);
                }
            });

        Button payBtn = view.findViewById(R.id.btnPay);
           payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PayActivity.class);
                    intent.putExtra("action","pay");
                    startActivity(intent);
                }
            });


        return view;

    }



    public class PaysAdapter extends ArrayAdapter<User.Pay> {

        private Context context;
        private List<User.Pay> pays = new ArrayList<>();

        public PaysAdapter(@NonNull Context context, ArrayList<User.Pay> list) {
            super(context, 0 , list);
            this.context = context;
            pays= list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View item = convertView;
            if(item == null)
                item = LayoutInflater.from(context).inflate(R.layout.item,parent,false);

            User.Pay currentBr = pays.get(position);

            TextView name =  item.findViewById(R.id.tv1);
            name.setText(currentBr.getName());

            TextView release =  item.findViewById(R.id.tv2);
            release.setText(currentBr.getCheck());

            return item;
        }
    }
}
