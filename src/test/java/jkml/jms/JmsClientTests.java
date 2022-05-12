package jkml.jms;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JmsClientTests {

	private static final String ARTEMIS_BROKER_URL = "vm://0";

	private static final String QUEUE_NAME = "queue1";

	@Test
	void test() throws Exception {
		try (JmsClient client = new ArtemisJmsClient(ARTEMIS_BROKER_URL)) {

			// Test connect
			client.connect();

			// Test put and depth
			String expected = UUID.randomUUID().toString();
			client.put(QUEUE_NAME, expected);
			await().until(() -> client.depth(QUEUE_NAME) == 1);

			// Test browse and depth
			await().until(() -> expected.equals(client.browse(QUEUE_NAME).getBody()));
			await().until(() -> client.depth(QUEUE_NAME) == 1);

			// Test get and depth
			await().until(() -> expected.equals(client.get(QUEUE_NAME).getBody()));
			await().until(() -> client.depth(QUEUE_NAME) == 0);

			// Test clear and count
			client.put(QUEUE_NAME, UUID.randomUUID().toString());
			client.put(QUEUE_NAME, UUID.randomUUID().toString());
			await().until(() -> client.depth(QUEUE_NAME) == 2);
			assertEquals(2, client.clear(QUEUE_NAME));
			await().until(() -> client.depth(QUEUE_NAME) == 0);

			// Test get invalid queue
			assertThrows(RuntimeException.class, () -> client.get("NO_SUCH_QUEUE"));
		}
	}

}
