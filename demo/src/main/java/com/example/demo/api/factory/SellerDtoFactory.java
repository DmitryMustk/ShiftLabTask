package com.example.demo.api.factory;

import com.example.demo.api.dto.SellerDto;
import com.example.demo.store.entity.SellerEntity;
import org.springframework.stereotype.Component;

@Component
public class SellerDtoFactory {
    public SellerDto makeSellerDto(SellerEntity sellerEntity) {
        return SellerDto.builder()
                .id(sellerEntity.getId())
                .name(sellerEntity.getName())
                .contactInfo(sellerEntity.getContactInfo())
                .registrationDate(sellerEntity.getRegistrationDate())
                .build();
    }
}
