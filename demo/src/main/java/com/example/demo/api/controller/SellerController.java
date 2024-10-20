package com.example.demo.api.controller;

import com.example.demo.api.dto.AckDto;
import com.example.demo.api.dto.SellerDto;
import com.example.demo.api.factory.SellerDtoFactory;
import com.example.demo.api.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SellerController {
    private final SellerDtoFactory sellerDtoFactory;
    private final SellerService sellerService;

    public static final String FETCH_SELLERS =      "/api/sellers";
    public static final String FETCH_SELLER_BY_ID = "/api/sellers/{id}";
    public static final String CREATE_SELLER =      "/api/sellers";
    public static final String UPDATE_SELLER =      "/api/sellers/{id}";
    public static final String DELETE_SELLER =      "/api/sellers/{id}";

    @GetMapping(FETCH_SELLERS)
    public List<SellerDto> fetchSellers() {
        return sellerService.getAllSellersStream()
                .map(sellerDtoFactory::makeSellerDto)
                .toList();
    }

    @GetMapping(FETCH_SELLER_BY_ID)
    public SellerDto fetchSellerById(
            @PathVariable Long id
    ) {
        return sellerDtoFactory
                .makeSellerDto(sellerService
                        .getSellerOrThrowException(id));
    }

    @PostMapping(CREATE_SELLER)
    public SellerDto createSeller(
            @RequestParam(name = "seller_name") String sellerName,
            @RequestParam(name = "seller_contact_info") String sellerContactInfo
    ) {
        return sellerDtoFactory
                .makeSellerDto(sellerService
                        .createSeller(sellerName, sellerContactInfo));
    }

    @PatchMapping(UPDATE_SELLER)
    public SellerDto updateSeller(
            @PathVariable(name = "id") Long sellerId,
            @RequestParam(name = "seller_name", required = false) Optional<String> optionalSellerName,
            @RequestParam(name = "seller_contact_info", required = false) Optional<String> optionalSellerContactInfo
    ) {
        return sellerDtoFactory
                .makeSellerDto(sellerService
                        .updateSeller(sellerId, optionalSellerName, optionalSellerContactInfo));
    }

    @DeleteMapping(DELETE_SELLER)
    public AckDto deleteSeller(
            @PathVariable(name = "id") Long sellerId
    ) {
        sellerService.deleteSeller(sellerId);

        return AckDto.builder()
                .answer(true)
                .build();
    }

}
