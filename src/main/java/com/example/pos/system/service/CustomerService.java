package com.example.pos.system.service;

import com.example.pos.system.payload.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO customerDTO) throws Exception;

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws Exception;

    void deleteCustomer(Long id) throws Exception;

    CustomerDTO getCustomer(Long id) throws Exception;

    List<CustomerDTO> getAllCustomers() throws Exception;

    List<CustomerDTO> searchCustomer(String keyword) throws Exception;

}