package com.example.skglw;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment {

    // newInstance constructor for creating fragment with arguments
    public static Fragment2 newInstance(int page, String title) {
        Fragment2 fragmentFirst = new Fragment2();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
//        page = getArguments().getInt("someInt", 0);
//        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.tv);
        Intent intent = getActivity().getIntent();
        String login = intent.getStringExtra("login");
        tvLabel.setText(login);
        return view;
//
//        ViewGroup rootView = (ViewGroup) inflater.inflate(
//                R.layout.fragment0, container, false);

    //   return rootView;
    }
}
