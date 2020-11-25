package com.example.skglw;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.FutureTask;

import static com.example.skglw.User.checkList;

public class Fragment1 extends Fragment {

View view; SwipeRefreshLayout swipeLayout;LinearLayout cardsLayout;
    public static Fragment1 newInstance() {
        Fragment1 fragmentFirst = new Fragment1();


        //Toast.makeText(getContext(),"jgbekjgbekrjgv",Toast.LENGTH_SHORT);



        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView(cardsLayout,view);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment1, container, false);
        //TextView tvUsername = view.findViewById(R.id.tvUsername);
      //  Intent intent = getActivity().getIntent();
      //  tvUsername.setText(intent.getStringExtra("name"));
        cardsLayout = view.findViewById(R.id.cardsLayout);
        updateView(cardsLayout,view);
        Button mapBtn = view.findViewById(R.id.btnMap);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });
        swipeLayout = view.findViewById(R.id.llSwipe);
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.e("LOG_TAG", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        updateView(cardsLayout,view);
                    }
                }
        );
        return view;
    }

    private void updateView(LinearLayout cardsLayout,View view) {

        User.checkList.clear();
        JSONObject json = new JSONObject();
        try {
            json.put("token", User.token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestCallable callable = new RequestCallable("POST", "http://mobile-api.fxnode.ru:18888/getcheck", json);
        FutureTask task = new FutureTask(callable);
        Thread request = new Thread(task);
        request.start();
        try {
            request.join();
            String jsonString = String.valueOf(task.get());
            JSONArray arr = new JSONArray(jsonString);
            for (int i = 0; i < arr.length(); i++) {
                User.Check check = new User.Check();
                JSONObject obj = new JSONObject(arr.get(i).toString());
                check.balance = obj.get("balance").toString();
                check.number = obj.get("check_number").toString();

                checkList.add(check);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        callable = new RequestCallable("POST", "http://mobile-api.fxnode.ru:18888/getcards", json);
        task = new FutureTask(callable);
        request = new Thread(task);
        request.start();
        try {
            request.join();
            String jsonString = String.valueOf(task.get());
            JSONArray arr = new JSONArray(jsonString);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = new JSONObject(arr.get(i).toString());
                String number = obj.get("check_number").toString(), card = obj.get("card_number").toString();
                if (number.equals(checkList.get(i).number)) {
                    checkList.get(i).card = card;
                    if(card.substring(0, 1).equals("4"))checkList.get(i).image=R.drawable.ic_visa;
                    else checkList.get(i).image=R.drawable.ic_mastercard;

                    if (obj.get("block").toString().equals("true")){
                        checkList.get(i).blocked=true;
                    }else checkList.get(i).blocked=false;
                }

            }
            swipeLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardsLayout.removeAllViews();
        for (final User.Check check : checkList) {
            LayoutInflater inf = getLayoutInflater();
            final View card = inf.inflate(R.layout.card, null);
            TextView tvCardNumber = card.findViewById(R.id.tvCardNumber);
            TextView tvCheckNumber = card.findViewById(R.id.tvCheckNumber);
            TextView tvBalance = card.findViewById(R.id.tvBalance);
            ImageView img = card.findViewById(R.id.ivCard);

            final SwitchMaterial sw = card.findViewById(R.id.sw);
            if(check.blocked){
                sw.setChecked(false);
                sw.setEnabled(false);
                sw.setText("Карта заблокирована");
            }

            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked){
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
                    dialog.setTitle("Блокировка карты");
                    dialog.setMessage("Вы уверены, что хотите заблокировать карту?");
                    dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sw.setChecked(true);
                        }
                    });

                    dialog.setPositiveButton("Заблокировать карту", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject json = new JSONObject();
                            try {
                                json.put("token", User.token);
                                json.put("card_number", check.card);
                                RequestCallable callable = new RequestCallable("POST", "http://mobile-api.fxnode.ru:18888/block", json, getActivity());
                                FutureTask task = new FutureTask(callable);
                                Thread request = new Thread(task);
                                request.start();
                                request.join();
                                }catch(Exception e){}
                            sw.setChecked(false);
                            sw.setEnabled(false);
                            sw.setText("Карта заблокирована");
                            card.setEnabled(false);

                        }
                    });
                    dialog.show();
                }
                }
            });
            tvCheckNumber.setText(check.number);
            tvBalance.setText(check.balance + " руб.");
            tvCardNumber.setText(check.card);
            img.setImageResource(check.image);
            cardsLayout = view.findViewById(R.id.cardsLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 20);
            cardsLayout.addView(card, layoutParams);
           if (sw.isChecked()){
               card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
           }
        }
    }


    private void showPopupMenu(View v) {

        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.card_popup_menu);
        TextView tvCard = v.findViewById(R.id.tvCardNumber);
        final String card = tvCard.getText().toString();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), RefillActivity.class);
                switch (item.getItemId()) {
                    case R.id.miPay:
                        intent.putExtra("card", card);
                        intent.putExtra("action", "refillFrom");
                        getActivity().startActivity(intent);
                        return true;
                    case R.id.miRfill:
                        intent.putExtra("card", card);
                        intent.putExtra("action", "refillTo");
                        getActivity().startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }
}

/*

    EditText etText;
    Button btnSave, btnLoad, btnSend,btnPost;
    SharedPreferences sPref;
    final String SAVED_TEXT = "saved_text";
 etText = view.findViewById(R.id.text);
        btnSave = view.findViewById(R.id.save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPref = getActivity().getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(SAVED_TEXT, etText.getText().toString());
                ed.commit();

            }
        });
        btnLoad = view.findViewById(R.id.load);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPref = getActivity().getPreferences(MODE_PRIVATE);
                String savedText = sPref.getString(SAVED_TEXT, "");
                etText.setText(savedText);
            }
        });
        btnSend= view.findViewById(R.id.send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //создаю новый поток для запроса
                Thread request = new Thread(new Runnable()
                {
                    //прописываю что ему делать
                    public void run()
                    {
                        try{
                            //Log.e("HTTP-get", "try1");
                            URL url = new URL("http://mobile-api.fxnode.ru:18888/bankomats");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            //Log.e("HTTP-get", "try2");
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                //Если запрос выполнен удачно, читаем полученные данные и далее, делаем что-то
                                // Log.e("HTTP-resp", "ok");
                               BufferedReader input = new BufferedReader(
                                        new InputStreamReader(connection.getInputStream(), "UTF-8"));
                                //Log.e("HTTP-GET", "try3");
                                StringBuilder strB = new StringBuilder();
                                String str;
                                while (null != (str = input.readLine())) {
                                   // Log.e("!!","reading");
                                    strB.append(str).append("\r\n");
                                }
                                input.close();
                                Log.e("RESPONSE", String.valueOf(strB));
                                //} catch (IOException e) { }
                            }
                            else {
                               // Log.e("resp", "response not ok");
                            }


                            connection.disconnect();
                        } catch (Exception e) {}//Log.e("HTTP-GET", e.toString());  }
                    }
                });
                request.start();	//Запуск потока
                try {
                    request.join();
                    //Log.e("!!","Главный поток завершён...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        btnPost= view.findViewById(R.id.post);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpThread post = new  httpThread();
                Thread myThready = new Thread(post);
               // myThready.start();
            }
        });
 */