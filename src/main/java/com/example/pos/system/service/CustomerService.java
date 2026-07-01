package com.example.pos.system.service;

import com.example.pos.system.modal.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer) throws Exception;
    void deleteCustomer(Long id) throws Exception;
    Customer getCustomer(Long id) throws Exception;
    List<Customer> getAllCustomers() throws Exception;
    List<Customer> searchCustomer(String keyword) throws Exception;
}
