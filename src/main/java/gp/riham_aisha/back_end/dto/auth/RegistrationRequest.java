package gp.riham_aisha.back_end.dto.auth;

public record RegistrationRequest(String username, String firstName, String lastName,
                                  String email, String password,
                                  String phoneNumber) {
}
