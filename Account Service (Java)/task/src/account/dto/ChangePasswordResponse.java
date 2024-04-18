package account.dto;

public record ChangePasswordResponse(String email, String status) {

    @Override
    public String email() {
        return email;
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public String toString() {
        return "ChangePasswordResponse{" +
               "email='" + email + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}
