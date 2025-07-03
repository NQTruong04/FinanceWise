package com.example.financewise.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financewise.R;
import com.example.financewise.data.model.Saving;
import com.google.firebase.Timestamp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SavingAdapter extends ListAdapter<Saving, SavingAdapter.SavingViewHolder> {

    private String savingTargetCategory; // Thêm trường này

    private static final DiffUtil.ItemCallback<Saving> DIFF_CALLBACK = new DiffUtil.ItemCallback<Saving>() {
        @Override
        public boolean areItemsTheSame(@NonNull Saving oldItem, @NonNull Saving newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Saving oldItem, @NonNull Saving newItem) {
            return oldItem.equals(newItem);
        }
    };

    // Cập nhật constructor để nhận savingTargetCategory
    public SavingAdapter(String savingTargetCategory) {
        super(DIFF_CALLBACK);
        this.savingTargetCategory = savingTargetCategory;
    }

    // Hoặc bạn có thể thêm một setter nếu cần cập nhật category sau này
    public void setSavingTargetCategory(String savingTargetCategory) {
        this.savingTargetCategory = savingTargetCategory;
        // Nếu bạn muốn cập nhật ngay lập tức UI khi category thay đổi
        // notifyDataSetChanged(); // Cẩn thận khi dùng, có thể gây hiệu năng kém
    }

    @NonNull
    @Override
    public SavingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_v2, parent, false);
        // Truyền savingTargetCategory vào ViewHolder
        return new SavingViewHolder(view, savingTargetCategory);
    }

    @Override
    public void onBindViewHolder(@NonNull SavingViewHolder holder, int position) {
        Saving currentSaving = getItem(position);
        holder.bind(currentSaving);
    }

    public static class SavingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvTitle;
        private final TextView tvAmount;
        private final TextView tvDate;
        private final String category; // Lưu trữ category ở đây

        // Cập nhật constructor để nhận category
        public SavingViewHolder(@NonNull View itemView, String category) {
            super(itemView);
            this.category = category; // Gán category
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(Saving saving) {
            if (saving == null) {
                Log.e("SavingViewHolder", "Attempted to bind a null Saving object.");
                return;
            }

            tvTitle.setText(saving.getTitle());

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            formatter.setMaximumFractionDigits(0);
            tvAmount.setText(formatter.format(saving.getAmount()));

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - MMMM dd, yyyy", Locale.getDefault());
            Object dateObj = saving.getDate();
            if (dateObj instanceof Timestamp) {
                tvDate.setText(sdf.format(((Timestamp) dateObj).toDate()));
            } else if (dateObj instanceof Date) {
                tvDate.setText(sdf.format((Date) dateObj));
            } else {
                Log.e("SavingAdapter", "Unknown date type: " + (dateObj != null ? dateObj.getClass().getName() : "null"));
                tvDate.setText("Invalid Date");
            }

            // Đặt icon dựa trên category đã truyền vào
            ivIcon.setImageResource(getIconForCategory(category));
        }

        // Phương thức trợ giúp để lấy icon
        private int getIconForCategory(String category) {
            if (category == null) return R.drawable.ic_savings; // Icon mặc định
            switch (category.toLowerCase(Locale.getDefault())) {
                case "travel": return R.drawable.ic_travel_white;
                case "car": return R.drawable.ic_car_white;
                case "wedding": return R.drawable.ic_wedding_white;
                case "house": return R.drawable.ic_house_white;
                default: return R.drawable.ic_savings;
            }
        }
    }
}