package com.gsw.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
public record UpdatePasswordDTO ( String email,
         String senha){
}
