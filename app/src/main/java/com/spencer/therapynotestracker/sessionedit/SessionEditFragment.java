package com.spencer.therapynotestracker.sessionedit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.spencer.therapynotestracker.R;
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
            EditText editAgenda = view.findViewById(R.id.edit_agenda);
            Toast.makeText(v.getContext(), editAgenda.getText(), Toast.LENGTH_SHORT).show();

            }
        );

        activeSessionViewModel = new ViewModelProvider(requireActivity()).get(ActiveSessionViewModel.class);
        activeSessionViewModel.getSelectedItem().observe(getViewLifecycleOwner(), session -> {
            // Update the list UI.
            // Find the TextView by its ID
            EditText editDate = view.findViewById(R.id.edit_date);
            EditText editAgenda = view.findViewById(R.id.edit_agenda);
            EditText editNotes = view.findViewById(R.id.edit_notes);


            // Set the text
            editDate.setText(session.getDate());
            editAgenda.setText(session.getAgenda());
            editNotes.setText(session.getNotes());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}