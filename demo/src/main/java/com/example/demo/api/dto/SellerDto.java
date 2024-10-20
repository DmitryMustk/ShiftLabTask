package com.example.demo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerDto {
    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    @JsonProperty("contact_info")
    private String contactInfo;

    @JsonProperty("registration_date")
    private LocalDateTime registrationDate;
}
