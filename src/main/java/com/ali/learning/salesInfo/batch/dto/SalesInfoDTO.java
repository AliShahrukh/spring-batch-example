package com.ali.learning.salesInfo.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesInfoDTO {

    private String product;
    private String seller;
    private Integer sellerId;
    private double price;
    private String city;
    private String category;
}
