package net.schwabe.tardis.responses;

/*
 * Generic POJO for returning structured message content (via JSON)
 */
public class MessageResponse {
	public int ERROR = 0;
	public String MESSAGE = "";

	public MessageResponse(String msg)
	{
		this.MESSAGE = (msg == null) ? "" : msg;
	}

	public MessageResponse(int err, String msg)
	{
		this.ERROR = err;
		this.MESSAGE = msg;
	}
}
