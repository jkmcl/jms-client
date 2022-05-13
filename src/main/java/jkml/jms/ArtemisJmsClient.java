package jkml.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ArtemisJmsClient extends JmsClient {

	private final ActiveMQConnectionFactory connectionFactory;

	public ArtemisJmsClient(String brokerUrl) {
		connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
	}

	@Override
	public void close() {
		if (context != null) {
			context.close();
			context = null;
		}
		connectionFactory.close();
	}

	@Override
	protected ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	protected Queue createQueue(String name) {
		return context.createQueue(name);
	}

}
