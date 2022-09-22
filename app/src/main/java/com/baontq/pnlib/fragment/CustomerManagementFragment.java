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

import com.baontq.pnlib.R;
import com.baontq.pnlib.adapter.CustomerAdapter;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CustomerManagementFragment extends Fragment {
    private RecyclerView rvListCustomer;
    private FloatingActionButton fabAddCustomer;
    private CustomerAdapter customerAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvListCustomer = view.findViewById(R.id.rv_list_customer);
        fabAddCustomer = view.findViewById(R.id.fab_add_customer);
        rvListCustomer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getList();
        fabAddCustomer.setOnClickListener(view1 -> customerAdapter.addItem());
    }

    private void getList() {
        class GetListTask extends AsyncTask<Void, Void, List<Customer>> {

            @Override
            protected List<Customer> doInBackground(Void... voids) {
                return DatabaseClient.getInstance(requireContext()).getAppDatabase().customerDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Customer> customers) {
                super.onPostExecute(customers);
                customerAdapter = new CustomerAdapter(requireContext(), customers);
                rvListCustomer.setAdapter(customerAdapter);
            }
        }
        GetListTask getListTask = new GetListTask();
        getListTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_management, container, false);
    }
}