package jkml.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;

public class ArtemisJmsClient extends JmsClient {

	private final ActiveMQConnectionFactory connectionFactory;

	public ArtemisJmsClient(String brokerUrl) {
		connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
	}

	@Override
	public void close() {
		connectionFactory.close();
		super.close();
	}

	@Override
	protected ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	protected Queue createQueue(String name) {
		return getContext().createQueue(name);
	}

}
