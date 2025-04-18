package jkml.jms;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.QueueBrowser;

public abstract class JmsClient implements AutoCloseable {

	private final Logger log = LoggerFactory.getLogger(JmsClient.class);

	protected JMSContext context = null;

	protected abstract ConnectionFactory getConnectionFactory();

	protected abstract Queue createQueue(String name);

	private JMSContext getContext() {
		if (context == null) {
			context = getConnectionFactory().createContext(JMSContext.AUTO_ACKNOWLEDGE);
		}
		return context;
	}

	public boolean connect() {
		log.info("Connecting to provider");
		try {
			getContext();
			log.info("Connected to provider");
			return true;
		} catch (Exception e) {
			log.info("Failed to connect to provider");
			return false;
		}
	}

	public void put(String queueName, String message) {
		log.info("Sending message to queue: {}", queueName);

		getContext().createProducer().send(createQueue(queueName), message);
	}

	public JmsMessage get(String queueName) {
		log.info("Receiving message from queue: {}", queueName);

		try (JMSConsumer consumer = getContext().createConsumer(createQueue(queueName))) {
			return toJmsMessage(consumer.receiveNoWait());
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	public int clear(String queueName) {
		log.info("Deleting all messages in queue: {}", queueName);

		try (JMSConsumer consumer = getContext().createConsumer(createQueue(queueName))) {
			int count = 0;
			while (consumer.receiveNoWait() != null) {
				++count;
			}
			return count;
		}
	}

	public int depth(String queueName) {
		log.info("Counting number of messages in queue: {}", queueName);

		try (QueueBrowser browser = getContext().createBrowser(createQueue(queueName))) {
			Enumeration<?> me = browser.getEnumeration();
			int count = 0;
			while (me.hasMoreElements()) {
				++count;
				me.nextElement();
			}
			return count;
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	public JmsMessage browse(String queueName) {
		log.info("Browsing first message in queue: {}", queueName);
		try (QueueBrowser browser = getContext().createBrowser(createQueue(queueName))) {
			Enumeration<?> me = browser.getEnumeration();
			return me.hasMoreElements() ? toJmsMessage((Message) me.nextElement()) : null;
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	private static JmsMessage toJmsMessage(Message message) throws JMSException {
		if (message == null) {
			return null;
		}
		Map<String, String> properties = new TreeMap<>();
		Enumeration<?> names = message.getPropertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			properties.put(name, message.getStringProperty(name));
		}
		return new JmsMessage(message.getBody(String.class), properties);
	}

}
