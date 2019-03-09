package com.happyhappyyay.landscaperecord.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.happyhappyyay.landscaperecord.pojo.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense... expenses);

    @Query("SELECT * FROM Expense")
    List<Expense> getAllExpenses();

    @Query("SELECT * FROM Expense WHERE id == :expenseId")
    Expense getExpenseById(String expenseId);

    @Query("SELECT * FROM Expense WHERE modifiedTime == :time")
    List<Expense> getExpenseByTime(long time);

    @Query("SELECT * FROM Expense WHERE modifiedTime > :modifiedTime")
    List<Expense> getNewlyModifiedExpenses(long modifiedTime);

    @Delete
    void deleteExpense(Expense expense);
}
