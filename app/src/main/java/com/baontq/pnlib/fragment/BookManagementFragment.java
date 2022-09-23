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
import android.widget.TextView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.adapter.BookAdapter;
import com.baontq.pnlib.dao.GenreDao;
import com.baontq.pnlib.dao.BookDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class BookManagementFragment extends Fragment {
    private static final Integer SUCCESS = 0;
    private static final Integer GENRE_NOT_FOUND = 1;
    private RecyclerView rvListBook;
    private FloatingActionButton fabAddBook;
    private BookDao bookDao;
    private GenreDao genreDao;
    private BookAdapter bookAdapter;
    private TextView tvListStatus;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvListBook = view.findViewById(R.id.rv_list_book);
        fabAddBook = view.findViewById(R.id.fab_add_book);
        tvListStatus = view.findViewById(R.id.tv_list_status);
        bookDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().bookDao();
        genreDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().genreDao();
        rvListBook.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getList();
        fabAddBook.setOnClickListener(view1 -> {
            bookAdapter.addItem();
            checkEmpty();
        });
    }

    private void getList() {
        class GetListTask extends AsyncTask<Void, Void, Integer> {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected Integer doInBackground(Void... voids) {
                if (genreDao.getAll().isEmpty()) {
                    return GENRE_NOT_FOUND;
                }
                bookAdapter = new BookAdapter(requireContext(), bookDao.getAll(), genreDao.getAll(), BookManagementFragment.this::checkEmpty);
                return SUCCESS;
            }

            @Override
            protected void onPostExecute(Integer status) {
                super.onPostExecute(status);
                if (status.equals(GENRE_NOT_FOUND)) {
                    tvListStatus.setText(R.string.list_book_genre_empty);
                    fabAddBook.setVisibility(View.INVISIBLE);
                } else {
                    rvListBook.setAdapter(bookAdapter);
                    checkEmpty();
                }
            }
        }
        GetListTask getListTask = new GetListTask();
        getListTask.execute();
    }

    private void checkEmpty() {
        if (bookAdapter.getItemCount() != 0) {
            tvListStatus.setVisibility(View.GONE);
        } else {
            tvListStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_management, container, false);
    }
}