package com.baontq.pnlib.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.LibrarianDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Librarian;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class ChangeInformationFragment extends Fragment {
    private TextInputLayout tilOldPassword, tilPassword, tilRePassword;
    private TextInputEditText edtOldPassword, edtPassword, edtRePassword;
    private MaterialButton btnUpdate;
    private LibrarianDao librarianDao = null;

    private final int HANDLE_SUCCESS = 0;
    private final int OLD_PASSWORD_NOT_VALID = 1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        librarianDao = DatabaseClient.getInstance(requireContext()).getAppDatabase().librarianDao();
        tilOldPassword = view.findViewById(R.id.til_old_password);
        edtOldPassword = view.findViewById(R.id.edt_old_password);
        tilPassword = view.findViewById(R.id.til_password);
        edtPassword = view.findViewById(R.id.edt_password);
        tilRePassword = view.findViewById(R.id.til_re_password);
        edtRePassword = view.findViewById(R.id.edt_re_password);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(view1 -> handleSubmit());
    }

    private void handleSubmit() {
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtPassword.getText().toString().trim();
        String rePass = edtRePassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldPass)) {
            tilOldPassword.setError("Ch??a nh???p m???t kh???u c??!");
            return;
        } else {
            tilOldPassword.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(newPass)) {
            tilPassword.setError("Ch??a nh???p m???t kh???u m???i!");
            return;
        } else {
            tilPassword.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(rePass)) {
            tilRePassword.setError("H??y nh???p l???i m???t kh???u m???i!");
            return;
        } else {
            tilRePassword.setErrorEnabled(false);
        }
        if (!rePass.equalsIgnoreCase(newPass)) {
            tilRePassword.setError("M???t kh???u kh??ng tr??ng kh???p !");
            return;
        } else {
            tilRePassword.setErrorEnabled(false);
        }
        handleChangePass(oldPass, newPass);
    }

    private void handleChangePass(String oldPass, String newPass) {
        class ChangePassTask extends AsyncTask<Void, Void, Integer> {
            Librarian currentLibrarian = null;
            String username = getActivity().getSharedPreferences("session", Context.MODE_PRIVATE).getString(getString(R.string.session_data_username), "");

            @Override
            protected Integer doInBackground(Void... voids) {
                currentLibrarian = librarianDao.auth(username, oldPass);

                if (currentLibrarian == null) {
                    return OLD_PASSWORD_NOT_VALID;
                } else {
                    if (librarianDao.changePassword(username, newPass)> 0) {
                        return HANDLE_SUCCESS;
                    }else {
                        return OLD_PASSWORD_NOT_VALID;
                    }
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (result == OLD_PASSWORD_NOT_VALID) {
                    tilOldPassword.setError("M???t kh???u c?? kh??ng ch??nh x??c!");
                } else {
                    tilOldPassword.setErrorEnabled(false);
                    Toast.makeText(getContext(), "?????i m???t kh???u th??nh c??ng!", Toast.LENGTH_SHORT).show();
                    edtOldPassword.setText("");
                    edtPassword.setText("");
                    edtRePassword.setText("");
                    edtOldPassword.requestFocus();
                }
            }
        }
        ChangePassTask changePassTask = new ChangePassTask();
        changePassTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_infomation, container, false);
    }
}