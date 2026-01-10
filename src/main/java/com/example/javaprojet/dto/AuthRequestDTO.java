package com.example.javaprojet.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequestDTO {
    private String email;
    private String password;
}



