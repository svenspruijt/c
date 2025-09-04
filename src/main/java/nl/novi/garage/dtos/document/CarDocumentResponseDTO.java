package nl.novi.garage.dtos.document;

public class CarDocumentResponseDTO {

    private Long id;
    private Long carId;
    private String carLicensePlate;
    private String filename;
    private String contentType;
    private Long fileSize;

    // Constructors
    public CarDocumentResponseDTO() {
    }

    public CarDocumentResponseDTO(Long id, Long carId, String filename, String contentType, Long fileSize) {
        this.id = id;
        this.carId = carId;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    public CarDocumentResponseDTO(Long id, Long carId, String carLicensePlate, String filename, String contentType,
            Long fileSize) {
        this.id = id;
        this.carId = carId;
        this.carLicensePlate = carLicensePlate;
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    public void setCarLicensePlate(String carLicensePlate) {
        this.carLicensePlate = carLicensePlate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "CarDocumentResponseDTO{" +
                "id=" + id +
                ", carId=" + carId +
                ", carLicensePlate='" + carLicensePlate + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}