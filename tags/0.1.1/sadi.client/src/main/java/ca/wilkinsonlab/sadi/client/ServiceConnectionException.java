package ca.wilkinsonlab.sadi.client;

import ca.wilkinsonlab.sadi.SADIException;

public class ServiceConnectionException extends SADIException
{
	private static final long serialVersionUID = 1L;

	public ServiceConnectionException(String message)
	{
		super(message);
	}
	
	public ServiceConnectionException(String message, Throwable cause)
	{
        super(message, cause);
    }
	
	public ServiceConnectionException(Throwable cause)
	{
        super(cause);
    }
}
