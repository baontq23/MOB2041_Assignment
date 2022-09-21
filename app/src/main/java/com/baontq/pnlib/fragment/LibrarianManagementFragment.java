package com.baontq.pnlib.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.baontq.pnlib.R;
import com.baontq.pnlib.adapter.LibrarianAdapter;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.interfaces.HandleLibrarianItem;
import com.baontq.pnlib.model.Librarian;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;


public class LibrarianManagementFragment extends Fragment {
    private RecyclerView rvListLibrarian;
    private LibrarianAdapter librarianAdapter;
    private List<Librarian> librarianList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvListLibrarian = view.findViewById(R.id.rv_list_librarian);
        rvListLibrarian.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        getListLibrarian();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_librarian_management, container, false);

    }

    private void getListLibrarian() {
        class GetLibrarians extends AsyncTask<Void, Void, List<Librarian>> {

            @Override
            protected List<Librarian> doInBackground(Void... voids) {
                librarianList = DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                        .librarianDao().getAll();
                return librarianList;
            }

            @Override
            protected void onPostExecute(List<Librarian> librarians) {
                super.onPostExecute(librarians);
                librarianAdapter = new LibrarianAdapter(librarians, LibrarianManagementFragment.this::openMenu);
                rvListLibrarian.setAdapter(librarianAdapter);
            }
        }
        GetLibrarians getLibrariansTask = new GetLibrarians();
        getLibrariansTask.execute();
    }

    @SuppressLint("RestrictedApi")
    private void openMenu(View view, int position) {
        MenuBuilder menuBuilder = new MenuBuilder(requireContext());
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(R.menu.option_librarian, menuBuilder);
        MenuPopupHelper optionMenu = new MenuPopupHelper(requireContext(), menuBuilder, view);
        optionMenu.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_edit:
                        showUpdateDialog(librarianList.get(position));
                        return true;
                    case R.id.option_delete:
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {
            }
        });
        optionMenu.show();
    }

    private void showUpdateDialog(Librarian librarian) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_librarian_update_dialog);
        TextInputLayout tilDialogFullName, tilDialogUsername, tilDialogPassword;
        TextInputEditText edtDialogFullName, edtDialogUsername, edtDialogPassword;
        MaterialButton btnDialogClose, btnDialogUpdate;
        tilDialogFullName = dialog.findViewById(R.id.til_dialog_full_name);
        edtDialogFullName = dialog.findViewById(R.id.edt_dialog_full_name);
        tilDialogUsername = dialog.findViewById(R.id.til_dialog_username);
        edtDialogUsername = dialog.findViewById(R.id.edt_dialog_username);
        tilDialogPassword = dialog.findViewById(R.id.til_dialog_password);
        edtDialogPassword = dialog.findViewById(R.id.edt_dialog_password);
        btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogUpdate = dialog.findViewById(R.id.btn_dialog_update);
        edtDialogFullName.setText(librarian.getFullName());
        edtDialogUsername.setText(librarian.getUsername());
        btnDialogClose.setOnClickListener(view -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        btnDialogUpdate.setOnClickListener(view -> {
            librarian.setFullName(edtDialogFullName.getText().toString().trim());
            librarian.setUsername(edtDialogUsername.getText().toString().trim());
            if (TextUtils.isEmpty(librarian.getFullName())) {
                tilDialogFullName.setError("Chưa nhập họ tên");
                return;
            }else {
                tilDialogFullName.setErrorEnabled(false);
            }
            if (TextUtils.isEmpty(librarian.getUsername())) {
                tilDialogUsername.setError("Chưa nhập username");
                return;
            }else {
                tilDialogUsername.setErrorEnabled(false);
            }
        });

        class UpdateLibrarianTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
            }
        }
    }
}