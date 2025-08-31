package nl.novi.garage.services;

import nl.novi.garage.dtos.customer.CustomerResponseDTO;
import nl.novi.garage.dtos.inspection.InspectionResponseDTO;
import nl.novi.garage.dtos.receipt.ReceiptGenerateRequestDTO;
import nl.novi.garage.dtos.receipt.ReceiptPaymentDTO;
import nl.novi.garage.dtos.receipt.ReceiptResponseDTO;
import nl.novi.garage.dtos.repair.RepairResponseDTO;
import nl.novi.garage.models.*;
import nl.novi.garage.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReceiptService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.21");
    private static final BigDecimal INSPECTION_FEE = new BigDecimal("50.00");

    private final ReceiptRepository receiptRepository;
    private final ReceiptInspectionsRepository receiptInspectionsRepository;
    private final ReceiptRepairsRepository receiptRepairsRepository;
    private final CustomerRepository customerRepository;
    private final InspectionRepository inspectionRepository;
    private final RepairRepository repairRepository;
    private final RepairActionsRepository repairActionsRepository;
    private final RepairPartsRepository repairPartsRepository;
    private final RepairCustomActionsRepository repairCustomActionsRepository;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository,
            ReceiptInspectionsRepository receiptInspectionsRepository,
            ReceiptRepairsRepository receiptRepairsRepository,
            CustomerRepository customerRepository,
            InspectionRepository inspectionRepository,
            RepairRepository repairRepository,
            RepairActionsRepository repairActionsRepository,
            RepairPartsRepository repairPartsRepository,
            RepairCustomActionsRepository repairCustomActionsRepository) {
        this.receiptRepository = receiptRepository;
        this.receiptInspectionsRepository = receiptInspectionsRepository;
        this.receiptRepairsRepository = receiptRepairsRepository;
        this.customerRepository = customerRepository;
        this.inspectionRepository = inspectionRepository;
        this.repairRepository = repairRepository;
        this.repairActionsRepository = repairActionsRepository;
        this.repairPartsRepository = repairPartsRepository;
        this.repairCustomActionsRepository = repairCustomActionsRepository;
    }

    public ReceiptResponseDTO generateReceipt(ReceiptGenerateRequestDTO requestDTO) {
        // Validate customer exists
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with id: " + requestDTO.getCustomerId()));

        // Validate and fetch inspections
        List<Inspection> inspections = new ArrayList<>();
        if (requestDTO.getInspectionIds() != null) {
            for (Long inspectionId : requestDTO.getInspectionIds()) {
                Inspection inspection = inspectionRepository.findById(inspectionId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Inspection not found with id: " + inspectionId));

                // Verify inspection belongs to customer
                if (!inspection.getCar().getCustomer().getId().equals(requestDTO.getCustomerId())) {
                    throw new IllegalArgumentException(
                            "Inspection " + inspectionId + " does not belong to customer "
                                    + requestDTO.getCustomerId());
                }

                // Check if inspection is already paid
                if (inspection.getIsPaid()) {
                    throw new IllegalArgumentException(
                            "Inspection " + inspectionId + " is already paid");
                }

                inspections.add(inspection);
            }
        }

        // Validate and fetch repairs
        List<Repair> repairs = new ArrayList<>();
        if (requestDTO.getRepairIds() != null) {
            for (Long repairId : requestDTO.getRepairIds()) {
                Repair repair = repairRepository.findById(repairId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Repair not found with id: " + repairId));

                // Verify repair belongs to customer
                if (!repair.getCar().getCustomer().getId().equals(requestDTO.getCustomerId())) {
                    throw new IllegalArgumentException(
                            "Repair " + repairId + " does not belong to customer " + requestDTO.getCustomerId());
                }

                // Check if repair is already paid
                if (repair.getIsPaid()) {
                    throw new IllegalArgumentException(
                            "Repair " + repairId + " is already paid");
                }

                repairs.add(repair);
            }
        }

        // Calculate totals
        BigDecimal totalExclVat = calculateTotalExcludingVat(inspections, repairs);
        BigDecimal vat = totalExclVat.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInclVat = totalExclVat.add(vat);

        // Create receipt
        Receipt receipt = new Receipt();
        receipt.setCustomer(customer);
        receipt.setTotalExclVat(totalExclVat);
        receipt.setVat(vat);
        receipt.setTotalInclVat(totalInclVat);
        receipt.setIsPaid(false);
        receipt.setCreatedDate(LocalDate.now());

        Receipt savedReceipt = receiptRepository.save(receipt);

        // Create junction entries for inspections
        for (Inspection inspection : inspections) {
            ReceiptInspections receiptInspection = new ReceiptInspections(savedReceipt, inspection);
            receiptInspectionsRepository.save(receiptInspection);
        }

        // Create junction entries for repairs
        for (Repair repair : repairs) {
            ReceiptRepairs receiptRepair = new ReceiptRepairs(savedReceipt, repair);
            receiptRepairsRepository.save(receiptRepair);
        }

        return mapToResponseDTO(savedReceipt);
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getAllReceipts() {
        List<Receipt> receipts = receiptRepository.findAll();
        return receipts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReceiptResponseDTO getReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found with id: " + id));
        return mapToResponseDTOWithDetails(receipt);
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getReceiptsByCustomerId(Long customerId) {
        // Verify customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        List<Receipt> receipts = receiptRepository.findByCustomerId(customerId);
        return receipts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getReceiptsByPaymentStatus(Boolean isPaid) {
        List<Receipt> receipts = receiptRepository.findByIsPaid(isPaid);
        return receipts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ReceiptResponseDTO markAsPaid(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found with id: " + id));

        // Check if already paid
        if (receipt.getIsPaid()) {
            throw new IllegalStateException("Receipt is already marked as paid");
        }

        receipt.setIsPaid(true);
        Receipt updatedReceipt = receiptRepository.save(receipt);

        // Mark all associated inspections as paid
        List<ReceiptInspections> receiptInspections = receiptInspectionsRepository.findByReceiptId(id);
        for (ReceiptInspections receiptInspection : receiptInspections) {
            Inspection inspection = receiptInspection.getInspection();
            inspection.setIsPaid(true);
            inspectionRepository.save(inspection);
        }

        // Mark all associated repairs as paid
        List<ReceiptRepairs> receiptRepairs = receiptRepairsRepository.findByReceiptId(id);
        for (ReceiptRepairs receiptRepair : receiptRepairs) {
            Repair repair = receiptRepair.getRepair();
            repair.setIsPaid(true);
            repairRepository.save(repair);
        }

        return mapToResponseDTO(updatedReceipt);
    }

    public ReceiptResponseDTO updatePaymentStatus(Long id, ReceiptPaymentDTO paymentDTO) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found with id: " + id));

        // Only allow marking as paid, not unpaid
        if (!paymentDTO.getIsPaid() && receipt.getIsPaid()) {
            throw new IllegalStateException("Cannot mark a paid receipt as unpaid");
        }

        if (paymentDTO.getIsPaid() && !receipt.getIsPaid()) {
            return markAsPaid(id);
        }

        return mapToResponseDTO(receipt);
    }

    private BigDecimal calculateTotalExcludingVat(List<Inspection> inspections, List<Repair> repairs) {
        BigDecimal total = BigDecimal.ZERO;

        // Add inspection fees (€50 each)
        if (inspections != null) {
            total = total.add(INSPECTION_FEE.multiply(new BigDecimal(inspections.size())));
        }

        // Add repair costs
        if (repairs != null) {
            for (Repair repair : repairs) {
                total = total.add(calculateRepairCost(repair));
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRepairCost(Repair repair) {
        BigDecimal cost = BigDecimal.ZERO;

        // Add action costs
        List<RepairActions> actions = repairActionsRepository.findByRepairId(repair.getId());
        for (RepairActions action : actions) {
            BigDecimal actionCost = action.getAction().getPrice()
                    .multiply(new BigDecimal(action.getAmount()));
            cost = cost.add(actionCost);
        }

        // Add part costs
        List<RepairParts> parts = repairPartsRepository.findByRepairId(repair.getId());
        for (RepairParts part : parts) {
            BigDecimal partCost = part.getPart().getPrice()
                    .multiply(new BigDecimal(part.getAmount()));
            cost = cost.add(partCost);
        }

        // Add custom action costs
        List<RepairCustomActions> customActions = repairCustomActionsRepository.findByRepairId(repair.getId());
        for (RepairCustomActions customAction : customActions) {
            cost = cost.add(customAction.getPrice());
        }

        return cost;
    }

    // Helper methods for DTO mapping
    private ReceiptResponseDTO mapToResponseDTO(Receipt receipt) {
        CustomerResponseDTO customerDTO = new CustomerResponseDTO(
                receipt.getCustomer().getId(),
                receipt.getCustomer().getName(),
                receipt.getCustomer().getPhonenumber());

        return new ReceiptResponseDTO(
                receipt.getId(),
                customerDTO,
                receipt.getTotalExclVat(),
                receipt.getVat(),
                receipt.getTotalInclVat(),
                receipt.getIsPaid(),
                receipt.getCreatedDate());
    }

    private ReceiptResponseDTO mapToResponseDTOWithDetails(Receipt receipt) {
        CustomerResponseDTO customerDTO = new CustomerResponseDTO(
                receipt.getCustomer().getId(),
                receipt.getCustomer().getName(),
                receipt.getCustomer().getPhonenumber());

        // Get inspections
        List<ReceiptInspections> receiptInspections = receiptInspectionsRepository.findByReceiptId(receipt.getId());
        List<InspectionResponseDTO> inspectionDTOs = receiptInspections.stream()
                .map(ri -> mapInspectionToResponseDTO(ri.getInspection()))
                .collect(Collectors.toList());

        // Get repairs
        List<ReceiptRepairs> receiptRepairs = receiptRepairsRepository.findByReceiptId(receipt.getId());
        List<RepairResponseDTO> repairDTOs = receiptRepairs.stream()
                .map(rr -> mapRepairToResponseDTO(rr.getRepair()))
                .collect(Collectors.toList());

        return new ReceiptResponseDTO(
                receipt.getId(),
                customerDTO,
                receipt.getTotalExclVat(),
                receipt.getVat(),
                receipt.getTotalInclVat(),
                receipt.getIsPaid(),
                receipt.getCreatedDate(),
                inspectionDTOs,
                repairDTOs);
    }

    private InspectionResponseDTO mapInspectionToResponseDTO(Inspection inspection) {
        return new InspectionResponseDTO(
                inspection.getId(),
                inspection.getCar().getId(),
                inspection.getCar().getBrand(),
                inspection.getCar().getModel(),
                inspection.getCar().getLicensePlate(),
                inspection.getDate(),
                inspection.getReport(),
                inspection.getStatus(),
                inspection.getIsPaid());
    }

    private RepairResponseDTO mapRepairToResponseDTO(Repair repair) {
        RepairResponseDTO dto = new RepairResponseDTO();
        dto.setId(repair.getId());
        dto.setCarId(repair.getCar().getId());
        dto.setCarBrand(repair.getCar().getBrand());
        dto.setCarModel(repair.getCar().getModel());
        dto.setCarLicensePlate(repair.getCar().getLicensePlate());
        dto.setDate(repair.getDate());
        dto.setStatus(repair.getStatus());
        dto.setReport(repair.getReport());
        dto.setIsPaid(repair.getIsPaid());
        return dto;
    }
}