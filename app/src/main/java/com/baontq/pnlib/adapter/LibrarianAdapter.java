package com.baontq.pnlib.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.LibrarianDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.interfaces.HandleLibrarianItem;
import com.baontq.pnlib.model.Librarian;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class LibrarianAdapter extends RecyclerView.Adapter<LibrarianAdapter.LibrarianVH> {
    private Context mContext;
    private List<Librarian> mList;
    private LibrarianDao librarianDao;
    private final int SUCCESS = 0;
    private final int FAILED = 1;

    public LibrarianAdapter(Context context, List<Librarian> mList) {
        this.mList = mList;
        this.mContext = context;
        librarianDao = DatabaseClient.getInstance(mContext).getAppDatabase().librarianDao();
    }

    public void addItem(Librarian librarian) {

        class AddLibrarianTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                if (librarianDao.store(librarian) > 0) {
                    mList.clear();
                    mList = librarianDao.getAll();
                    return SUCCESS;
                }
                return FAILED;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (result == SUCCESS) {
                    notifyItemInserted(mList.size());
                    Toast.makeText(mContext, "Thêm thủ thư thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        AddLibrarianTask addLibrarianTask = new AddLibrarianTask();
        addLibrarianTask.execute();
    }

    private void deleteItem(int position) {
        class DeleteLibrarianTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                if (librarianDao.delete(mList.get(position)) > 0) {
                    mList.clear();
                    mList = librarianDao.getAll();
                    return SUCCESS;
                }
                return FAILED;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (integer == SUCCESS) {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size());
                    Toast.makeText(mContext, "Đã xoá thủ thư!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Đã xảy ra lỗi khi xoá!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        DeleteLibrarianTask deleteLibrarianTask = new DeleteLibrarianTask();
        deleteLibrarianTask.execute();
    }

    @NonNull
    @Override
    public LibrarianVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibrarianVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_librarian, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull LibrarianVH holder, int position) {
        Librarian librarian = mList.get(position);
        String[] nameSplit = librarian.getFullName().split(" ");
        String avatarText = nameSplit[0].split("")[0] + nameSplit[nameSplit.length - 1].split("")[0];
        holder.tvLibrarianItemAvatarText.setText(avatarText.toUpperCase());
        holder.tvLibrarianItemFullName.setText(librarian.getFullName());
        holder.tvLibrarianItemUsername.setText(librarian.getUsername());
        holder.ibMoreInfo.setOnClickListener(view -> openMenu(view, position));
        holder.itemView.setOnClickListener(view -> showUpdateDialog(position,mList.get(position)));
    }

    @SuppressLint("RestrictedApi")
    private void openMenu(View view, int position) {
        MenuBuilder menuBuilder = new MenuBuilder(mContext);
        MenuInflater inflater = new MenuInflater(mContext);
        inflater.inflate(R.menu.option_librarian, menuBuilder);
        MenuPopupHelper optionMenu = new MenuPopupHelper(mContext, menuBuilder, view);
        optionMenu.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_edit:
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(25, 0, 25, 0);
                        EditText input = new EditText(mContext);
                        input.setLayoutParams(lp);
                        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        RelativeLayout container = new RelativeLayout(mContext);
                        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        container.setLayoutParams(rlParams);
                        container.addView(input);
                        new MaterialAlertDialogBuilder(mContext)
                                .setTitle("Đổi mật khẩu")
                                .setMessage("Nhập mật khẩu mới cho " + mList.get(position).getFullName())
                                .setView(container)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                                    if (TextUtils.isEmpty(input.getText().toString().trim())) {
                                        Toast.makeText(mContext, "Chưa nhập mật khẩu mới!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Librarian librarian = mList.get(position);
                                    librarian.setPassword(input.getText().toString().trim());
                                    updateItem(position, librarian);
                                }).show();
//                        showUpdateDialog(mList.get(position));
                        return true;
                    case R.id.option_delete:
                        new MaterialAlertDialogBuilder(mContext)
                                .setTitle(R.string.notification)
                                .setMessage("Xác nhận xoá thủ thư " + mList.get(position).getFullName() + " ?")
                                .setIcon(R.drawable.ic_outline_delete_24)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                                    deleteItem(position);
                                }).show();

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

    private void showUpdateDialog(int position, Librarian librarian) {
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_librarian_update_dialog);
        TextInputLayout tilDialogFullName, tilDialogUsername;
        TextInputEditText edtDialogFullName, edtDialogUsername;
        MaterialButton btnDialogClose, btnDialogUpdate;
        tilDialogFullName = dialog.findViewById(R.id.til_dialog_full_name);
        edtDialogFullName = dialog.findViewById(R.id.edt_dialog_full_name);
        tilDialogUsername = dialog.findViewById(R.id.til_dialog_username);
        edtDialogUsername = dialog.findViewById(R.id.edt_dialog_username);
        dialog.findViewById(R.id.til_dialog_password).setVisibility(View.INVISIBLE);
        dialog.findViewById(R.id.edt_dialog_password).setVisibility(View.INVISIBLE);
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
            } else {
                tilDialogFullName.setErrorEnabled(false);
            }
            if (TextUtils.isEmpty(librarian.getUsername())) {
                tilDialogUsername.setError("Chưa nhập username");
                return;
            } else {
                tilDialogUsername.setErrorEnabled(false);
            }
            updateItem(position, librarian);
            dialog.dismiss();
        });

    }

    public void updateItem(int position, Librarian librarian) {
        class UpdateLibrarianTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... librarians) {
                if (librarianDao.update(librarian) > 0) {
                    mList.clear();
                    mList = librarianDao.getAll();
                    return SUCCESS;
                }
                return FAILED;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (integer == SUCCESS) {
                    notifyItemChanged(position);
                    Toast.makeText(mContext, "Đã đổi thông tin của thủ thư " + librarian.getFullName(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
        UpdateLibrarianTask updateLibrarianTask = new UpdateLibrarianTask();
        updateLibrarianTask.execute();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class LibrarianVH extends RecyclerView.ViewHolder {
        TextView tvLibrarianItemAvatarText, tvLibrarianItemUsername, tvLibrarianItemFullName;
        ImageButton ibMoreInfo;

        public LibrarianVH(@NonNull View itemView) {
            super(itemView);
            tvLibrarianItemAvatarText = itemView.findViewById(R.id.tv_librarian_item_avatar_text);
            tvLibrarianItemFullName = itemView.findViewById(R.id.tv_librarian_item_full_name);
            tvLibrarianItemUsername = itemView.findViewById(R.id.tv_librarian_item_username);
            ibMoreInfo = itemView.findViewById(R.id.ib_more_info);
        }
    }
}
