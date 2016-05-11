package com.example.mahes_000.sunshine_app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        Intent intent = getActivity().getIntent();

        String Message = intent.getStringExtra(Intent.EXTRA_TEXT);

        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        ((TextView) rootView.findViewById(R.id.text_detail)).setText(Message);

        return rootView;
    }
}
