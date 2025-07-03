package com.example.financewise.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.financewise.data.model.Saving;
import com.example.financewise.data.model.SavingTarget;
import com.example.financewise.data.repository.SavingTargetRepository;
import com.example.financewise.data.repository.SavingRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailSavingViewModel extends BaseViewModel{
    private static final String TAG = "DetailSavingViewModel";
    private SavingTargetRepository savingTargetRepository;
    private SavingRepository savingRepository;
    private final MutableLiveData<SavingTarget> savingTarget = new MutableLiveData<>();
    private final MutableLiveData<List<Saving>> savings = new MutableLiveData<>();
    private final MutableLiveData<Date> selectedDate = new MutableLiveData<>();
    private String userId;
    private String savingName;
    private String targetId;
    private final Map<String, Observer<?>> observers = new HashMap<>();

    public DetailSavingViewModel(){
        selectedDate.setValue(null);
    }

    public void setUserIdAndSavingName(String userId, String savingName) {
        if(this.userId == null || !this.userId.equals(userId) || this.savingName == null || !this.savingName.equals(savingName)) {
            this.userId = userId;
            this.savingName = savingName;
            if (this.userId != null) {
                savingTargetRepository = new SavingTargetRepository(this.userId);
                Log.d(TAG, "initializeRepositories: userId = " + this.userId);
            } else {
                Log.e(TAG, "User ID is null. Cannot initialize repositories.");
                return; // Thoát nếu userId là null
            }
            loadSavingTargetBySavingName();
        }
    }

    private void loadSavingTargetBySavingName(){
        if(userId == null || savingName == null || savingTargetRepository == null){ // Thêm kiểm tra savingTargetRepository
            Log.e(TAG, "User ID, Saving Name, or SavingTargetRepository is null. Cannot load saving target.");
            savingTarget.setValue(null);
            return;
        }

        LiveData<SavingTarget> targetLiveData = savingTargetRepository.getSavingTargetByCategory(savingName, userId);
        Observer<SavingTarget> observer = target->{
            Log.d(TAG, "SavingTarget observed:" + (target != null));
            savingTarget.setValue(target);
            if (target != null) {
                targetId = target.getId(); // Lấy ID tài liệu thực tế của SavingTarget
                savingRepository = new SavingRepository(targetId, userId); // Khởi tạo SavingRepository với ID đúng
                loadSavings(); // Tải các khoản tiết kiệm sau khi có targetId
            } else {
                // Xử lý trường hợp không tìm thấy SavingTarget
                targetId = null;
                savingRepository = null;
                savings.setValue(null); // Xóa dữ liệu tiết kiệm cũ
            }
        };
        observeForeverWithKey("savingTarget", targetLiveData, observer,null);
        observers.put("savingTarget", observer);
    }
    private void loadSavings(){
        if(userId == null || targetId == null || savingRepository == null){
            Log.e(TAG, "userId or targetId is null");
            savingTarget.setValue(null);
            return;
        }
        LiveData<List<Saving>> savingsLiveData;
        if(selectedDate.getValue() != null){
            long startDate = selectedDate.getValue().getTime();
            long endDate = startDate + 24 * 60 * 60 * 1000 - 1;
            // Gọi phương thức từ savingRepository
            savingsLiveData = savingRepository.getItemsByDateRange("date", startDate, endDate, Saving.class);
        }else {
            // Gọi phương thức từ savingRepository
            savingsLiveData = savingRepository.getAllItems(Saving.class);
        }
        Observer<List<Saving>> observer = savingsList -> {
            Log.d(TAG, "Savings observed, size: " + (savingsList != null ? savingsList.size() : 0));
            savings.setValue(savingsList);
        };
        observeForeverWithKey("saving_", savingsLiveData, observer, null);
        observers.put("saving_", observer);

    }

    public void setSelectedDate(Date date){
        selectedDate.setValue(date);
        loadSavings();
    }
    public LiveData<SavingTarget> getSavingTarget() {
        return savingTarget;
    }

    public LiveData<List<Saving>> getSavings() {
        return savings;
    }

    public LiveData<Date> getSelectedDate() {
        return selectedDate;
    }
}
