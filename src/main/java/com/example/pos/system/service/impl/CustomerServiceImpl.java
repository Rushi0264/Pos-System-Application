package com.example.pos.system.service.impl;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.CustomerMapper;
import com.example.pos.system.modal.Customer;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.CustomerDTO;
import com.example.pos.system.repository.CustomerRepository;
import com.example.pos.system.service.CustomerService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws Exception {

        User user = userService.getCurrentUser();
        checkCreateAuthority(user);

        Customer customer = CustomerMapper.toEntity(customerDTO);

        if(user.getRole()!=UserRole.ROLE_SUPER_ADMIN){

            if(user.getStore()==null){
                throw new UserException("Store not assigned");
            }

            customer.setStore(user.getStore());
            customer.setBranch(user.getBranch());
        }

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerMapper.toDTO(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) throws Exception {

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new Exception("Customer not found"));

        User user = userService.getCurrentUser();

        checkAuthority(user, existing);

        existing.setFullName(customerDTO.getFullName());
        existing.setEmail(customerDTO.getEmail());
        existing.setPhone(customerDTO.getPhone());
        existing.setAddress(customerDTO.getAddress());

        Customer updatedCustomer = customerRepository.save(existing);

        return CustomerMapper.toDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) throws Exception {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new Exception("Customer not found"));

        User user = userService.getCurrentUser();

        checkAuthority(user, customer);

        customerRepository.delete(customer);
    }

    @Override
    public CustomerDTO getCustomer(Long id) throws Exception {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new Exception("Customer not found"));

        User user = userService.getCurrentUser();

        checkAuthority(user, customer);

        return CustomerMapper.toDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() throws Exception {

        User user = userService.getCurrentUser();

        List<Customer> customers;

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {

            customers = customerRepository.findAll();

        } else {

                if(user.getStore()==null){
                    throw new UserException("Store not assigned");
                }

                customers = customerRepository.findByStoreId(
                        user.getStore().getId()
                );

        }

        return customers.stream()
                .map(CustomerMapper::toDTO)
                .toList();
    }

    @Override
    public List<CustomerDTO> searchCustomer(String keyword) throws Exception {

        User user = userService.getCurrentUser();

        List<Customer> customers;

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {

            customers = customerRepository
                    .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword,
                            keyword
                    );

        } else {

            customers = customerRepository.searchByStore(
                    user.getStore().getId(),
                    keyword
            );
        }

        return customers.stream()
                .map(CustomerMapper::toDTO)
                .toList();
    }

    private void checkAuthority(User user, Customer customer)
            throws UserException {


        if(user.getRole()==UserRole.ROLE_SUPER_ADMIN){
            return;
        }


        if(customer.getStore()==null ||
                user.getStore()==null ||
                !customer.getStore().getId()
                        .equals(user.getStore().getId())){

            throw new UserException("Access Denied");
        }


        if(customer.getBranch()==null ||
                user.getBranch()==null ||
                !customer.getBranch().getId()
                        .equals(user.getBranch().getId())){

            throw new UserException("Branch Access Denied");
        }

    }

    private void checkCreateAuthority(User user) throws UserException {

        if(user.getRole() == UserRole.ROLE_SUPER_ADMIN){
            return;
        }

        if(user.getRole() == UserRole.ROLE_STORE_ADMIN){
            return;
        }

        if(user.getRole() == UserRole.ROLE_BRANCH_MANAGER){
            return;
        }

        if(user.getRole() == UserRole.ROLE_BRANCH_CASHIER){
            return;
        }


        throw new UserException(
                "You don't have permission to create customer"
        );
    }
}