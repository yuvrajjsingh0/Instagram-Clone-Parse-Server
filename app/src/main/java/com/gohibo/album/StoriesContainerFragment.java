package com.gohibo.album;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gohibo.album.R;
import com.gohibo.album.helper.StoriesAdapter;
import com.gohibo.album.helper.UserStoriesHelper;

import java.net.URI;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoriesContainerFragment extends Fragment {

    ArrayList<UserStoriesHelper> listitems = new ArrayList<>();
    RecyclerView myRecyclerView;
    String users[] = {"Yuvraj", "Jaya", "Deeksha", "Shashi"};
    URI displayPictures[] = {URI.create("https://scontent.xx.fbcdn.net/v/t1.0-1/p200x200/24176763_1324689471010957_7293825096713744274_n.jpg?oh=e525160638a112c42931788121f51a8b&oe=5ADBACD8"),
            URI.create("https://scontent.xx.fbcdn.net/v/t1.0-1/p200x200/26168837_1990294294625261_1252342541684575856_n.jpg?oh=f822300e14824062927813eb8cb1a7dd&oe=5AE333E3"),
            URI.create("https://scontent.xx.fbcdn.net/v/t1.0-1/c0.0.200.200/p200x200/26196436_522199301484304_968626131853915437_n.jpg?oh=37b5f8a1c017ec1624ca37c538409ec6&oe=5AEEBEB4"),
            URI.create("https://scontent.xx.fbcdn.net/v/t1.0-1/p200x200/17190698_105202006680487_357623708231224066_n.jpg?oh=7de3df30a67398aacddf53bd72f840f4&oe=5B267CA2")
    };

    public static StoriesContainerFragment newInstance() {
        StoriesContainerFragment fragment = new StoriesContainerFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        listitems.clear();
        for(int i =0;i<users.length;i++) {
            UserStoriesHelper item = new UserStoriesHelper();
            item.setCardName(users[i]);
            item.setImageResourceId(displayPictures[i]);
            item.setIsturned(0);
            listitems.add(item);
        }

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stories_container, container, false);
            RecyclerView MyRecyclerView = view.findViewById(R.id.cardView);
            MyRecyclerView.setHasFixedSize(true);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            MyRecyclerView.setAdapter(new StoriesAdapter(listitems, getContext()));

            MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

}
