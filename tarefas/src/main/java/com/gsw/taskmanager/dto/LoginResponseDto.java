
package com.gsw.taskmanager.dto;

import java.util.List;

public record LoginResponseDto(
     String nome,
     String email,
     List<String>roles
) {}