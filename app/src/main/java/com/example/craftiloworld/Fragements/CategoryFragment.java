package com.example.craftiloworld.Fragements;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.craftiloworld.R;

public class CategoryFragment extends Fragment {

    private ListView listView;
    private String[] listItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        listView = view.findViewById(R.id.list_view);
        listItem = getResources().getStringArray(R.array.spinner_items);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value = adapter.getItem(position);

                getContext().getSharedPreferences("CATEGORY", Context.MODE_PRIVATE).edit().putString("categoryName", value).apply();
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoryAllFragment()).commit();

            }
        });

        return view;
    }
}