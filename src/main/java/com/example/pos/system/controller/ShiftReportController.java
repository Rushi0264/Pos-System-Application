package com.example.pos.system.controller;

import com.example.pos.system.payload.dto.ShiftReportDTO;
import com.example.pos.system.service.ShiftReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shift-reports")
public class ShiftReportController {

    private final ShiftReportService shiftReportService;

    @PostMapping("/start")
    public ResponseEntity<ShiftReportDTO> startShift() throws Exception{
        return ResponseEntity.ok(
                shiftReportService.startShift()
        );
    }

    @PatchMapping("/end")
    public ResponseEntity<ShiftReportDTO> endShift() throws Exception{
        return ResponseEntity.ok(
                shiftReportService.endShift(null, LocalDateTime.now())
        );
    }

    @GetMapping("/current")
    public ResponseEntity<ShiftReportDTO> getCurrentShiftProgress() throws Exception{
        return ResponseEntity.ok(
                shiftReportService.getCurrentShiftProgress(null)
        );
    }

    @GetMapping("/cashier/{cashierId}/by-date")
    public ResponseEntity<ShiftReportDTO> getShiftReportByDate(
            @PathVariable Long cashierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDateTime date
            ) throws Exception{
        return ResponseEntity.ok(
                shiftReportService.getShiftByCashierAndDate(cashierId, date)
        );
    }

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<ShiftReportDTO>> getShiftReportByCashier(
            @PathVariable Long cashierId
    ) throws Exception{
        return ResponseEntity.ok(
                shiftReportService.getShiftReportByCashierId(cashierId)
        );
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ShiftReportDTO>> getShiftReportByBranch(
            @PathVariable Long branchId
    ) throws Exception{
        return ResponseEntity.ok(
                shiftReportService.getShiftReportByBranchId(branchId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftReportDTO> getShiftReportById(
            @PathVariable Long id
    ) throws Exception{
        return ResponseEntity.ok(
                shiftReportService.getShiftReportById(id)
        );
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ShiftReportDTO>> getShiftReportsByStore(
            @PathVariable Long storeId
    ) throws Exception {
        return ResponseEntity.ok(
                shiftReportService.getShiftReportsByStoreId(storeId)
        );
    }

}
