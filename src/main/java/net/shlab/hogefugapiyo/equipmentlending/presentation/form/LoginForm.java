package net.shlab.hogefugapiyo.equipmentlending.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginForm {

    @NotBlank(message = "{validation.login.user-id.required}")
    @Size(max = 6, message = "{validation.login.user-id.size}")
    private String userId;

    @NotBlank(message = "{validation.login.password.required}")
    @Size(max = 20, message = "{validation.login.password.size}")
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
