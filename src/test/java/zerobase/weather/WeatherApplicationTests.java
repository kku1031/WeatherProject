package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest    //클래스가 테스트 용 클래스다.
class WeatherApplicationTests {

	@Test
	void equalTest() {
		assertEquals(1,1);
	}

	@Test
	void nullTest() {
		assertNull(null);
	}
	@Test
	void trueTest() {
		assertTrue(1==1);
	}
}
