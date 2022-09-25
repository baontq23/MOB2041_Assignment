package com.baontq.pnlib.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.adapter.Top10BookAdapter;
import com.baontq.pnlib.dao.BookDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Book;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class Top10SellerFragment extends Fragment {
    private RecyclerView rvTop10Book;
    private Top10BookAdapter top10BookAdapter;
    private BookDao bookDao;
    private TextView tvListStatus;
    private MaterialButton btnSortByCount, btnSortByIncome;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvTop10Book = view.findViewById(R.id.rv_top10_book);
        btnSortByCount = view.findViewById(R.id.btn_sort_by_count);
        btnSortByIncome = view.findViewById(R.id.btn_sort_by_income);
        tvListStatus = view.findViewById(R.id.tv_list_status);
        bookDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().bookDao();
        rvTop10Book.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        btnSortByCount.setOnClickListener(view1 -> getDataByBorrowCount());
        btnSortByIncome.setOnClickListener(view1 -> getDataByIncome());
        getDataByBorrowCount();
    }

    private void getDataByBorrowCount() {
        class GetTop10Task extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                top10BookAdapter = new Top10BookAdapter(requireContext(),bookDao.getTop10());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                rvTop10Book.setAdapter(top10BookAdapter);
                checkListStatus();
            }
        }
        GetTop10Task getTop10Task = new GetTop10Task();
        getTop10Task.execute();
    }

    private void getDataByIncome() {
        class GetTop10Task extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                top10BookAdapter = new Top10BookAdapter(requireContext(),bookDao.getTop10ByIncome());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                rvTop10Book.setAdapter(top10BookAdapter);
                checkListStatus();
            }
        }
        GetTop10Task getTop10Task = new GetTop10Task();
        getTop10Task.execute();
    }

    private void checkListStatus() {
        if (top10BookAdapter.getItemCount() > 0) {
            tvListStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top10_seller, container, false);
    }
}