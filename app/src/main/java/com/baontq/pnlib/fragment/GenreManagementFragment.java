package com.baontq.pnlib.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baontq.pnlib.R;
import com.baontq.pnlib.adapter.GenreAdapter;
import com.baontq.pnlib.dao.GenreDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Genre;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;


public class GenreManagementFragment extends Fragment {
    private RecyclerView rvListGenre;
    private FloatingActionButton fabAddGenre;
    private GenreAdapter genreAdapter;
    private GenreDao genreDao;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvListGenre = view.findViewById(R.id.rv_list_genre);
        fabAddGenre = view.findViewById(R.id.fab_add_genre);
        genreDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().genreDao();
        rvListGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getList();
        fabAddGenre.setOnClickListener(view1 -> showAddGenreDialog());
    }

    private void getList() {
        class GetGenreList extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                genreAdapter = new GenreAdapter(requireContext(), genreDao.getAll());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                rvListGenre.setAdapter(genreAdapter);
            }
        }
        GetGenreList getGenreList = new GetGenreList();
        getGenreList.execute();
    }

    private void showAddGenreDialog() {
        Genre genre = new Genre();
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_genre_dialog);
        TextInputLayout tilDialogGenreName, tilDialogGenreLocation;
        TextInputEditText edtDialogGenreName, edtDialogGenreLocation;
        MaterialButton btnDialogClose, btnDialogSubmit;

        tilDialogGenreName = dialog.findViewById(R.id.til_dialog_genre_name);
        edtDialogGenreName = dialog.findViewById(R.id.edt_dialog_genre_name);
        tilDialogGenreLocation = dialog.findViewById(R.id.til_dialog_genre_location);
        edtDialogGenreLocation = dialog.findViewById(R.id.edt_dialog_genre_location);
        btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogSubmit = dialog.findViewById(R.id.btn_dialog_submit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDialogClose.setOnClickListener(view -> dialog.dismiss());
        btnDialogSubmit.setOnClickListener(view -> {
            genre.setLocation(edtDialogGenreLocation.getText().toString().trim());
            genre.setName(edtDialogGenreName.getText().toString().trim());

            if (TextUtils.isEmpty(genre.getName())) {
                tilDialogGenreName.setError("Chưa nhập tên thể loại");
                return;
            } else {
                tilDialogGenreName.setErrorEnabled(false);
            }
            if (TextUtils.isEmpty(genre.getLocation())) {
                tilDialogGenreLocation.setError("Chưa nhập vị tri");
                return;
            } else {
                tilDialogGenreLocation.setErrorEnabled(false);
            }
            genreAdapter.addItem(genre);
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genre_management, container, false);
    }

}
