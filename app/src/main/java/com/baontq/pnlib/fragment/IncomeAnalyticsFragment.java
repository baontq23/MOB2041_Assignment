package com.baontq.pnlib.fragment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.CallCardDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.BookAnalytics;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.concurrent.Executors;


public class IncomeAnalyticsFragment extends Fragment {
    private TextInputLayout tilLayoutStartDate, tilLayoutEndDate;
    private TextInputEditText edtLayoutStartDate, edtLayoutEndDate;
    private MaterialButton btnAnalytics;
    private TextView tvTotalBorrow, tvTotalIncome;
    private String startDate, endDate;
    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();
    private CallCardDao callCardDao;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callCardDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().callCardDao();
        tilLayoutStartDate = view.findViewById(R.id.til_layout_start_date);
        edtLayoutStartDate = view.findViewById(R.id.edt_layout_start_date);
        tilLayoutEndDate = view.findViewById(R.id.til_layout_end_date);
        edtLayoutEndDate = view.findViewById(R.id.edt_layout_end_date);
        btnAnalytics = view.findViewById(R.id.btn_analytics);
        tvTotalBorrow = view.findViewById(R.id.tv_total_borrow);
        tvTotalIncome = view.findViewById(R.id.tv_total_income);
        tilLayoutStartDate.setEndIconOnClickListener(view1 -> {
            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    startDate = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                    edtLayoutStartDate.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
                    startCalendar.set(Calendar.YEAR, year);
                    startCalendar.set(Calendar.MONTH, monthOfYear);
                    startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }
            }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        tilLayoutEndDate.setEndIconOnClickListener(view1 -> {
            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    endDate = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                    edtLayoutEndDate.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
                    endCalendar.set(Calendar.YEAR, year);
                    endCalendar.set(Calendar.MONTH, monthOfYear);
                    endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        btnAnalytics.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(startDate)) {
                tilLayoutStartDate.setError("Chưa chọn ngày bắt đầu");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tilLayoutStartDate.setErrorEnabled(false);
                    }
                }, 2000);
                return;
            }

            if (TextUtils.isEmpty(endDate)) {
                tilLayoutEndDate.setError("Chưa chọn ngày kết thúc");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tilLayoutEndDate.setErrorEnabled(false);
                    }
                }, 2000);
                return;
            }

            if (startCalendar.after(endCalendar)) {
                Toast.makeText(getContext(), "Ngày bắt đầu và kết thúc không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
          class AnalyticsTask extends AsyncTask<Void, Void, BookAnalytics> {

              @Override
              protected BookAnalytics doInBackground(Void... voids) {
                  return callCardDao.analyticsByDate(startDate, endDate);
              }

              @Override
              protected void onPostExecute(BookAnalytics bookAnalytics) {
                  super.onPostExecute(bookAnalytics);
                  tvTotalBorrow.setText("Số lượng sách cho thuê: " + bookAnalytics.getBorrowCount());
                  tvTotalIncome.setText(String.format("Tổng doanh thu: %.0f VND", bookAnalytics.getTotalPrice()));
              }
          }

            AnalyticsTask analyticsTask = new AnalyticsTask();
            analyticsTask.execute();
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_income_analytics, container, false);
    }
}