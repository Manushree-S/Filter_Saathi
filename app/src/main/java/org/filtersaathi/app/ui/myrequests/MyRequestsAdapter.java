package org.filtersaathi.app.ui.myrequests;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.filtersaathi.app.R;
import org.filtersaathi.app.domain.model.BookingRequest;
import org.filtersaathi.app.domain.model.ProblemReport;
import org.filtersaathi.app.domain.model.RequestOrReportItem;

import java.util.ArrayList;
import java.util.List;

public class MyRequestsAdapter extends RecyclerView.Adapter<MyRequestsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RequestOrReportItem item);
    }

    private List<RequestOrReportItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public MyRequestsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<RequestOrReportItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestOrReportItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRequestId;
        private final TextView tvRequestLocation;
        private final TextView tvStatusChip;
        private final TextView tvTagType;
        private final TextView tvRequestTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestId = itemView.findViewById(R.id.tvRequestId);
            tvRequestLocation = itemView.findViewById(R.id.tvRequestLocation);
            tvStatusChip = itemView.findViewById(R.id.tvStatusChip);
            tvTagType = itemView.findViewById(R.id.tvTagType);
            tvRequestTime = itemView.findViewById(R.id.tvRequestTime);
        }

        public void bind(RequestOrReportItem item, OnItemClickListener listener) {
            Context ctx = itemView.getContext();

            if (item instanceof RequestOrReportItem.Booking) {
                BookingRequest req = ((RequestOrReportItem.Booking) item).getRequest();
                tvRequestId.setText(req.getRequestNumber());
                tvRequestLocation.setText(req.getVillage() + ", " + req.getDistrict());
                tvTagType.setText(R.string.ui_tag_request);

                int stageResId = ctx.getResources().getIdentifier(req.getStageKey(), "string", ctx.getPackageName());
                String stageText = stageResId != 0 ? ctx.getString(stageResId) : req.getStageKey();
                tvStatusChip.setText(stageText);

                if (req.getCurrentStage() >= 6) {
                    tvStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.status_success)));
                } else if (req.getCurrentStage() >= 3) {
                    tvStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.secondary_blue)));
                } else {
                    tvStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.status_warning)));
                }

                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(req.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                tvRequestTime.setText(timeAgo);

            } else if (item instanceof RequestOrReportItem.Report) {
                ProblemReport report = ((RequestOrReportItem.Report) item).getReport();
                tvRequestId.setText(report.getTicketNumber());
                tvRequestLocation.setText(report.getVillage());
                tvTagType.setText(R.string.ui_tag_report);

                int probResId = ctx.getResources().getIdentifier(report.getProblemType().getKey(), "string", ctx.getPackageName());
                String probText = probResId != 0 ? ctx.getString(probResId) : report.getProblemType().name();
                tvStatusChip.setText(probText);
                tvStatusChip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.accent_saffron)));

                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(report.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
                tvRequestTime.setText(timeAgo);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
