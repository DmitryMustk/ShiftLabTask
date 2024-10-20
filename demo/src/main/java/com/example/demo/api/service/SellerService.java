package com.example.demo.api.service;

import com.example.demo.api.exception.seller.SellerBadRequestException;
import com.example.demo.api.exception.seller.SellerNotFoundException;
import com.example.demo.store.entity.SellerEntity;
import com.example.demo.store.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    public Stream<SellerEntity> getAllSellersStream() {
        return sellerRepository.streamAllBy();
    }

    public SellerEntity getSellerOrThrowException(Long id) {
        return sellerRepository
                .findById(id)
                .orElseThrow(() -> new SellerNotFoundException(id));
    }

    public SellerEntity createSeller(String sellerName, String sellerContactInfo) {
        if (sellerName.trim().isEmpty() || sellerContactInfo.trim().isEmpty()) {
            throw new SellerBadRequestException("Seller name or contact info cannot be empty");
        }

        return sellerRepository.saveAndFlush(
                SellerEntity.builder()
                        .name(sellerName)
                        .contactInfo(sellerContactInfo)
                        .registrationDate(LocalDateTime.now())
                        .build()
        );
    }

    public SellerEntity updateSeller(
            Long id,
            Optional<String> optionalSellerName,
            Optional<String> optionalSellerContactInfo
    ) {
        SellerEntity seller = getSellerOrThrowException(id);
        optionalSellerName
                .filter(sellerName -> !sellerName.trim().isEmpty())
                .map(String::trim)
                .ifPresent(seller::setName);

        optionalSellerContactInfo
                .filter(sellerContactInfo -> !sellerContactInfo.trim().isEmpty())
                .map(String::trim)
                .ifPresent(seller::setContactInfo);


        return sellerRepository.saveAndFlush(seller);
    }

    public void deleteSeller(Long id) {
        sellerRepository.delete(getSellerOrThrowException(id));
    }


}
