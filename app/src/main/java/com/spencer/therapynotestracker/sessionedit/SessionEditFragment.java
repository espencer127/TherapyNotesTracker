package com.spencer.therapynotestracker.sessionedit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.spencer.therapynotestracker.R;
import com.spencer.therapynotestracker.database.Session;
import com.spencer.therapynotestracker.databinding.FragmentSessionEditBinding;

public class SessionEditFragment extends Fragment {

    private FragmentSessionEditBinding binding;

    private ActiveSessionViewModel activeSessionViewModel;


    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSessionEditBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_session_edit, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SessionEditFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        binding.buttonSaveSessionEdit.setOnClickListener(v -> {
                attemptEdit();
            }
        );

        binding.buttonSaveSessionDelete.setOnClickListener(v -> {
                attemptDelete();
            }
        );

        activeSessionViewModel = new ViewModelProvider(requireActivity()).get(ActiveSessionViewModel.class);
        activeSessionViewModel.getSelectedItem().observe(getViewLifecycleOwner(), session -> {
            // Update the list UI.
            // Find the TextView by its ID
            TextView editDate = view.findViewById(R.id.view_date);
            EditText editAgenda = view.findViewById(R.id.edit_agenda);
            EditText editNotes = view.findViewById(R.id.edit_notes);
            EditText editTherapist = view.findViewById(R.id.edit_therapist);

            // Set the text
            editDate.setText(session.getDate());
            editAgenda.setText(session.getAgenda());
            editNotes.setText(session.getNotes());
            editTherapist.setText(session.getTherapist());
        });
    }

    private void attemptEdit() {
        EditText editAgenda = getView().findViewById(R.id.edit_agenda);
        EditText editNotes = getView().findViewById(R.id.edit_notes);
        TextView date = getView().findViewById(R.id.view_date);
        EditText editTherapist = getView().findViewById(R.id.edit_therapist);

        String dateDate = date.getText().toString();
        String newAgenda = editAgenda.getText().toString();
        String newNotes = editNotes.getText().toString();
        String newTherapist = editTherapist.getText().toString();

        Session tempSesh = new Session(dateDate, newAgenda, newNotes, newTherapist);

        activeSessionViewModel.editItem(tempSesh);

        Toast.makeText(getView().getContext(), "Edited record with date " + editAgenda.getText(), Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(SessionEditFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    private void attemptDelete() {
        EditText editAgenda = getView().findViewById(R.id.edit_agenda);
        TextView date = getView().findViewById(R.id.view_date);
        String dateDate = date.getText().toString();

        activeSessionViewModel.deleteItem(dateDate);

        Toast.makeText(getView().getContext(), "Deleted the item for date " + dateDate, Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(SessionEditFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}