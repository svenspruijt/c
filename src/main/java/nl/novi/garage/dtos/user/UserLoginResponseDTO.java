package nl.novi.garage.dtos.user;

public class UserLoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private String username;
    private String name;
    private String role;

    // Constructors
    public UserLoginResponseDTO() {
    }

    public UserLoginResponseDTO(String token, String username, String name, String role) {
        this.token = token;
        this.username = username;
        this.name = name;
        this.role = role;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserLoginResponseDTO{" +
                "type='" + type + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}