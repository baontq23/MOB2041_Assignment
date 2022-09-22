package com.baontq.pnlib.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontq.pnlib.R;
import com.baontq.pnlib.dao.CustomerDao;
import com.baontq.pnlib.database.DatabaseClient;
import com.baontq.pnlib.model.Customer;
import com.baontq.pnlib.model.Librarian;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerVH> {
    private Context mContext;
    private List<Customer> mList;
    private CustomerDao customerDao;

    private final int SUCCESS = 0;
    private final int FAILED = 1;

    public CustomerAdapter(Context mContext, List<Customer> mList) {
        this.mContext = mContext;
        this.mList = mList;
        customerDao = DatabaseClient.getInstance(mContext).getAppDatabase().customerDao();
    }

    @NonNull
    @Override
    public CustomerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_customer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerVH holder, int position) {
        Customer customer = mList.get(position);
        String[] nameSplit = customer.getName().split(" ");
        String avatarText = nameSplit.length == 1 ? nameSplit[0].split("")[0] : nameSplit[0].split("")[0] + nameSplit[nameSplit.length - 1].split("")[0];
        holder.tvCustomerItemAvatarText.setText(avatarText.toUpperCase());
        holder.tvCustomerItemFullName.setText(customer.getName());
        holder.tvCustomerItemId.setText(String.format("ID: %d", customer.getId()));
        holder.ibDeleteCustomer.setOnClickListener(view -> deleteItem(position));
        holder.itemView.setOnClickListener(view -> showUpdateDialog(position, mList.get(position)));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private void deleteItem(int position) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Thông báo")
                .setMessage("Xác nhận xoá toàn bộ phiếu mượn và thành viên " + mList.get(position).getName() + " ?")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    class DeleteCustomerTask extends AsyncTask<Void, Void, Integer> {

                        @Override
                        protected Integer doInBackground(Void... voids) {
                            if (customerDao.delete(mList.get(position)) > 0) {
                                mList.clear();
                                mList = customerDao.getAll();
                                return SUCCESS;
                            }
                            return FAILED;
                        }

                        @Override
                        protected void onPostExecute(Integer result) {
                            super.onPostExecute(result);
                            if (result == SUCCESS) {
                                Toast.makeText(mContext, "Đã xoá thành viên và toàn bộ phiếu mượn liên quan!", Toast.LENGTH_SHORT).show();
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, mList.size());
                            }
                        }
                    }

                    DeleteCustomerTask deleteCustomerTask = new DeleteCustomerTask();
                    deleteCustomerTask.execute();
                }).show();
    }

    private void showUpdateDialog(int position, Customer customer) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(25, 0, 25, 0);
        EditText input = new EditText(mContext);
        input.setLayoutParams(lp);
        input.setTransformationMethod(SingleLineTransformationMethod.getInstance());
        input.setText(customer.getName());
        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(rlParams);
        container.addView(input);
        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Cập nhật thành viên")
                .setMessage("Nhập tên mới cho thành viên")
                .setIcon(R.drawable.ic_baseline_people_24)
                .setView(container)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    if (TextUtils.isEmpty(input.getText().toString().trim())) {
                        Toast.makeText(mContext, "Chưa nhập tên thành viên!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    customer.setName(input.getText().toString().trim());
                    class UpdateCustomerDialog extends AsyncTask<Void, Void, Integer> {

                        @Override
                        protected Integer doInBackground(Void... voids) {
                            if (customerDao.update(customer) > 0) {
                                mList.set(position, customer);
                                return SUCCESS;
                            }
                            return FAILED;
                        }

                        @Override
                        protected void onPostExecute(Integer result) {
                            super.onPostExecute(result);
                            if (result == SUCCESS) {
                                Toast.makeText(mContext, "Đã cập nhật thông tin!", Toast.LENGTH_SHORT).show();
                                notifyItemChanged(position);
                            }
                        }
                    }
                    UpdateCustomerDialog updateCustomerDialog = new UpdateCustomerDialog();
                    updateCustomerDialog.execute();

                }).show();

    }

    public void addItem() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(25, 0, 25, 0);
        EditText input = new EditText(mContext);
        input.setLayoutParams(lp);
        input.setTransformationMethod(SingleLineTransformationMethod.getInstance());
        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(rlParams);
        container.addView(input);
        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Thêm thành viên")
                .setMessage("Nhập tên thành viên mới")
                .setIcon(R.drawable.ic_baseline_people_24)
                .setView(container)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    if (TextUtils.isEmpty(input.getText().toString().trim())) {
                        Toast.makeText(mContext, "Chưa nhập tên thành viên!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    class AddCustomerTask extends AsyncTask<Void, Void, Integer> {

                        @Override
                        protected Integer doInBackground(Void... voids) {
                            if (customerDao.insert(new Customer(input.getText().toString().trim())) > 0) {
                                mList.clear();
                                mList = customerDao.getAll();
                                return SUCCESS;
                            }
                            return FAILED;
                        }

                        @Override
                        protected void onPostExecute(Integer result) {
                            super.onPostExecute(result);
                            if (result == SUCCESS) {
                                notifyItemInserted(mList.size());
                                Toast.makeText(mContext, "Đã thêm thành viên!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    AddCustomerTask addCustomerTask = new AddCustomerTask();
                    addCustomerTask.execute();

                }).show();

    }

    static class CustomerVH extends RecyclerView.ViewHolder {
        TextView tvCustomerItemAvatarText, tvCustomerItemId, tvCustomerItemFullName;
        ImageButton ibDeleteCustomer;

        public CustomerVH(@NonNull View itemView) {
            super(itemView);

            tvCustomerItemAvatarText = itemView.findViewById(R.id.tv_customer_item_avatar_text);
            tvCustomerItemFullName = itemView.findViewById(R.id.tv_customer_item_full_name);
            tvCustomerItemId = itemView.findViewById(R.id.tv_customer_item_id);
            ibDeleteCustomer = itemView.findViewById(R.id.ib_delete_customer);

        }
    }

}
