package jkml.jms;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JmsClientTests {

	private final String ARTEMIS_BROKER_URL = "vm://0";

	private final String QUEUE_NAME = "queue1";

	@Test
	void test() throws Exception {
		try (JmsClient client = new ArtemisJmsClient(ARTEMIS_BROKER_URL)) {

			// Test connect
			client.connect();

			// Test put and count
			String expected = UUID.randomUUID().toString();
			client.put(QUEUE_NAME, expected);
			await().until(() -> client.count(QUEUE_NAME) == 1);

			// Test peek and count
			await().until(() -> expected.equals(client.peek(QUEUE_NAME).getBody()));
			await().until(() -> client.count(QUEUE_NAME) == 1);

			// Test get and count
			await().until(() -> expected.equals(client.get(QUEUE_NAME).getBody()));
			await().until(() -> client.count(QUEUE_NAME) == 0);

			// Test clear and count
			client.put(QUEUE_NAME, UUID.randomUUID().toString());
			client.put(QUEUE_NAME, UUID.randomUUID().toString());
			await().until(() -> client.count(QUEUE_NAME) == 2);
			assertEquals(2, client.clear(QUEUE_NAME));
			await().until(() -> client.count(QUEUE_NAME) == 0);
		}
	}

}
