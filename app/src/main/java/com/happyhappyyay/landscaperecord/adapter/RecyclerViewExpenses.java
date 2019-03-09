package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Expense;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;
import java.util.Locale;

public class RecyclerViewExpenses extends Adapter implements DatabaseAccess<Expense> {
    private List<Expense> expenses;
    private Context context;
    private Expense expense;

    public RecyclerViewExpenses(Context context, List<Expense> expenses) {
        this.expenses = expenses;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_expenses_item, parent, false);
        return new RecyclerViewExpenses.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewExpenses.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String createLogInfo() {
        if(expense != null) {
            return expense.getName();
        }
        return "";
    }

    @Override
    public void onPostExecute(List<Expense> databaseObjects) {
        expense = null;
        notifyDataSetChanged();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView date, name, price, type;
        private Button delete;

        private ListViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.view_expenses_item_date);
            name = view.findViewById(R.id.view_expenses_item_name);
            price = view.findViewById(R.id.view_expenses_item_price);
            type = view.findViewById(R.id.view_expenses_item_type);
            delete = view.findViewById(R.id.view_expenses_item_delete);

        }

        public void bindView(final int position) {
            final Expense expense = expenses.get(position);
            delete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            RecyclerViewExpenses.this.expense = expense;
                                            expenses.remove(position);
                                            Util.deleteObject(RecyclerViewExpenses.this,Util.EXPENSE_REFERENCE,expense);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.recycler_view_expenses_delete) + " " + expense.getName() + " ?")
                                    .setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                                    .setNegativeButton(context.getString(R.string.no), dialogClickListener).show();
                        }
                    });
            date.setText(Util.convertLongToStringDate(expense.getDate()));
            name.setText(expense.getName());
            price.setText(String.format(Locale.US,"%.2f", expense.getPrice()));
            type.setText(expense.getExpenseType());
        }
    }
}
