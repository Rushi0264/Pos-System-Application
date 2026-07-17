package com.example.pos.system.controller;


import com.example.pos.system.payload.dto.PaymentDTO;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {


    private final PaymentService paymentService;



    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(
            @RequestBody PaymentDTO dto
    ) throws Exception {


        return ResponseEntity.ok(
                paymentService.createPayment(dto)
        );

    }



    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getByOrder(
            @PathVariable Long orderId
    ) throws Exception {


        return ResponseEntity.ok(
                paymentService.getPaymentByOrderId(orderId)
        );

    }



    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAll()
            throws Exception {


        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );

    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id
    ) throws Exception {


        paymentService.deletePayment(id);


        ApiResponse response = new ApiResponse();

        response.setMessage(
                "Payment deleted successfully"
        );


        return ResponseEntity.ok(response);

    }

}