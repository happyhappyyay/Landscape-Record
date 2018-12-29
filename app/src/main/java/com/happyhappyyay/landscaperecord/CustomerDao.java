package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CustomerDao {

    @Insert
    void insert(Customer... customers);

    @Query("SELECT * FROM customer")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM Customer WHERE customerFirstName + ' ' + customerLastName OR customerBusiness = :customerName")
    Customer findCustomerByName(String customerName);

    @Query("SELECT * FROM Customer WHERE customerId = :customerId")
    Customer findCustomerById(String customerId);

    @Query("SELECT * FROM Customer WHERE modifiedTime > :modifiedTime")
    List<Customer> getNewlyModifiedCustomers(long modifiedTime);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomer(Customer customer);

    @Query("DELETE FROM customer")
    void deleteAllCustomers();

}
