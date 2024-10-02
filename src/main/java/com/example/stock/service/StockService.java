package com.example.stock.service;

import com.example.stock.entity.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

	private final StockRepository stockRepository;

	// 재고 감소 로직
	// @Transactional
	public synchronized void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id).orElseThrow();	// 재고 조회
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
