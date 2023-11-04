package jkml.jms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class IbmJmsClientTests {

	@Test
	void test() {

		var qmgr = new IbmQueueManager();
		qmgr.setHost("locahost");
		qmgr.setPort(1414);
		qmgr.setName("name");
		qmgr.setChannel("channel");

		try (var client = new IbmJmsClient(qmgr)) {
			assertDoesNotThrow(() -> client.close());
		}
	}

}
