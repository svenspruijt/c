package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.receipt.ReceiptGenerateRequestDTO;
import nl.novi.garage.dtos.receipt.ReceiptPaymentDTO;
import nl.novi.garage.dtos.receipt.ReceiptResponseDTO;
import nl.novi.garage.services.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receipts")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ReceiptResponseDTO> generateReceipt(
            @Valid @RequestBody ReceiptGenerateRequestDTO receiptGenerateRequestDTO) {
        ReceiptResponseDTO response = receiptService.generateReceipt(receiptGenerateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReceiptResponseDTO>> getAllReceipts() {
        List<ReceiptResponseDTO> receipts = receiptService.getAllReceipts();
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptResponseDTO> getReceiptById(@PathVariable Long id) {
        ReceiptResponseDTO receipt = receiptService.getReceiptById(id);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByCustomerId(@PathVariable Long customerId) {
        List<ReceiptResponseDTO> receipts = receiptService.getReceiptsByCustomerId(customerId);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<ReceiptResponseDTO>> getUnpaidReceipts() {
        List<ReceiptResponseDTO> receipts = receiptService.getReceiptsByPaymentStatus(false);
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/paid")
    public ResponseEntity<List<ReceiptResponseDTO>> getPaidReceipts() {
        List<ReceiptResponseDTO> receipts = receiptService.getReceiptsByPaymentStatus(true);
        return ResponseEntity.ok(receipts);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<ReceiptResponseDTO> markReceiptAsPaid(@PathVariable Long id) {
        ReceiptResponseDTO response = receiptService.markAsPaid(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<ReceiptResponseDTO> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReceiptPaymentDTO receiptPaymentDTO) {
        ReceiptResponseDTO response = receiptService.updatePaymentStatus(id, receiptPaymentDTO);
        return ResponseEntity.ok(response);
    }
}