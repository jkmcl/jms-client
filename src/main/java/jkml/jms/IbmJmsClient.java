package jkml.jms;

import com.ibm.mq.jakarta.jms.MQDestination;
import com.ibm.msg.client.jakarta.jms.JmsConnectionFactory;
import com.ibm.msg.client.jakarta.jms.JmsConstants;
import com.ibm.msg.client.jakarta.jms.JmsFactoryFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;

public class IbmJmsClient extends JmsClient {

	private final JmsConnectionFactory connectionFactory;

	public IbmJmsClient(IbmQueueManager queueManager) {
		try {
			connectionFactory = JmsFactoryFactory.getInstance(JmsConstants.JAKARTA_WMQ_PROVIDER).createConnectionFactory();
			connectionFactory.setStringProperty(CommonConstants.WMQ_HOST_NAME, queueManager.getHost());
			connectionFactory.setIntProperty(CommonConstants.WMQ_PORT, queueManager.getPort());
			connectionFactory.setStringProperty(CommonConstants.WMQ_CHANNEL, queueManager.getChannel());
			connectionFactory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
			connectionFactory.setStringProperty(CommonConstants.WMQ_QUEUE_MANAGER, queueManager.getName());
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	@Override
	public void close() {
		if (context != null) {
			context.close();
			context = null;
		}
	}

	@Override
	protected ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	protected Queue createQueue(String queueName) {
		Queue queue = context.createQueue("queue:///" + queueName);
		try {
			// Do not send JMS header
			((MQDestination) queue).setTargetClient(CommonConstants.WMQ_CLIENT_NONJMS_MQ);
		} catch (JMSException e) {
			throw new JmsException(e);
		}
		return queue;
	}

}
