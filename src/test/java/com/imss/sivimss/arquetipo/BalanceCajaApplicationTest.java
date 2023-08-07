package com.imss.sivimss.arquetipo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.balancecaja.BalanceCajaApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BalanceCajaApplicationTest {

	@Test
	void contextLoads() {
		String result = "test";
		BalanceCajaApplication.main(new String[] {});
		assertNotNull(result);
	}

}
