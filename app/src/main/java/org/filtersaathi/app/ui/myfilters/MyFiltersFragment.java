package org.filtersaathi.app.ui.myfilters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentMyFiltersBinding;
import org.filtersaathi.app.domain.model.FilterDevice;

import java.util.List;

/**
 * MyFiltersFragment:
 * - Displays list of user's filter devices sorted by care urgency.
 * - Displays device type, installation date, overall status chip, and item life bars.
 * - Clicking a device navigates to FilterDetailFragment.
 */
@AndroidEntryPoint
public class MyFiltersFragment extends Fragment {

    private FragmentMyFiltersBinding binding;
    private MyFiltersViewModel viewModel;
    private MyFiltersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyFiltersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MyFiltersViewModel.class);

        adapter = new MyFiltersAdapter(device -> {
            Bundle args = new Bundle();
            args.putString("deviceId", device.getId());
            Navigation.findNavController(requireView()).navigate(R.id.navigation_filter_detail, args);
        });

        binding.rvFilterDevices.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFilterDevices.setAdapter(adapter);

        viewModel.getFilterDevices().observe(getViewLifecycleOwner(), devices -> {
            List<FilterDevice> sorted = viewModel.getSortedDevices();
            if (sorted == null || sorted.isEmpty()) {
                binding.tvEmptyState.setVisibility(View.VISIBLE);
                binding.rvFilterDevices.setVisibility(View.GONE);
            } else {
                binding.tvEmptyState.setVisibility(View.GONE);
                binding.rvFilterDevices.setVisibility(View.VISIBLE);
                adapter.setDevices(sorted);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
