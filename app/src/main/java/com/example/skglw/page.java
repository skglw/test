package com.example.skglw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class page extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber; View view;
    static page newInstance(int page) {
        page pageFragment = new page();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        switch (pageNumber){
            case 0: view = inflater.inflate(R.layout.fragment0, null);
                break;
            case 1: view = inflater.inflate(R.layout.fragment1, null);
                break;
            case 2: view = inflater.inflate(R.layout.fragment2, null);
                break;
        }



//
//        TextView tvPage = view.findViewById(R.id.tvPage);
//        tvPage.setText("Page " + pageNumber);
//        TextView tvPage2 = view.findViewById(R.id.tvPage2);
//        tvPage2.setText("Page " + pageNumber);
        return view;
    }
}