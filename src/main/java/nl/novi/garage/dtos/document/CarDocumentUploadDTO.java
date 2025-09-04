package nl.novi.garage.dtos.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CarDocumentUploadDTO {

    @NotNull(message = "Car ID cannot be null")
    private Long carId;

    @NotBlank(message = "Filename cannot be blank")
    @Size(min = 1, max = 255, message = "Filename must be between 1 and 255 characters")
    private String filename;

    @NotNull(message = "File data cannot be null")
    private byte[] data;

    @NotBlank(message = "Content type cannot be blank")
    private String contentType;

    // Constructors
    public CarDocumentUploadDTO() {
    }

    public CarDocumentUploadDTO(Long carId, String filename, byte[] data, String contentType) {
        this.carId = carId;
        this.filename = filename;
        this.data = data;
        this.contentType = contentType;
    }

    // Getters and Setters
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "CarDocumentUploadDTO{" +
                "carId=" + carId +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", dataSize=" + (data != null ? data.length : 0) +
                '}';
    }
}