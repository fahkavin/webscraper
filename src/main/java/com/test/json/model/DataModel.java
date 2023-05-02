package com.test.json.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class DataModel {
    private String symbol;
    private LocalDate transactionDate;
    private Double openPrice;
    private Double maxPrice;
    private Double minPrice;
    private Double closePrice;
    private Double changePrice;
    private Double changeRatio;
    private Double noOfStock;
    private Double volume;
    private String reason;
    private String status;
    private BigDecimal batchId;
}
