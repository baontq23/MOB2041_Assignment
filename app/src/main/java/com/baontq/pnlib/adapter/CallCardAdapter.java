package com.baontq.pnlib.adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.CallCardDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.interfaces.HandleCallCardItem;
import com.baontq.pnlib.model.Book;
import com.baontq.pnlib.model.CallCard;
import com.baontq.pnlib.model.Customer;
import com.baontq.pnlib.model.Genre;
import com.baontq.pnlib.model.Librarian;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CallCardAdapter extends RecyclerView.Adapter<CallCardAdapter.CallCardVH> {
    private static final Integer SUCCESS = 0;
    private static final Integer FAILED = 1;
    private Context mContext;
    private List<CallCard> mListCards;
    private List<Book> mListBooks;
    private List<Librarian> mListLibrarians;
    private List<Customer> mListCustomers;
    private List<Genre> mListGenres;
    private HandleCallCardItem handleCallCardItem;
    private CallCardDao callCardDao;
    private Map<Integer, String> genreMap;
    private Map<Integer, Book> bookMap;
    private Map<Integer, Customer> customerMap;
    private Map<Integer, Librarian> librarianMap;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CallCardAdapter(Context mContext, List<CallCard> mListCards, List<Book> mListBooks, List<Librarian> mListLibrarians, List<Customer> mListCustomers, List<Genre> mListGenres, HandleCallCardItem handleCallCardItem) {
        this.mContext = mContext;
        this.mListCards = mListCards;
        this.mListBooks = mListBooks;
        this.mListLibrarians = mListLibrarians;
        this.mListCustomers = mListCustomers;
        this.mListGenres = mListGenres;
        this.handleCallCardItem = handleCallCardItem;
        callCardDao = DatabaseClient.getInstance(mContext).getAppDatabase().callCardDao();
        genreMap = mListGenres.stream().collect(Collectors.toMap(Genre::getId, Genre::getName));
        bookMap = mListBooks.stream().collect(Collectors.toMap(Book::getId, Function.identity()));
        customerMap = mListCustomers.stream().collect(Collectors.toMap(Customer::getId, Function.identity()));
        librarianMap = mListLibrarians.stream().collect(Collectors.toMap(Librarian::getId, Function.identity()));
    }

    @NonNull
    @Override
    public CallCardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallCardVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_call_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CallCardVH holder, int position) {
        CallCard callCard = mListCards.get(position);
        holder.tvCardItemId.setText("Mã phiếu: " + callCard.getId());
        holder.tvCardItemCustomer.setText(customerMap.get(callCard.getCustomerId()).getName());
        holder.tvCardItemPrice.setText(String.format("Giá bìa: %.0f", bookMap.get(callCard.getBookId()).getPrice()));
        if (callCard.isReturned()) {
            holder.tvBookItemStatusTime.setText("Đã trả vào ngày: " + callCard.getReturnTime());
        } else {
            holder.tvBookItemStatusTime.setText("Đã mượn vào ngày: " + callCard.getBorrowTime());
        }
        holder.tvBookItemRecentLibrarian.setText("Thủ thư thực hiện: " + librarianMap.get(callCard.getLibrarianId()).getFullName());
    }

    @Override
    public int getItemCount() {
        return mListCards == null ? 0 : mListCards.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addItem() {
        Calendar calendar = Calendar.getInstance();
        CallCard callCard = new CallCard();
        callCard.setLibrarianId(mContext.getSharedPreferences("session", Context.MODE_PRIVATE).getInt(mContext.getString(R.string.session_data_id), 1));
        callCard.setBookId(mListBooks.get(0).getId());
        callCard.setCustomerId(mListCustomers.get(0).getId());
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.layout_dialog_call_card);
        TextView tvDialogTitle;
        Spinner snDialogBook, snDialogCustomer;
        TextInputLayout edtLayoutNewCallCardTime;
        TextInputEditText edtDialogCallCardTime;
        SwitchMaterial swDialogIsReturned;
        MaterialButton btnDialogClose, btnDialogSubmit;

        tvDialogTitle = dialog.findViewById(R.id.tv_dialog_title);
        snDialogBook = dialog.findViewById(R.id.sn_dialog_book);
        snDialogCustomer = dialog.findViewById(R.id.sn_dialog_customer);
        edtLayoutNewCallCardTime = dialog.findViewById(R.id.edt_layout_new_call_card_time);
        edtDialogCallCardTime = dialog.findViewById(R.id.edt_dialog_call_card_time);
        swDialogIsReturned = dialog.findViewById(R.id.sw_dialog_is_returned);
        btnDialogClose = dialog.findViewById(R.id.btn_dialog_close);
        btnDialogSubmit = dialog.findViewById(R.id.btn_dialog_submit);
        edtLayoutNewCallCardTime.setEndIconOnClickListener(view -> {
            new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    edtDialogCallCardTime.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    callCard.setBorrowTime(dateFormatter.format(selectedDate));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        snDialogBook.setAdapter(new ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, mListBooks));
        snDialogBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                callCard.setBookId(mListBooks.get(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        snDialogCustomer.setAdapter(new ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, mListCustomers));
        snDialogCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                callCard.setCustomerId(mListCustomers.get(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDialogClose.setOnClickListener(view -> dialog.dismiss());
        btnDialogSubmit.setOnClickListener(view -> {
            if (TextUtils.isEmpty(callCard.getBorrowTime())) {
                edtLayoutNewCallCardTime.setError("Chưa chọn ngày");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        edtLayoutNewCallCardTime.setErrorEnabled(false);
                    }
                }, 2000);
                return;
            }

            class AddCallCardTask extends AsyncTask<Void, Void, Integer> {

                @Override
                protected Integer doInBackground(Void... voids) {
                    if (callCardDao.store(callCard) > 0) {
                        mListCards = callCardDao.listAll();
                        return SUCCESS;
                    }
                    return FAILED;
                }

                @Override
                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);
                    if (result.equals(SUCCESS)) {
                        notifyItemInserted(mListCards.size());
                        Toast.makeText(mContext, "Tạo phiếu mượn thành công!", Toast.LENGTH_SHORT).show();
                        handleCallCardItem.listSizeChangeListener(mListCards.size());
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            AddCallCardTask addCallCardTask = new AddCallCardTask();
            addCallCardTask.execute();
        });
        dialog.show();

    }

    static class CallCardVH extends RecyclerView.ViewHolder {
        CardView cvImage;
        TextView tvCardItemId, tvCardItemCustomer, tvCardItemPrice, tvBookItemStatusTime, tvBookItemRecentLibrarian;
        ImageButton ibDeleteCard;

        public CallCardVH(@NonNull View itemView) {
            super(itemView);

            cvImage = itemView.findViewById(R.id.cv_image);
            tvCardItemId = itemView.findViewById(R.id.tv_card_item_id);
            tvCardItemCustomer = itemView.findViewById(R.id.tv_card_item_customer);
            tvCardItemPrice = itemView.findViewById(R.id.tv_card_item_price);
            ibDeleteCard = itemView.findViewById(R.id.ib_delete_card);
            tvBookItemStatusTime = itemView.findViewById(R.id.tv_book_item_status_time);
            tvBookItemRecentLibrarian = itemView.findViewById(R.id.tv_book_item_recent_librarian);

        }
    }
}
