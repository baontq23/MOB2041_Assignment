package com.baontq.pnlib.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.baontq.pnlib.MainActivity;
import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.LibrarianDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Librarian;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilLoginUsername, tilLoginPassword;
    private TextInputEditText edtLoginUsername, edtLoginPassword;
    private CheckBox cbLoginRemember;
    private MaterialButton btnLoginSubmit;
    private final String TAG = "Debug log";
    private final Integer LOGIN_SUCCESS = 0;
    private final Integer LOGIN_FAILED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        btnLoginSubmit.setOnClickListener(view -> handleLogin());

    }

    private void handleLogin() {
        Boolean isRememberSession = false;
        Librarian librarian = new Librarian();
        librarian.setUsername(edtLoginUsername.getText().toString().trim());
        librarian.setPassword(edtLoginPassword.getText().toString().trim());
        if (TextUtils.isEmpty(librarian.getUsername())) {
            tilLoginUsername.setError("Chưa nhập username");
            return;
        } else {
            tilLoginUsername.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(librarian.getPassword())) {
            tilLoginPassword.setError("Chưa nhập mật khẩu");
            return;
        } else {
            tilLoginPassword.setErrorEnabled(false);
        }
        isRememberSession = cbLoginRemember.isChecked();
        class LoginTask extends AsyncTask<Void, Void, Integer> {
            Librarian data;
            @Override
            protected Integer doInBackground(Void... voids) {
                LibrarianDao librarianDao = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .librarianDao();
                data = librarianDao.auth(librarian.getUsername(), librarian.getPassword());
                if (data == null) return LOGIN_FAILED;
                if (data.isValid()) {
                    return LOGIN_SUCCESS;
                }
                return LOGIN_FAILED;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                btnLoginSubmit.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                btnLoginSubmit.setEnabled(true);
                if (result.equals(LOGIN_SUCCESS)) {
                    SharedPreferences.Editor editor = getSharedPreferences("session", Context.MODE_PRIVATE).edit();
                    editor.putInt(getString(R.string.session_data_id), data.getId());
                    editor.putString(getString(R.string.session_data_fullname), data.getFullName());
                    editor.putString(getString(R.string.session_data_username), data.getUsername());
                    editor.putString(getString(R.string.session_data_role), data.getRole());
                    editor.putBoolean(getString(R.string.session_is_remember),  cbLoginRemember.isChecked());
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Thông tin tài khoản không chính xác!", Toast.LENGTH_SHORT).show();
                }

            }
        }
        LoginTask loginTask = new LoginTask();
        loginTask.execute();
    }

    private void findView() {
        tilLoginUsername = findViewById(R.id.til_login_username);
        edtLoginUsername = findViewById(R.id.edt_login_username);
        tilLoginPassword = findViewById(R.id.til_login_password);
        edtLoginPassword = findViewById(R.id.edt_login_password);
        cbLoginRemember = findViewById(R.id.cb_login_remember);
        btnLoginSubmit = findViewById(R.id.btn_login_submit);
    }

}