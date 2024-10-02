package com.example.stock.facade;

import com.example.stock.entity.Stock;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.OptimisticLockStockService;
import com.example.stock.service.PessimisticLockStockService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockStockFacadeTest {

	@Autowired private StockRepository stockRepository;
	@Autowired private OptimisticLockStockFacade stockFacade;

	@BeforeEach
	public void before() {
		stockRepository.saveAndFlush(new Stock(1L, 100L));
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}

	@Test
	@DisplayName("재고 감소 로직 테스트")
	void decrease() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockFacade.decrease(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();
		Stock stock = stockRepository.findById(1L).orElseThrow();
		Assertions.assertThat(stock.getQuantity()).isEqualTo(0);
	}
}