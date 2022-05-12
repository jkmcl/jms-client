package jkml.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JmsClient implements AutoCloseable {

	private final Logger log = LoggerFactory.getLogger(JmsClient.class);

	private final Map<String, Queue> queueMap = new HashMap<>();

	private JMSContext context;

	protected abstract ConnectionFactory getConnectionFactory();

	protected Queue createQueue(String queueName) {
		return getContext().createQueue(queueName);
	}

	private Queue getQueue(String queueName) {
		return queueMap.computeIfAbsent(queueName, this::createQueue);
	}

	protected JMSContext getContext() {
		if (context == null) {
			context = getConnectionFactory().createContext(JMSContext.AUTO_ACKNOWLEDGE);
		}
		return context;
	}

	public boolean connect() {
		log.info("Connecting to provider");
		try (Connection conn = getConnectionFactory().createConnection()) {
			conn.start();
			log.info("Connected to provider");
			return true;
		} catch (JMSException e) {
			log.info("Failed to connect to provider");
			return false;
		}
	}

	public void put(String queueName, String message) {
		log.info("Sending message to queue: {}", queueName);

		getContext().createProducer().send(getQueue(queueName), message);
	}

	public JmsMessage get(String queueName) {
		log.info("Receiving message from queue: {}", queueName);

		try (JMSConsumer consumer = getContext().createConsumer(getQueue(queueName))) {
			return toJmsMessage(consumer.receiveNoWait());
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	public int clear(String queueName) {
		log.info("Deleting all messages in queue: {}", queueName);

		try (JMSConsumer consumer = getContext().createConsumer(getQueue(queueName))) {
			int count = 0;
			while (consumer.receiveNoWait() != null) {
				++count;
			}
			return count;
		}
	}

	public int depth(String queueName) {
		log.info("Counting number of messages in queue: {}", queueName);

		try (QueueBrowser browser = getContext().createBrowser(getQueue(queueName))) {
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
		try (QueueBrowser browser = getContext().createBrowser(getQueue(queueName))) {
			Enumeration<?> me = browser.getEnumeration();
			return me.hasMoreElements() ? toJmsMessage((Message) me.nextElement()) : null;
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	private JmsMessage toJmsMessage(Message message) throws JMSException {
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

	@Override
	public void close() {
		if (context != null) {
			context.close();
			context = null;
		}
	}

}
