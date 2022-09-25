package com.baontq.pnlib.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.model.Book;
import com.baontq.pnlib.model.BookAnalytics;
import com.baontq.pnlib.model.CallCard;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Top10BookAdapter extends RecyclerView.Adapter<Top10BookAdapter.Top10VH> {
    private Context context;
    private List<Book> bookList;
    private int index = 1;

    public Top10BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public Top10VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Top10VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_top10_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Top10VH holder, int position) {
        switch (index) {
            case 1:
                holder.top10ItemIndex.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark, context.getTheme()));
                break;
            case 2:
                holder.top10ItemIndex.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light, context.getTheme()));
                break;
            case 3:
                holder.top10ItemIndex.setTextColor(context.getResources().getColor(android.R.color.holo_green_light, context.getTheme()));
                break;
        }
        Book book = bookList.get(position);
        holder.top10ItemIndex.setText("" + index);
        holder.top10ItemName.setText(book.getName());
        holder.top10ItemBorrowedTime.setText("Đã thuê: " + book.getBorrowCount());
        holder.top10ItemIncome.setText("Doanh thu: " + book.getBorrowCount() * book.getPrice());
        index++;
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    static class Top10VH extends RecyclerView.ViewHolder {
        TextView top10ItemBorrowedTime, top10ItemIndex, top10ItemName, top10ItemIncome;

        public Top10VH(View itemView) {
            super(itemView);
            top10ItemIndex = itemView.findViewById(R.id.top10_item_index);
            top10ItemName = itemView.findViewById(R.id.top10_item_name);
            top10ItemIncome = itemView.findViewById(R.id.top10_item_income);
            top10ItemBorrowedTime = itemView.findViewById(R.id.top10_item_borrowed_time);
        }
    }
}
