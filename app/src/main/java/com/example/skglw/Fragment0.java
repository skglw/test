package com.example.skglw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Fragment0 extends Fragment {

    // newInstance constructor for creating fragment with arguments
    public static Fragment0 newInstance(int page, String title) {
        Fragment0 fragmentFirst = new Fragment0();
        Bundle args = new Bundle();
       // args.putInt("someInt", page);
       // args.putString("someTitle", title);
      //  fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
   //GridView gvMain;
  //  ArrayAdapter<String> adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment0, container, false);
    ArrayList<Branch> branches = new ArrayList<>();
       final StringBuilder strB = new StringBuilder();
        Button btnGet= view.findViewById(R.id.map);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getActivity(), MapActivity.class);
               // startActivity(intent);
            }
        });
        Thread request = new Thread(new Runnable() {
            //прописываю что ему делать
            public void run() {
                try {
                    URL url = new URL("http://mobile-api.fxnode.ru:18888/bankomats");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader input = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        String str;
                        while (null != (str = input.readLine())) {
                           strB.append(str);
                        }
                        input.close();
                    } else {
                        Log.e("HTTP-GET-ERROR", "response is not ok");
                    }
                    connection.disconnect();

                } catch (Exception e) {Log.e("HTTP-GET-ERROR", e.toString()); }
            }
        });

        request.start();	//Запуск потока
        try {
            request.join();
            String jsonString = String.valueOf(strB);
            Log.e("tt",jsonString);
            JSONArray arr = new JSONArray(jsonString);
            for (int i=0;i<arr.length();i++)
            {
                String obj = arr.get(i).toString();
                Log.e("OBJECT>> ", obj);
                JSONObject jsonO = new JSONObject(arr.get(i).toString());
                branches.add(new Branch(jsonO.get("type").toString() , jsonO.get("address").toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BranchesAdapter adapter = new BranchesAdapter(this.getContext(), branches);
        ListView lv =  view.findViewById(R.id.lv);
        lv.setAdapter(adapter);
        lv.setDividerHeight(15);

//        adapter = new ArrayAdapter<String>(getActivity(), R.layout.item, R.id.tv1, addresses);
//        gvMain = (GridView) view.findViewById(R.id.gv);
//        gvMain.setAdapter(adapter);
//        gvMain.setNumColumns(3);
//        gvMain.setNumColumns(GridView.AUTO_FIT);
//        gvMain.setColumnWidth(180);
//        gvMain.setVerticalSpacing(25);
//        gvMain.setHorizontalSpacing(25);
        return view;
    }

    public class Branch {

        private String type, address;
        public Branch( String type, String address) {
            this.type = type;
            this.address =address;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
    }


    public class BranchesAdapter extends ArrayAdapter<Branch> {

        private Context context;
        private List<Branch> branches = new ArrayList<>();

        public BranchesAdapter(@NonNull Context context, ArrayList<Branch> list) {
            super(context, 0 , list);
            this.context = context;
            branches= list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View item = convertView;
            if(item == null)
                item = LayoutInflater.from(context).inflate(R.layout.item,parent,false);

            Branch currentBr = branches.get(position);

            TextView name =  item.findViewById(R.id.tv1);
            name.setText(currentBr.getType());

            TextView release =  item.findViewById(R.id.tv2);
            release.setText(currentBr.getAddress());

            return item;
        }
    }
}
