package com.happyhappyyay.landscaperecord.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.happyhappyyay.landscaperecord.pojo.Customer;

import java.util.List;

@Dao
public interface CustomerDao {

    @Insert
    void insert(Customer... customers);

    @Query("SELECT * FROM customer")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM Customer WHERE id = :customerId")
    Customer findCustomerById(String customerId);

    @Query("SELECT * FROM Customer WHERE modifiedTime > :modifiedTime")
    List<Customer> getNewlyModifiedCustomers(long modifiedTime);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomer(Customer customer);
}
