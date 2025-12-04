package com.mattschutz.scada.dto;

import com.mattschutz.scada.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
}
