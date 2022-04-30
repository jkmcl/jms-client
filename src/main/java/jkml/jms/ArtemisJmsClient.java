package jkml.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ArtemisJmsClient extends JmsClient {

	private ActiveMQConnectionFactory connectionFactory;

	public ArtemisJmsClient(String url) {
		this.connectionFactory = new ActiveMQConnectionFactory(url);
	}

	@Override
	protected ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	public void close() throws Exception {
		if (context != null) {
			context.close();
		}
		connectionFactory.close();
	}

}
