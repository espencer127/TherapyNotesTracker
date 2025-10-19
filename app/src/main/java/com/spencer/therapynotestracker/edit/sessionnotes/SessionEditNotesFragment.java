package com.spencer.therapynotestracker.edit.sessionnotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.spencer.therapynotestracker.R;
import com.spencer.therapynotestracker.databinding.FragmentSessionNotesEditBinding;
import com.spencer.therapynotestracker.edit.ActiveSessionViewModel;

public class SessionEditNotesFragment extends Fragment {


    private FragmentSessionNotesEditBinding binding;

    private ActiveSessionViewModel activeSessionViewModel;


    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSessionNotesEditBinding.inflate(inflater, container, false);

        View contentView = inflater.inflate(R.layout.fragment_session_agenda_edit, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSaveSessionEdit.setOnClickListener(v -> {
                    attemptEdit();
                }
        );

        activeSessionViewModel = new ViewModelProvider(requireActivity()).get(ActiveSessionViewModel.class);
        activeSessionViewModel.getSelectedItem().observe(getViewLifecycleOwner(), session -> {
            // Update the list UI.
            // Find the TextView by its ID
            EditText editNotes = view.findViewById(R.id.edit_notes);

            // Set the text
            editNotes.setText(session.getNotes());
        });
    }

    private void attemptEdit() {
        EditText editNotes = getView().findViewById(R.id.edit_notes);
        String date = activeSessionViewModel.getSelectedItem().getValue().getDate();

        String newNotes = editNotes.getText().toString();

        activeSessionViewModel.editItemNotes(date, newNotes);

        Toast.makeText(getView().getContext(), "Edited record for " + date, Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(SessionEditNotesFragment.this)
                .navigate(R.id.action_SessionEditNotesFragment_to_FirstFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
