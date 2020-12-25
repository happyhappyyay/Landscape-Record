package com.happyhappyyay.landscaperecord.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
