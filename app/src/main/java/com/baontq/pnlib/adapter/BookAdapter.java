package com.baontq.pnlib.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.BookDao;
import com.baontq.pnlib.dao.GenreDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.interfaces.HandleBookItem;
import com.baontq.pnlib.model.Book;
import com.baontq.pnlib.model.Genre;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kotlin.Function;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookVH> {
    private static final int SUCCESS = 0;
    private static final int FAILED = 1;
    private Context mContext;
    private List<Book> mBookList;
    private List<Genre> mGenreList;
    private BookDao bookDao;
    private Map<Integer, String> genreMap;
    private HandleBookItem handleBookItem;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public BookAdapter(Context mContext, List<Book> mBookList, List<Genre> mGenreList, HandleBookItem handleBookItem) {
        this.mContext = mContext;
        this.mBookList = mBookList;
        this.mGenreList = mGenreList;
        this.handleBookItem = handleBookItem;
        bookDao = DatabaseClient.getInstance(mContext).getAppDatabase().bookDao();
        genreMap = mGenreList.stream().collect(Collectors.toMap(Genre::getId, Genre::getName));
    }

    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH holder, int position) {
        Book book = mBookList.get(position);
        holder.tvBookItemName.setText(book.getName());
        holder.tvBookItemGenre.setText("Thể loại: " + genreMap.get(book.getGenreId()));
        holder.tvBookItemName.setText(book.getName());
        holder.tvBookItemPrice.setText(String.format("Giá bìa: %.0f VND", book.getPrice()));
        holder.ibDeleteBook.setOnClickListener(view -> deleteItem(position));
        holder.itemView.setOnClickListener(view -> showUpdateItemDialog(position, mBookList.get(position)));
        holder.tvBookItemBorrowCount.setText("Đã bán: " + book.getBorrowCount());
    }

    @Override
    public int getItemCount() {
        return mBookList == null ? 0 : mBookList.size();
    }

    public void addItem() {
        Book book = new Book();
        book.setGenreId(mGenreList.get(0).getId());
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_dialog_book);
        TextInputLayout tilDialogBookName, tilDialogBookPrice;
        TextInputEditText edtDialogBookName, edtDialogBookPrice;
        Spinner snDialogGenre;
        MaterialButton btnDialogClose, btnDialogSubmit;
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mGenreList);
        tilDialogBookName = dialog.findViewById(R.id.til_dialog_book_name);
        edtDialogBookName = dialog.findViewById(R.id.edt_dialog_book_name);
        snDialogGenre = dialog.findViewById(R.id.sn_dialog_genre);
        tilDialogBookPrice = dialog.findViewById(R.id.til_dialog_book_price);
        edtDialogBookPrice = dialog.findViewById(R.id.edt_dialog_book_price);
        snDialogGenre.setAdapter(genreAdapter);
        edtDialogBookName.requestFocus();
        snDialogGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                book.setGenreId(mGenreList.get(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogSubmit = dialog.findViewById(R.id.btn_dialog_submit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDialogClose.setOnClickListener(view -> dialog.dismiss());
        btnDialogSubmit.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edtDialogBookName.getText().toString().trim())) {
                tilDialogBookName.setError("Chưa nhập tên sách");
                return;
            } else {
                tilDialogBookName.setErrorEnabled(false);
                book.setName(edtDialogBookName.getText().toString().trim());
            }
            if (TextUtils.isEmpty(edtDialogBookPrice.getText().toString().trim())) {
                tilDialogBookPrice.setError("Chưa nhập giá sách");
                return;
            } else {
                tilDialogBookPrice.setErrorEnabled(false);
                book.setPrice(Double.parseDouble(edtDialogBookPrice.getText().toString().trim()));
            }
            class AddBookTask extends AsyncTask<Void, Void, Integer> {

                @Override
                protected Integer doInBackground(Void... voids) {
                    if (bookDao.insert(book) > 0) {
                        mBookList.clear();
                        mBookList = bookDao.getAll();
                        return SUCCESS;
                    }
                    return FAILED;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);
                    if (result == SUCCESS) {
                        notifyItemInserted(mBookList.size());
                        Toast.makeText(mContext, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                        handleBookItem.listSizeChangeListener();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            AddBookTask addBookTask = new AddBookTask();
            addBookTask.execute();
        });


        dialog.show();

    }

    private void deleteItem(int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setIcon(R.drawable.ic_outline_delete_24)
                .setTitle("Thông báo")
                .setMessage("Xác nhận xoá sách " + mBookList.get(position).getName() + " và toàn bộ phiếu mượn liên quan ?")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    class DeleteBookTask extends AsyncTask<Void, Void, Integer> {

                        @Override
                        protected Integer doInBackground(Void... voids) {
                            if (bookDao.delete(mBookList.get(position)) > 0) {
                                mBookList.remove(position);
                                return SUCCESS;
                            }
                            return FAILED;
                        }

                        @Override
                        protected void onPostExecute(Integer result) {
                            super.onPostExecute(result);
                            if (result == SUCCESS) {
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, mBookList.size());
                                Toast.makeText(mContext, "Đã xoá thành công!", Toast.LENGTH_SHORT).show();
                                handleBookItem.listSizeChangeListener();
                            } else {
                                Toast.makeText(mContext, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    DeleteBookTask deleteBookTask = new DeleteBookTask();
                    deleteBookTask.execute();
                }).show();
    }

    private void showUpdateItemDialog(int position, Book book) {
        int spinnerIndex = 0;
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_dialog_book);
        TextInputLayout tilDialogBookName, tilDialogBookPrice;
        TextInputEditText edtDialogBookName, edtDialogBookPrice;
        TextView tvDialogTitle;
        tvDialogTitle = dialog.findViewById(R.id.tv_dialog_title);
        Spinner snDialogGenre;
        tvDialogTitle.setText("Cập nhật sách");
        MaterialButton btnDialogClose, btnDialogSubmit;
        ArrayAdapter<Genre> genreAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mGenreList);
        tilDialogBookName = dialog.findViewById(R.id.til_dialog_book_name);
        edtDialogBookName = dialog.findViewById(R.id.edt_dialog_book_name);
        snDialogGenre = dialog.findViewById(R.id.sn_dialog_genre);
        tilDialogBookPrice = dialog.findViewById(R.id.til_dialog_book_price);
        edtDialogBookPrice = dialog.findViewById(R.id.edt_dialog_book_price);
        snDialogGenre.setAdapter(genreAdapter);
        edtDialogBookName.setText(book.getName());
        edtDialogBookPrice.setText(String.valueOf(book.getPrice()));
        edtDialogBookName.requestFocus();
        snDialogGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                book.setGenreId(mGenreList.get(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        for (Genre genre : mGenreList) {
            if (genre.getId() == book.getGenreId()) {
                snDialogGenre.setSelection(spinnerIndex);
                break;
            }
            spinnerIndex++;
        }
        btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogSubmit = dialog.findViewById(R.id.btn_dialog_submit);
        btnDialogSubmit.setText(R.string.update);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDialogClose.setOnClickListener(view -> dialog.dismiss());
        btnDialogSubmit.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edtDialogBookName.getText().toString().trim())) {
                tilDialogBookName.setError("Chưa nhập tên sách");
                return;
            } else {
                tilDialogBookName.setErrorEnabled(false);
                book.setName(edtDialogBookName.getText().toString().trim());
            }
            if (TextUtils.isEmpty(edtDialogBookPrice.getText().toString().trim())) {
                tilDialogBookPrice.setError("Chưa nhập giá sách");
                return;
            } else {
                tilDialogBookPrice.setErrorEnabled(false);
                book.setPrice(Double.parseDouble(edtDialogBookPrice.getText().toString().trim()));
            }
            class UpdateBookTask extends AsyncTask<Void, Void, Integer> {

                @Override
                protected Integer doInBackground(Void... voids) {
                    if (bookDao.update(book) > 0) {
                        mBookList.set(position, book);
                        return SUCCESS;
                    }
                    return FAILED;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);
                    if (result == SUCCESS) {
                        notifyItemChanged(position);
                        Toast.makeText(mContext, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            UpdateBookTask addBookTask = new UpdateBookTask();
            addBookTask.execute();
        });


        dialog.show();

    }

    static class BookVH extends RecyclerView.ViewHolder {
        TextView tvBookItemName, tvBookItemGenre, tvBookItemPrice,tvBookItemBorrowCount;
        ImageButton ibDeleteBook;

        public BookVH(@NonNull View itemView) {
            super(itemView);
            tvBookItemName = itemView.findViewById(R.id.tv_book_item_name);
            tvBookItemGenre = itemView.findViewById(R.id.tv_book_item_genre);
            tvBookItemPrice = itemView.findViewById(R.id.tv_book_item_price);
            ibDeleteBook = itemView.findViewById(R.id.ib_delete_book);
            tvBookItemBorrowCount = itemView.findViewById(R.id.tv_book_item_borrow_count);
        }
    }

}
