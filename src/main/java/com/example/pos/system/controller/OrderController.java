package com.example.pos.system.controller;


import com.example.pos.system.domain.OrderStatus;
import com.example.pos.system.domain.PaymentType;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.ActivityDTO;
import com.example.pos.system.payload.dto.OrderDTO;
import com.example.pos.system.service.DashboardService;
import com.example.pos.system.service.InvoiceService;
import com.example.pos.system.service.OrderService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {


    private final OrderService orderService;
    private final InvoiceService invoiceService;
    private final DashboardService dashboardService;
    private final UserService userService;


    // CREATE ORDER
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody OrderDTO orderDTO
    ) throws Exception {


        System.out.println("========== ORDER API HIT ==========");
        System.out.println("CUSTOMER ID : " + orderDTO.getCustomerId());
        System.out.println("PAYMENT : " + orderDTO.getPaymentType());
        System.out.println("ITEMS : " + orderDTO.getItems());


        return ResponseEntity.ok(
                orderService.createOrder(orderDTO)
        );

    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Long id
    ) throws Exception {


        byte[] pdf =
                invoiceService.generateInvoice(id);


        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-"+id+".pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);

    }


    // GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long id
    ) throws Exception {


        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );

    }





    // GET ORDERS BY BRANCH WITH FILTER
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByBranch(

            @PathVariable Long branchId,

            @RequestParam(required=false) Long customerId,
            @RequestParam(required=false) Long cashierId,
            @RequestParam(required=false) PaymentType paymentType,
            @RequestParam(required=false) OrderStatus status

    ) throws Exception {


        return ResponseEntity.ok(
                orderService.getOrdersByBranch(
                        branchId,
                        customerId,
                        cashierId,
                        paymentType,
                        status
                )
        );
    }







    // GET ORDERS BY CASHIER
    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCashier(
            @PathVariable Long cashierId
    ){


        return ResponseEntity.ok(
                orderService.getOrderByCashier(cashierId)
        );

    }







    // TODAY ORDERS
    @GetMapping("/today/branch/{branchId}")
    public ResponseEntity<List<OrderDTO>> getTodayOrders(
            @PathVariable Long branchId
    ) throws Exception {



        return ResponseEntity.ok(
                orderService.getTodayOrdersByBranch(
                        branchId
                )
        );

    }







    // CUSTOMER ORDERS
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(
            @PathVariable Long customerId
    ) throws Exception {


        return ResponseEntity.ok(
                orderService.getOrdersByCustomerId(
                        customerId
                )
        );

    }







    // RECENT TOP 5 ORDERS
    @GetMapping("/recent/{branchId}")
    public ResponseEntity<List<OrderDTO>> getRecentOrders(
            @PathVariable Long branchId
    ) throws Exception {



        return ResponseEntity.ok(
                orderService.getTop5RecentOrdersByBranchId(
                        branchId
                )
        );


    }

    // GET ALL ORDERS (role-based: Super Admin = all, others = scoped)
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                orderService.getAllOrders(user)
        );
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<ActivityDTO>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }



    // DELETE ORDER
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable Long id
    ) throws Exception {


        orderService.deleteOrder(id);


        return ResponseEntity.ok(
                "Order deleted successfully"
        );


    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) throws Exception {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status)
        );
    }

}