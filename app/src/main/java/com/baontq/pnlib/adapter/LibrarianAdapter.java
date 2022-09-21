package com.baontq.pnlib.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.interfaces.HandleLibrarianItem;
import com.baontq.pnlib.model.Librarian;

import java.util.List;

public class LibrarianAdapter extends RecyclerView.Adapter<LibrarianAdapter.LibrarianVH> {
    private List<Librarian> mList;
    private HandleLibrarianItem librarianItemListener;

    public LibrarianAdapter(List<Librarian> mList, HandleLibrarianItem librarianItemListener) {
        this.mList = mList;
        this.librarianItemListener = librarianItemListener;
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
        String avatarText = String.valueOf(nameSplit[0].split("")[0] + nameSplit[nameSplit.length - 1].split("")[0]).toUpperCase();
        holder.tvLibrarianItemAvatarText.setText(avatarText);
        holder.tvLibrarianItemFullName.setText(librarian.getFullName());
        holder.tvLibrarianItemUsername.setText(librarian.getUsername());
        holder.ibMoreInfo.setOnClickListener(view -> librarianItemListener.openMenuContext(view,position));
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
