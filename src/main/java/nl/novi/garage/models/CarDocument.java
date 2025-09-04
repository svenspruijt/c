package nl.novi.garage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "car_documents")
public class CarDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Car cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotBlank(message = "Filename cannot be blank")
    @Size(min = 1, max = 255, message = "Filename must be between 1 and 255 characters")
    @Column(nullable = false)
    private String filename;

    @NotBlank(message = "Filepath cannot be blank")
    @Size(min = 1, max = 500, message = "Filepath must be between 1 and 500 characters")
    @Column(nullable = false)
    private String filepath;

    @NotNull(message = "Document data cannot be null")
    @Lob
    @Column(nullable = false)
    private byte[] data;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    // Constructors
    public CarDocument() {
    }

    public CarDocument(Car car, String filename, String filepath, byte[] data, String contentType, Long fileSize) {
        this.car = car;
        this.filename = filename;
        this.filepath = filepath;
        this.data = data;
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

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "CarDocument{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", filepath='" + filepath + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}