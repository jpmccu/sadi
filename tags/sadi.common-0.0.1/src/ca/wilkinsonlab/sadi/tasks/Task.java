package ca.wilkinsonlab.sadi.tasks;

import java.util.Calendar;
import java.util.Date;

/**
 * A basic abstract class for a background task.
 * See {@link ca.wilkinsonlab.sadi.tasks.TaskManager} for details.
 * 
 * @author Luke McCarthy
 */
public abstract class Task implements Runnable
{
	protected String id;
	protected String status;
	protected Throwable error;
	protected Date completed;
	
	protected Task()
	{
		status = "";
	}
	
	/**
	 * Returns the unique identifier of this task.
	 * @return the unique identifier of this task
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Returns the latest status message from this task.
	 * @return the latest status message from this task.
	 */
	public String getStatus()
	{
		return status;
	}
	
	/**
	 * Sets the task status message.
	 * @param status the status message
	 */
	protected void setStatus(String status)
	{
		this.status = status;
	}
	
	/**
	 * Returns the error that caused this task to fail.
	 * @return the error that caused this task to fail
	 */
	public Throwable getError()
	{
		return error;
	}
	
	/**
	 * Sets the error.
	 * @param error the error
	 */
	protected void setError(Throwable error)
	{
		this.error = error;
		setStatus(error.toString());
	}
	
	/**
	 * Indicates that this task finished with an error.
	 * @param error the error that caused this task to finish
	 */
	protected void fatalError(Throwable error)
	{
		setError(error);
		completed = Calendar.getInstance().getTime();
	}
	
	/**
	 * Indicates that this task finished successfully.
	 */
	protected void success()
	{
		completed = Calendar.getInstance().getTime();
	}
	
	/**
	 * Returns true if the task is finished, false otherwise.
	 * @return true if the task is finished, false otherwise.
	 */
	public boolean isFinished()
	{
		return completed != null;
	}
	
	/**
	 * Returns the time this task finished.
	 * @return
	 */
	public Date getCompletionTime()
	{
		return completed;
	}
	
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("[id:");
		buf.append(id);
		buf.append("]");
		return buf.toString();
	}
}