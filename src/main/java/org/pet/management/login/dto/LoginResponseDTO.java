package org.pet.management.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginResponseDTO {
    private boolean isLoggedIn;
    private String token;
    private String errorMessage;

    public static LoginResponseDTO loginFail(final String errorMessage) {
        return new LoginResponseDTO(false, null, errorMessage);
    }
    public static LoginResponseDTO loginSuccess(final String token) {
        return new LoginResponseDTO(true, token, null);
    }
}
