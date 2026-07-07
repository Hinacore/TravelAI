package com.travelai.recommend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTripRequest {

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "预算不能为空")
    private BigDecimal budget;

    @NotNull(message = "天数不能为空")
    private Integer days;

    private LocalDate startDate;

    private Integer travelers;

    private List<String> preferences;

    private String specialRequirements;
}