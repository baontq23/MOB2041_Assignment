package com.baontq.pnlib.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.GenreDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Genre;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreVH> {
    private Context mContext;
    private List<Genre> mList;
    private GenreDao genreDao;
    private final int SUCCESS = 0;
    private final int FAILED = 1;

    public GenreAdapter(Context mContext, List<Genre> mList) {
        this.mContext = mContext;
        this.mList = mList;
        genreDao = DatabaseClient.getInstance(mContext).getAppDatabase().genreDao();
    }

    @NonNull
    @Override
    public GenreVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GenreVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_genre, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenreVH holder, int position) {
        Genre genre = mList.get(position);
        holder.tvGenreName.setText(genre.getName());
        holder.tvGenreLocation.setText(genre.getLocation());
        holder.ibDelete.setOnClickListener(view -> {
            showDeleteGenreDialog(position);
        });
        holder.itemView.setOnClickListener(view -> showUpdateGenreDialog(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public synchronized void addItem(Genre genre) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (genreDao.insert(genre) > 0) {
                    mList.clear();
                    mList = genreDao.getAll();
                } else {
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        notifyItemInserted(mList.size());
        Toast.makeText(mContext, "Đã thêm thể loại!", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteGenreDialog(int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Thông báo")
                .setMessage("Xác nhận xoá thể loại " + mList.get(position).getName() + " và các dữ liệu liên quan ?")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    deleteItem(position);
                }).show();
    }

    public void deleteItem(int position) {
        class DeleteItemTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                if (genreDao.delete(mList.get(position)) > 0) {
                    mList.clear();
                    mList = genreDao.getAll();
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
                    Toast.makeText(mContext, "Xoá thể loại thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Không thể xoá, có thể còn sách hoặc phiếu mượn nào đó đang tồn tại thể loại này !", Toast.LENGTH_SHORT).show();
                }
            }
        }
        DeleteItemTask deleteItemTask = new DeleteItemTask();
        deleteItemTask.execute();
    }

    public void updateItem(int position, Genre genre) {
        class UpdateGenreTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                if (genreDao.update(genre) > 0) {
                    mList.clear();
                    mList = genreDao.getAll();
                    return SUCCESS;
                }
                return FAILED;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (result == SUCCESS) {
                    notifyItemChanged(position);
                    Toast.makeText(mContext, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, "Đã xảy ra lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        UpdateGenreTask updateGenreTask = new UpdateGenreTask();
        updateGenreTask.execute();
    }

    private void showUpdateGenreDialog(int position) {
        Genre genre = mList.get(position);
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_genre_dialog);
        TextInputLayout tilDialogGenreName, tilDialogGenreLocation;
        TextInputEditText edtDialogGenreName, edtDialogGenreLocation;
        MaterialButton btnDialogClose, btnDialogSubmit;
        TextView dialogTitle = dialog.findViewById(R.id.tv_dialog_title);
        dialogTitle.setText("Cập nhật thể loại");
        tilDialogGenreName = dialog.findViewById(R.id.til_dialog_genre_name);
        edtDialogGenreName = dialog.findViewById(R.id.edt_dialog_genre_name);
        tilDialogGenreLocation = dialog.findViewById(R.id.til_dialog_genre_location);
        edtDialogGenreLocation = dialog.findViewById(R.id.edt_dialog_genre_location);
        btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogSubmit = dialog.findViewById(R.id.btn_dialog_submit);
        btnDialogSubmit.setText("Cập nhật");
        edtDialogGenreName.setText(genre.getName());
        edtDialogGenreLocation.setText(genre.getLocation());
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
            updateItem(position, genre);
            dialog.dismiss();
        });

        dialog.show();

    }

    static class GenreVH extends RecyclerView.ViewHolder {
        TextView tvGenreName, tvGenreLocation;
        ImageButton ibDelete;

        public GenreVH(@NonNull View itemView) {
            super(itemView);
            tvGenreName = itemView.findViewById(R.id.tv_genre_name);
            tvGenreLocation = itemView.findViewById(R.id.tv_genre_location);
            ibDelete = itemView.findViewById(R.id.ib_delete);
        }
    }
}
