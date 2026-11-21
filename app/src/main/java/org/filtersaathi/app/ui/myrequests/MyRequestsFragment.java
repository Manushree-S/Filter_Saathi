package org.filtersaathi.app.ui.myrequests;

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
import org.filtersaathi.app.databinding.FragmentMyRequestsBinding;
import org.filtersaathi.app.domain.model.RequestOrReportItem;
import org.filtersaathi.app.domain.model.UserProfile;

import java.util.List;

/**
 * MyRequestsFragment:
 * - Public list of installation requests and problem reports owned by the signed-in user.
 * - Strict ownership filter: shows ONLY items matching ownerPhone.
 * - Displays signed-out empty state with Sign-in CTA when signed out.
 * - Supports filtering by All, Requests, or Reports.
 */
@AndroidEntryPoint
public class MyRequestsFragment extends Fragment {

    private FragmentMyRequestsBinding binding;
    private MyRequestsViewModel viewModel;
    private MyRequestsAdapter adapter;
    private RequestFilterType currentFilter = RequestFilterType.ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MyRequestsViewModel.class);

        adapter = new MyRequestsAdapter(item -> {
            Bundle args = new Bundle();
            args.putString("requestId", item.getId());
            Navigation.findNavController(requireView()).navigate(R.id.navigation_request_detail, args);
        });

        binding.rvMyRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMyRequests.setAdapter(adapter);

        setupFilterChips();
        setupActions();
        observeUserState();
    }

    private void setupFilterChips() {
        updateChipSelection(RequestFilterType.ALL);

        binding.chipFilterAll.setOnClickListener(v -> {
            currentFilter = RequestFilterType.ALL;
            updateChipSelection(RequestFilterType.ALL);
            refreshList();
        });

        binding.chipFilterRequests.setOnClickListener(v -> {
            currentFilter = RequestFilterType.REQUESTS;
            updateChipSelection(RequestFilterType.REQUESTS);
            refreshList();
        });

        binding.chipFilterReports.setOnClickListener(v -> {
            currentFilter = RequestFilterType.REPORTS;
            updateChipSelection(RequestFilterType.REPORTS);
            refreshList();
        });
    }

    private void updateChipSelection(RequestFilterType selected) {
        binding.chipFilterAll.setSelected(selected == RequestFilterType.ALL);
        binding.chipFilterRequests.setSelected(selected == RequestFilterType.REQUESTS);
        binding.chipFilterReports.setSelected(selected == RequestFilterType.REPORTS);
    }

    private void setupActions() {
        binding.btnSignInRequests.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_register);
        });

        binding.btnBookInstallation.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_book);
        });
    }

    private void observeUserState() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null || !user.isLoggedIn()) {
                binding.layoutSignedOut.setVisibility(View.VISIBLE);
                binding.layoutSignedInContent.setVisibility(View.GONE);
            } else {
                binding.layoutSignedOut.setVisibility(View.GONE);
                binding.layoutSignedInContent.setVisibility(View.VISIBLE);
                refreshList();
            }
        });
    }

    private void refreshList() {
        UserProfile user = viewModel.getCurrentUser().getValue();
        if (user == null || !user.isLoggedIn()) {
            adapter.setItems(null);
            return;
        }

        List<RequestOrReportItem> filtered = viewModel.getFilteredItems(currentFilter);
        if (filtered == null || filtered.isEmpty()) {
            binding.layoutEmptyRequests.setVisibility(View.VISIBLE);
            binding.rvMyRequests.setVisibility(View.GONE);
            adapter.setItems(null);
        } else {
            binding.layoutEmptyRequests.setVisibility(View.GONE);
            binding.rvMyRequests.setVisibility(View.VISIBLE);
            adapter.setItems(filtered);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
