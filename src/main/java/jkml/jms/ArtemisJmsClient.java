package jkml.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ArtemisJmsClient extends JmsClient {

	private final ActiveMQConnectionFactory connectionFactory;

	public ArtemisJmsClient(String brokerUrl) {
		connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
	}

	@Override
	protected ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	public void close() {
		super.close(); // session created from the factory has dependency on the latter
		connectionFactory.close();
	}

}
