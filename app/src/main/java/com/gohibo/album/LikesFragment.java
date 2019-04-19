package com.gohibo.album;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
/**
 * A simple {@link Fragment} subclass.
 */
public class LikesFragment extends Fragment {

    public static LikesFragment newInstance() {
        LikesFragment fragment = new LikesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarLikesAct);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return view;
    }
}