package com.spencer.therapynotestracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spencer.therapynotestracker.databinding.FragmentFirstBinding;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements View.OnClickListener {

    private FragmentFirstBinding binding;

    private List<Session> list;

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

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        this.listAdapter = new NoteAdapter(homeViewModel.getSessions().getValue(), container.getContext());

        homeViewModel.getSessions().observe(getViewLifecycleOwner(), new Observer<List<Session>>() {
            @Override
            public void onChanged(List<Session> dataList) {
                if (listAdapter.items != null && !listAdapter.isEmpty()) {
                    listAdapter.items.clear(); // Clear existing data
                    if (dataList != null && !dataList.isEmpty())
                        listAdapter.items.addAll(dataList); // Add new data
                }
                listAdapter.notifyDataSetChanged(); // Notify the adapter of the change
            }
        });



        //this.list = homeViewModel.getSessions().getValue(); // new ArrayList<>();

        listAdapter.notifyDataSetChanged();
        
         listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Session clickedItem = (Session) parent.getItemAtPosition(position);

                Toast.makeText(view.getContext(), "Clicked: " + clickedItem.getDate(), Toast.LENGTH_SHORT).show();
            }
        });

        enterButton = contentView.findViewById(R.id.fab);
        enterButton.setOnClickListener(this);

        return contentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe the LiveData. The onChanged() method is called when the data changes.
        homeViewModel.getSessions().observe(getViewLifecycleOwner(), sessions -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (homeViewModel.getSessions().getValue().size() > 0)
                    ((MainActivity) getActivity()).sendBinAlertNotification(sessions);
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
            List<Session> newList = new ArrayList<>();

            //prompt for item
            promptForSession();

            homeViewModel.addItems(newList);

            Toast.makeText(v.getContext(), "Add Button Clicked!", Toast.LENGTH_SHORT).show();
            // Add more cases for other buttons if needed
        }
    }

    private void promptForSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Enter Session Details");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogueView = inflater.inflate(R.layout.alert_session_prompt, null);
        builder.setView(dialogueView);

        final EditText alertPromptDate = dialogueView.findViewById(R.id.alertSessionDate);
        final EditText alertPromptAgenda = dialogueView.findViewById(R.id.alertSessionAgenda);
        final EditText alertPromptNotes = dialogueView.findViewById(R.id.alertSessionNotes);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Session tempSession = new Session
                (alertPromptDate.getText().toString(),
                alertPromptAgenda.getText().toString(),
                alertPromptNotes.getText().toString());
                list.add(tempSession);


                Session session = new Session(tempSession.getDate(), tempSession.getAgenda(), tempSession.getNotes());
                homeViewModel.insert(session);


                listAdapter.notifyDataSetChanged();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ((MainActivity) getActivity()).sendBinAlertNotification(list);
                    Log.d("First Fragment", homeViewModel.getSessions().getValue().toString());

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}