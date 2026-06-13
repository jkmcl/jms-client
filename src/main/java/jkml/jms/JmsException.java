package jkml.jms;

import java.io.Serial;

public class JmsException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public JmsException(Throwable cause) {
		super(cause);
	}

}
