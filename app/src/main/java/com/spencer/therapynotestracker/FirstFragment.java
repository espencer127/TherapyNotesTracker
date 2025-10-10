package com.spencer.therapynotestracker;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spencer.therapynotestracker.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements View.OnClickListener {

    private FragmentFirstBinding binding;

    private List<String> list;

    private HomeViewModel homeViewModel;

    private NoteAdapter listAdapter;
    FloatingActionButton enterButton;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        this.list = new ArrayList<>();

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_first, container, false);
        ListView listView = contentView.findViewById(R.id.listview);

        addItems(container, listView);

        enterButton = contentView.findViewById(R.id.fab);
        enterButton.setOnClickListener(this);

        return contentView;
    }

    public void addItems(ViewGroup container, ListView listView) {
        // sample data
        for (int i=0;i<10;i++) {
            list.add("Item " + i);
        }

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.setBins(list);

        this.listAdapter = new NoteAdapter(homeViewModel.getBins().getValue(), container.getContext());
        listView.setAdapter(listAdapter);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe the LiveData. The onChanged() method is called when the data changes.
        homeViewModel.getBins().observe(getViewLifecycleOwner(), bins -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ((MainActivity) getActivity()).sendBinAlertNotification(bins);
            }

            listAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            // Code to execute when button_one is clicked
            List<String> newList = new ArrayList<>();
//            firstFragment.addList("Item gazillion");

            for (int i=10;i<20;i++) {
                newList.add("Item " + i);
            }

            homeViewModel.addItems(newList);

            Toast.makeText(v.getContext(), "Add Button Clicked!", Toast.LENGTH_SHORT).show();
            // Add more cases for other buttons if needed
        }
    }

}