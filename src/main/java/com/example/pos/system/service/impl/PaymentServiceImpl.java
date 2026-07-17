package com.example.pos.system.service.impl;


import com.example.pos.system.domain.OrderStatus;
import com.example.pos.system.domain.PaymentStatus;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.PaymentMapper;
import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.Payment;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.PaymentDTO;
import com.example.pos.system.repository.OrderRepository;
import com.example.pos.system.repository.PaymentRepository;
import com.example.pos.system.service.PaymentService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final UserService userService;

    @Override
    public PaymentDTO createPayment(
            PaymentDTO dto
    ) throws Exception {


        Order order =
                orderRepository.findById(dto.getOrderId())
                        .orElseThrow(
                                () -> new Exception("Order not found")
                        );


        Payment payment =
                PaymentMapper.toEntity(dto, order);



        Payment savedPayment =
                paymentRepository.save(payment);



        if(savedPayment.getStatus()
                == PaymentStatus.SUCCESS){


            order.setStatus(
                    OrderStatus.COMPLETED
            );


            orderRepository.save(order);

        }



        return PaymentMapper.toDTO(savedPayment);
    }



    @Override
    public PaymentDTO getPaymentByOrderId(Long orderId)
            throws Exception {


        User user = userService.getCurrentUser();


        Payment payment =
                paymentRepository.findByOrderId(orderId);


        if(payment == null){
            throw new Exception(
                    "Payment not found"
            );
        }


        Order order = payment.getOrder();


        if(user.getRole() != UserRole.ROLE_SUPER_ADMIN){

            if(user.getBranch() == null ||
                    !user.getBranch()
                            .getId()
                            .equals(order.getBranch().getId())){

                throw new UserException(
                        "You cannot access this payment"
                );
            }
        }


        return PaymentMapper.toDTO(payment);
    }



    @Override
    public List<PaymentDTO> getAllPayments()
            throws Exception {


        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toDTO)
                .toList();

    }



    @Override
    public void deletePayment(Long id)
            throws Exception {


        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(
                                ()->new Exception("Payment not found")
                        );


        paymentRepository.delete(payment);

    }

}