package com.example.pos.system.service.impl;

import com.example.pos.system.domain.OrderStatus;
import com.example.pos.system.domain.PaymentStatus;
import com.example.pos.system.domain.PaymentType;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.OrderMapper;
import com.example.pos.system.modal.*;
import com.example.pos.system.payload.dto.OrderDTO;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.OrderService;
import com.example.pos.system.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;
    private final BranchRepository branchRepository;

    /**
     * Check whether logged-in user has permission
     * to access the given branch.
     */
    private void checkBranchAccess(User user, Branch branch) throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return;
        }

        if (branch == null || branch.getStore() == null) {
            throw new UserException("Branch not linked with store");
        }

        if (user.getStore() == null) {
            throw new UserException("User is not assigned to any store");
        }

        if (!branch.getStore().getId()
                .equals(user.getStore().getId())) {

            throw new UserException(
                    "You cannot access another store's orders."
            );
        }


        if(user.getRole() == UserRole.ROLE_BRANCH_CASHIER ||
                user.getRole() == UserRole.ROLE_BRANCH_MANAGER){

            if(user.getBranch() == null ||
                    !user.getBranch().getId()
                            .equals(branch.getId())){

                throw new UserException(
                        "You cannot create order for another branch"
                );
            }
        }
    }

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) throws Exception {




        User cashier = userService.getCurrentUser();

        Branch branch;

        if (cashier.getRole() == UserRole.ROLE_SUPER_ADMIN
                || cashier.getRole() == UserRole.ROLE_STORE_ADMIN) {

            if (orderDTO.getBranchId() == null) {
                throw new Exception("Please select a branch");
            }

            branch = branchRepository.findById(orderDTO.getBranchId())
                    .orElseThrow(() -> new Exception("Branch not found"));

        } else {

            branch = cashier.getBranch();

        }

        //User cashier = userService.getCurrentUser();

        System.out.println("USER : " + cashier.getEmail());
        System.out.println("ROLE : " + cashier.getRole());
        System.out.println("BRANCH : " +
                (cashier.getBranch()==null ? "NULL" : cashier.getBranch().getId()));


        if(branch == null){
            throw new Exception("Cashier branch not found");
        }


        checkBranchAccess(cashier, branch);



        Customer customer = null;


        if(orderDTO.getCustomerId()!=null){

            customer = customerRepository.findById(
                    orderDTO.getCustomerId()
            ).orElseThrow(
                    () -> new Exception("Customer not found")
            );

        }



        Order order = Order.builder()
                .branch(branch)
                .cashier(cashier)
                .customer(customer)
                .paymentType(orderDTO.getPaymentType())
                //.paymentStatus(PaymentStatus.PENDING)
                .status(OrderStatus.CREATED)
                .build();




        List<OrderItem> orderItems =
                orderDTO.getItems()
                        .stream()
                        .map(itemDto -> {


                            Product product =
                                    null;
                            try {
                                product = productRepository.findById(
                                                itemDto.getProductId()
                                        )
                                        .orElseThrow(
                                                () -> new Exception(
                                                        "Product not found"
                                                )
                                        );
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }


                            Inventory inventory =
                                    inventoryRepository
                                            .findByProductIdAndBranchId(
                                                    product.getId(),
                                                    branch.getId()
                                            );



                            if(inventory == null){

                                throw new RuntimeException(
                                        "Inventory not available for "
                                                + product.getName()
                                );

                            }



                            if(inventory.getQuantity()
                                    < itemDto.getQuantity()){


                                throw new RuntimeException(
                                        "Insufficient stock for "
                                                + product.getName()
                                );

                            }




                            // REDUCE STOCK

                            inventory.setQuantity(
                                    inventory.getQuantity()
                                            - itemDto.getQuantity()
                            );


                            inventoryRepository.save(inventory);





                            return OrderItem.builder()
                                    .product(product)
                                    .quantity(itemDto.getQuantity())
                                    .price(
                                            product.getSellingPrice()
                                    )
                                    .order(order)
                                    .build();



                        })
                        .toList();





        double subtotal = orderItems.stream()
                .mapToDouble(item ->
                        item.getPrice() * item.getQuantity()
                )
                .sum();


        double discount = 0;


        double tax = subtotal * 0.18;


        double total = subtotal + tax - discount;


        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setTaxAmount(tax);
        order.setTotalAmount(total);




        order.setItems(orderItems);

        //order.setTotalAmount(totalAmount);



        Order savedOrder =
                orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(savedOrder)
                .amount(total)
                .paymentType(orderDTO.getPaymentType())
                .status(PaymentStatus.SUCCESS) // किंवा PENDING, तुझ्या business logic नुसार
                .build();

        paymentRepository.save(payment);



        return OrderMapper.toDTO(savedOrder);

    }

    public List<OrderDTO> getAllOrders(User user) throws Exception {

        List<Order> orders;

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            orders = orderRepository.findAll();
        }
        else if (user.getRole() == UserRole.ROLE_STORE_ADMIN
                || user.getRole() == UserRole.ROLE_ACCOUNTANT) {

            if (user.getStore() == null) {
                throw new UserException("Store not assigned to user");
            }

            orders = orderRepository.findByBranch_Store_Id(user.getStore().getId());
        }
        else {

            if (user.getBranch() == null) {
                throw new UserException("Branch not assigned to user");
            }

            orders = orderRepository.findByBranchId(user.getBranch().getId());
        }

        return orders.stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    @Override
    public OrderDTO getOrderById(Long id) throws Exception {

        User user = userService.getCurrentUser();


        Order order =
                orderRepository.findOrderWithDetails(id)
                        .orElseThrow(() ->
                                new Exception("Order not found")
                        );


        checkBranchAccess(
                user,
                order.getBranch()
        );


        return OrderMapper.toDTO(order);
    }
    @Override
    public List<OrderDTO> getOrdersByBranch(
            Long branchId,
            Long customerId,
            Long cashierId,
            PaymentType paymentType,
            OrderStatus status
    ) throws Exception {

        //User currentUser = userService.getCurrentUser();
        User currentUser = userService.getCurrentUser();

        System.out.println("========== ORDER ACCESS ==========");
        System.out.println("Role: " + currentUser.getRole());
        System.out.println("User Branch: " +
                (currentUser.getBranch() == null ? "null" : currentUser.getBranch().getId()));
        System.out.println("Requested Branch: " + branchId);
        System.out.println("==================================");

        // Super Admin -> Access all branches
        if (currentUser.getRole() != UserRole.ROLE_SUPER_ADMIN) {

            if (currentUser.getBranch() == null ||
                    !currentUser.getBranch().getId().equals(branchId)) {

                throw new Exception("Access Denied");
            }
        }

        return orderRepository.findByBranchId(branchId)
                .stream()
                .filter(order ->
                        customerId == null ||
                                (order.getCustomer() != null &&
                                        order.getCustomer().getId().equals(customerId))
                )
                .filter(order ->
                        cashierId == null ||
                                (order.getCashier() != null &&
                                        order.getCashier().getId().equals(cashierId))
                )
                .filter(order ->
                        paymentType == null ||
                                order.getPaymentType() == paymentType
                )
                .filter(order ->
                        status == null ||
                                order.getStatus() == status
                )
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrderByCashier(Long cashierId) {

        return orderRepository.findByCashierId(cashierId)
                .stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(Long id) throws Exception {

        User currentUser = userService.getCurrentUser();

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new Exception("Order not found"));

        if (currentUser.getRole() != UserRole.ROLE_SUPER_ADMIN) {

            if (currentUser.getBranch() == null ||
                    !currentUser.getBranch().getId()
                            .equals(order.getBranch().getId())) {

                throw new Exception("Access Denied");
            }
        }

        orderRepository.delete(order);
    }

    @Override
    public List<OrderDTO> getTodayOrdersByBranch(Long branchId)
            throws Exception {

        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != UserRole.ROLE_SUPER_ADMIN) {

            if (currentUser.getBranch() == null ||
                    !currentUser.getBranch().getId().equals(branchId)) {

                throw new Exception("Access Denied");
            }
        }

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return orderRepository
                .findByBranchIdAndCreatedAtBetween(
                        branchId,
                        start,
                        end
                )
                .stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId)
            throws Exception {

        User currentUser = userService.getCurrentUser();

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .filter(order ->
                        currentUser.getRole() == UserRole.ROLE_SUPER_ADMIN ||
                                (currentUser.getBranch() != null &&
                                        order.getBranch().getId()
                                                .equals(currentUser.getBranch().getId()))
                )
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getTop5RecentOrdersByBranchId(Long branchId)
            throws Exception {

        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != UserRole.ROLE_SUPER_ADMIN) {

            if (currentUser.getBranch() == null ||
                    !currentUser.getBranch().getId().equals(branchId)) {

                throw new Exception("Access Denied");
            }
        }

        return orderRepository
                .findTop5ByBranchIdOrderByCreatedAtDesc(branchId)
                .stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) throws Exception {

        User currentUser = userService.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        if (status == OrderStatus.RETURNED) {
            throw new Exception(
                    "Order cannot be marked as RETURNED directly. Please create and approve a refund instead."
            );
        }

        System.out.println("========== UPDATE ORDER ==========");
        System.out.println("Logged User : " + currentUser.getEmail());
        System.out.println("Role : " + currentUser.getRole());
        System.out.println("User Branch : " +
                (currentUser.getBranch()==null ? "NULL" : currentUser.getBranch().getId()));
        System.out.println("Order Branch : " + order.getBranch().getId());

        checkBranchAccess(currentUser, order.getBranch());

        order.setStatus(status);

        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toDTO(savedOrder);
    }
}