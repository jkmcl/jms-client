package jkml.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;

import com.ibm.mq.jms.MQDestination;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.common.CommonConstants;

public class IbmJmsClient extends JmsClient {

	private final IbmQueueManager queueManager;

	public IbmJmsClient(IbmQueueManager queueManager) {
		this.queueManager = queueManager;
	}

	@Override
	protected ConnectionFactory getConnectionFactory() {
		try {
			JmsConnectionFactory cf = JmsFactoryFactory.getInstance(JmsConstants.WMQ_PROVIDER)
					.createConnectionFactory();
			cf.setStringProperty(CommonConstants.WMQ_HOST_NAME, queueManager.getHost());
			cf.setIntProperty(CommonConstants.WMQ_PORT, queueManager.getPort());
			cf.setStringProperty(CommonConstants.WMQ_CHANNEL, queueManager.getChannel());
			cf.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(CommonConstants.WMQ_QUEUE_MANAGER, queueManager.getName());
			return cf;
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	@Override
	protected Queue createQueue(String queueName) {
		Queue queue = getContext().createQueue("queue:///" + queueName);
		try {
			// Do not send JMS header
			((MQDestination) queue).setTargetClient(CommonConstants.WMQ_CLIENT_NONJMS_MQ);
		} catch (JMSException e) {
			throw new JmsException(e);
		}
		return queue;
	}

}
