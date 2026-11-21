package org.filtersaathi.app.ui.book;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.filtersaathi.app.R;
import org.filtersaathi.app.databinding.DialogMapPickerBinding;
import org.filtersaathi.app.util.LocationAddressResolver;
import org.filtersaathi.app.util.ResolvedAddress;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MapPickerDialogFragment extends DialogFragment {

    public interface OnLocationSelectedListener {
        void onLocationSelected(double lat, double lng, @Nullable ResolvedAddress resolvedAddress);
    }

    private DialogMapPickerBinding binding;
    private OnLocationSelectedListener listener;

    private double lastGeocodedLat = 0;
    private double lastGeocodedLng = 0;
    private ResolvedAddress lastResolvedAddress = null;
    private long lastGeocodeTime = 0;

    private Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;

    public static MapPickerDialogFragment newInstance() {
        return new MapPickerDialogFragment();
    }

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FilterSaathi);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context ctx = requireContext();
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(new File(ctx.getCacheDir(), "osmdroid"));

        binding = DialogMapPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(true);

        // Centring on Fused Location
        FusedLocationProviderClient fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        try {
            fusedClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && binding != null) {
                    GeoPoint userPt = new GeoPoint(location.getLatitude(), location.getLongitude());
                    binding.mapView.getController().setCenter(userPt);
                    binding.mapView.getController().setZoom(16.0);
                    onMapPositionChanged(location.getLatitude(), location.getLongitude());
                } else if (binding != null) {
                    GeoPoint defaultPt = new GeoPoint(12.9716, 77.5946); // Bengaluru default
                    binding.mapView.getController().setCenter(defaultPt);
                    binding.mapView.getController().setZoom(15.0);
                    onMapPositionChanged(12.9716, 77.5946);
                }
            });
        } catch (SecurityException ignored) {
            GeoPoint defaultPt = new GeoPoint(12.9716, 77.5946);
            binding.mapView.getController().setCenter(defaultPt);
            binding.mapView.getController().setZoom(15.0);
            onMapPositionChanged(12.9716, 77.5946);
        }

        binding.btnPickerBack.setOnClickListener(v -> dismiss());

        binding.fabRecentre.setOnClickListener(v -> {
            try {
                fusedClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null && binding != null) {
                        GeoPoint userPt = new GeoPoint(location.getLatitude(), location.getLongitude());
                        binding.mapView.getController().animateTo(userPt);
                    }
                });
            } catch (SecurityException ignored) {}
        });

        binding.btnConfirmLocation.setOnClickListener(v -> {
            if (listener != null && binding != null) {
                IGeoPoint center = binding.mapView.getMapCenter();
                listener.onLocationSelected(center.getLatitude(), center.getLongitude(), lastResolvedAddress);
            }
            dismiss();
        });

        // 600 ms Debounced map center reverse-geocoding
        debounceRunnable = () -> {
            if (binding != null) {
                IGeoPoint center = binding.mapView.getMapCenter();
                onMapPositionChanged(center.getLatitude(), center.getLongitude());
            }
        };

        binding.mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                debounceHandler.removeCallbacks(debounceRunnable);
                debounceHandler.postDelayed(debounceRunnable, 600);
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                debounceHandler.removeCallbacks(debounceRunnable);
                debounceHandler.postDelayed(debounceRunnable, 600);
                return false;
            }
        });
    }

    private void onMapPositionChanged(double lat, double lng) {
        if (Math.abs(lat - lastGeocodedLat) < 0.0001 && Math.abs(lng - lastGeocodedLng) < 0.0001 && lastResolvedAddress != null) {
            updateAddressUi(lastResolvedAddress, lat, lng);
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastGeocodeTime < 1000) {
            return; // Rate limit max 1 req/sec
        }
        lastGeocodeTime = now;

        if (binding == null) return;
        binding.pbPickerLoading.setVisibility(View.VISIBLE);
        binding.tvPickerAddress.setText(R.string.ui_picker_loading_address);

        Executors.newSingleThreadExecutor().execute(() -> {
            ResolvedAddress resolved = LocationAddressResolver.reverseGeocodeHttp(lat, lng);

            if (resolved == null && Geocoder.isPresent() && isAdded()) {
                try {
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(lat, lng, 1);
                    if (list != null && !list.isEmpty()) {
                        resolved = LocationAddressResolver.resolveAddress(list.get(0));
                    }
                } catch (Exception ignored) {}
            }

            ResolvedAddress finalResolved = resolved;
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (binding != null) {
                        binding.pbPickerLoading.setVisibility(View.GONE);
                        lastGeocodedLat = lat;
                        lastGeocodedLng = lng;
                        lastResolvedAddress = finalResolved;
                        updateAddressUi(finalResolved, lat, lng);
                    }
                });
            }
        });
    }

    private void updateAddressUi(ResolvedAddress resolved, double lat, double lng) {
        if (binding == null) return;
        if (resolved != null && !resolved.isOutsideKarnataka()) {
            StringBuilder sb = new StringBuilder();
            if (resolved.getSumpLocation() != null) sb.append(resolved.getSumpLocation()).append(", ");
            if (resolved.getVillageOrPanchayat() != null) sb.append(resolved.getVillageOrPanchayat()).append(", ");
            if (resolved.getDistrict() != null) sb.append(resolved.getDistrict()).append(" ");
            if (resolved.getPostalCode() != null) sb.append(resolved.getPostalCode());

            String addr = sb.toString().trim();
            if (addr.endsWith(",")) addr = addr.substring(0, addr.length() - 1);
            if (addr.isEmpty()) {
                addr = String.format(Locale.US, "%.5f, %.5f", lat, lng);
            }
            binding.tvPickerAddress.setText(addr);
        } else if (resolved != null && resolved.isOutsideKarnataka()) {
            binding.tvPickerAddress.setText(R.string.ui_location_outside_karnataka);
        } else {
            binding.tvPickerAddress.setText(String.format(Locale.US, "%.5f, %.5f", lat, lng));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            binding.mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (binding != null) {
            binding.mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (debounceHandler != null && debounceRunnable != null) {
            debounceHandler.removeCallbacks(debounceRunnable);
        }
        binding = null;
        super.onDestroyView();
    }
}
