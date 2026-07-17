package com.example.pos.system.service;

import com.example.pos.system.domain.OrderStatus;
import com.example.pos.system.domain.PaymentType;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO) throws Exception;

    //OrderDTO createOrder(OrderDTO orderDTO, User user) throws Exception;

    OrderDTO getOrderById(Long id) throws Exception;

    //OrderDTO getOrderById(Long id, User user) throws Exception;

    List<OrderDTO> getOrdersByBranch(
            Long branchId,
            Long customerId,
            Long cashierId,
            PaymentType paymentType,
            OrderStatus status
    ) throws Exception;
    public List<OrderDTO> getAllOrders(User user) throws Exception;
    /*List<OrderDTO> getOrdersByBranch(
            Long branchId,
            Long customerId,
            Long cashierId,
            PaymentType paymentType,
            OrderStatus status,
            User user
    ) throws Exception;*/

    List<OrderDTO> getOrderByCashier(Long cashierId);

/*    List<OrderDTO> getOrderByCashier(
            Long cashierId,
            User user
    ) throws Exception;*/

    void deleteOrder(Long id) throws Exception;

/*    void deleteOrder(
            Long id,
            User user
    ) throws Exception;*/

    List<OrderDTO> getTodayOrdersByBranch(
            Long branchId
    ) throws Exception;

/*    List<OrderDTO> getTodayOrdersByBranch(
            Long branchId,
            User user
    ) throws Exception;*/

    List<OrderDTO> getOrdersByCustomerId(
            Long customerId
    ) throws Exception;

/*    List<OrderDTO> getOrdersByCustomerId(
            Long customerId,
            User user
    ) throws Exception;*/

    List<OrderDTO> getTop5RecentOrdersByBranchId(
            Long branchId
    ) throws Exception;

/*    List<OrderDTO> getTop5RecentOrdersByBranchId(
            Long branchId,
            User user
    ) throws Exception;*/

    OrderDTO updateOrderStatus(Long orderId, OrderStatus status) throws Exception;
}