package jkml.jms;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public abstract class JmsClient implements AutoCloseable {

	private static final Gson GSON = new Gson();

	private final Logger log = LoggerFactory.getLogger(JmsClient.class);

	protected JMSContext context;

	protected abstract ConnectionFactory getConnectionFactory();

	protected Queue createQueue(String queueName) {
		return context.createQueue(queueName);
	}

	/** Creates context */
	public void connect() {
		log.info("Connecting to provider");
		context = getConnectionFactory().createContext();
	}

	public void put(String queueName, String message) {
		log.info("Sending message to queue: {}", queueName);
		context.createProducer().send(createQueue(queueName), message);
	}

	public JmsMessage get(String queueName) {
		log.info("Receiving message from queue: {}", queueName);
		try (JMSConsumer consumer = context.createConsumer(createQueue(queueName))) {
			Message message;
			if ((message = consumer.receiveNoWait()) != null) {
				return toJmsMessage(message);
			} else {
				return null;
			}
		}
	}

	public long clear(String queueName) {
		log.info("Removes all messages from queue: {}", queueName);
		long deleted = 0;
		try (JMSConsumer consumer = context.createConsumer(createQueue(queueName))) {
			while (consumer.receiveNoWait() != null) {
				++deleted;
			}
		}
		return deleted;
	}

	public long count(String queueName) {
		log.info("Get count of messages in queue: {}", queueName);
		long depth = 0;
		try (QueueBrowser browser = context.createBrowser(createQueue(queueName))) {
			Enumeration<?> me = browser.getEnumeration();
			while (me.hasMoreElements()) {
				++depth;
				me.nextElement();
			}
		} catch (JMSException e) {
			log.error("Failed to get count of messages in queue: {}", queueName);
		}
		return depth;
	}

	public JmsMessage peek(String queueName) {
		log.info("Receiving message from queue: {}", queueName);
		try (QueueBrowser browser = context.createBrowser(createQueue(queueName))) {
			Enumeration<?> me = browser.getEnumeration();
			if (me.hasMoreElements()) {
				return toJmsMessage((Message) me.nextElement());
			}
		} catch (JMSException e) {
			log.error("Failed to browse queue: {}", queueName);
		}
		return null;
	}

	private JmsMessage toJmsMessage(Message message) {
		try {
			Map<String, String> properties = new TreeMap<>();
			Enumeration<?> names = message.getPropertyNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				properties.put(name, message.getStringProperty(name));
			}
			if (log.isDebugEnabled()) {
				log.debug("Message properties: {}", GSON.toJson(properties));
			}
			return new JmsMessage(message.getBody(String.class), properties);
		} catch (JMSException e) {
			throw new RuntimeException("Failed to extract message", e);
		}
	}

	@Override
	public void close() throws Exception {
		if (context != null) {
			context.close();
		}
	}

}
