package org.pet.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginRequestDTO {
    private String username;
    private String password;

    public static LoginRequestDTO from(final String userName, final String password) {
        return new LoginRequestDTO(userName, password);
    }

}
