package com.example.demo1.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewDto {

    @NotNull(message = "orderLogIdx는 필수값입니다.")
    private Long orderLogIdx;

    @NotBlank(message = "review는 필수값입니다.")
    private String review;

    @NotNull(message = "score는 필수값입니다.")
    @Range(min = 1, max = 5, message = "점수는 1~5점 사이여야 합니다.")
    private Integer score; // 1~5 점수

}
