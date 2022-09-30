package com.baontq.pnlib.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.adapter.CallCardAdapter;
import com.baontq.pnlib.dao.BookDao;
import com.baontq.pnlib.dao.CallCardDao;
import com.baontq.pnlib.dao.CustomerDao;
import com.baontq.pnlib.dao.GenreDao;
import com.baontq.pnlib.dao.LibrarianDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Book;
import com.baontq.pnlib.model.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CallCardManagementFragment extends Fragment {
    private static final Integer LIST_BOOK_EMPTY = 1;
    private static final Integer LIST_CUSTOMER_EMPTY = 2;
    private RecyclerView rvListCallCard;
    private FloatingActionButton fabAddCallCard;
    private TextView tvListStatus;
    private BookDao bookDao;
    private CallCardDao callCardDao;
    private CustomerDao customerDao;
    private GenreDao genreDao;
    private LibrarianDao librarianDao;
    private CallCardAdapter callCardAdapter;
    private List<Book> bookList;
    private List<Customer> customerList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvListCallCard = view.findViewById(R.id.rv_list_call_card);
        fabAddCallCard = view.findViewById(R.id.fab_add_call_card);
        tvListStatus = view.findViewById(R.id.tv_list_status);
        bookDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().bookDao();
        callCardDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().callCardDao();
        customerDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().customerDao();
        librarianDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().librarianDao();
        genreDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().genreDao();
        rvListCallCard.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fabAddCallCard.setOnClickListener(view1 -> callCardAdapter.addItem());
       rvListCallCard.addOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               if (dy > 5) {
                   fabAddCallCard.setVisibility(View.INVISIBLE);
               }else  {
                   fabAddCallCard.setVisibility(View.VISIBLE);
               }
           }
       });
        initialData();
    }

    private void initialData() {
        class GetCallCardTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                bookList = bookDao.getAll();
                if (bookList.isEmpty()) {
                    return LIST_BOOK_EMPTY;
                }
                customerList = customerDao.getAll();
                if (customerList.isEmpty()) {
                    return LIST_CUSTOMER_EMPTY;
                }
                callCardAdapter = new CallCardAdapter(requireContext(), callCardDao.listAll(), bookList, librarianDao.listAll(), customerDao.getAll(), genreDao.getAll(), position -> checkStatusList(position));

                return 0;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (result.equals(LIST_BOOK_EMPTY)) {
                    tvListStatus.setText("Vui lòng thêm sách trước khi tạo phiếu mượn");
                    fabAddCallCard.setVisibility(View.INVISIBLE);
                } else if (result.equals(LIST_CUSTOMER_EMPTY)) {
                    tvListStatus.setText("Vui lòng thêm khách hàng trước khi tạo phiếu mượn");
                    fabAddCallCard.setVisibility(View.INVISIBLE);
                } else {
                    rvListCallCard.setAdapter(callCardAdapter);
                    checkStatusList(0);
                }

            }

        }
        GetCallCardTask getCallCardTask = new GetCallCardTask();
        getCallCardTask.execute();
    }

    private void checkStatusList(int position) {
        if (callCardAdapter.getItemCount() == 0) {
            tvListStatus.setVisibility(View.VISIBLE);
        } else {
            tvListStatus.setVisibility(View.INVISIBLE);
            rvListCallCard.smoothScrollToPosition(position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_call_card_management, container, false);
    }
}