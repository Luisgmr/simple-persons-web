package com.luisgmr.senai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data @AllArgsConstructor
public class PaginaResponseDTO<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElement;
}