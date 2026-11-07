package org.filtersaathi.app.ui.certificate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.print.PrintHelper;

import dagger.hilt.android.AndroidEntryPoint;
import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.FragmentCertificateBinding;
import org.filtersaathi.app.domain.model.BookingRequest;

/**
 * CertificateFragment (Screen 5):
 * - Displays a paper-style Water Safety Certificate with double border and tricolour strip
 * - Strictly white background with explicit dark text colors for day and night modes
 * - Shows Request number, district, village, installer, date, readings, and sample QR
 * - Handles "Print / Save" via PrintHelper / PrintManager
 * - Handles "Back to tracking" navigation
 */
@AndroidEntryPoint
public class CertificateFragment extends Fragment {

    private FragmentCertificateBinding binding;
    private CertificateViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCertificateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CertificateViewModel.class);

        observeBooking();
        setupActions();
    }

    private void observeBooking() {
        viewModel.getCurrentBooking().observe(getViewLifecycleOwner(), booking -> {
            if (booking != null) {
                bindBookingData(booking);
            }
        });
    }

    private void bindBookingData(@NonNull BookingRequest booking) {
        binding.tvCertReqNo.setText(booking.getRequestNumber());
        binding.tvCertDistrict.setText(booking.getDistrict());
        binding.tvCertVillage.setText(booking.getVillage());
        binding.tvCertInstaller.setText(getString(R.string.ui_installer_name));
        binding.tvCertDate.setText(getString(R.string.ui_date_val));
        binding.tvCertReadings.setText(getString(R.string.ui_read_val));
    }

    private void setupActions() {
        binding.btnPrintCert.setOnClickListener(v -> printCertificate());

        binding.btnBackToTrack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
    }

    private void printCertificate() {
        View cardView = binding.cardCertificate;
        if (cardView == null || cardView.getWidth() <= 0 || cardView.getHeight() <= 0) {
            return;
        }

        try {
            Bitmap bitmap = Bitmap.createBitmap(cardView.getWidth(), cardView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            cardView.draw(canvas);

            PrintHelper printHelper = new PrintHelper(requireContext());
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            String jobName = getString(R.string.ui_cert_title) + " - FilterSaathi";
            printHelper.printBitmap(jobName, bitmap);
        } catch (Exception ignored) {
            // Graceful fallback if printing is not available on device/emulator
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
