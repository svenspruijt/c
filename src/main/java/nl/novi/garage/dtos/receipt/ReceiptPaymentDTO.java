package nl.novi.garage.dtos.receipt;

import jakarta.validation.constraints.NotNull;

public class ReceiptPaymentDTO {

    @NotNull(message = "Payment status cannot be null")
    private Boolean isPaid;

    // Constructors
    public ReceiptPaymentDTO() {
    }

    public ReceiptPaymentDTO(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    // Getters and Setters
    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    @Override
    public String toString() {
        return "ReceiptPaymentDTO{" +
                "isPaid=" + isPaid +
                '}';
    }
}