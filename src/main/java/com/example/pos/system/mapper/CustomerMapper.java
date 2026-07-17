package com.example.pos.system.mapper;

import com.example.pos.system.modal.Customer;
import com.example.pos.system.payload.dto.CustomerDTO;

public class CustomerMapper {


    public static CustomerDTO toDTO(Customer customer){

        return CustomerDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .storeId(
                        customer.getStore()!=null?
                                customer.getStore().getId():null
                )
                .branchId(
                        customer.getBranch()!=null?
                                customer.getBranch().getId():null
                )
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }


    public static Customer toEntity(CustomerDTO dto){

        Customer customer = new Customer();

        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());

        return customer;
    }

}