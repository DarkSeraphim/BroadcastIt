package org.sensationcraft.broadcastit;

public class Message {

	private final String message;
	private final double weight;

	public Message(final String message){
		this(message, 1.0D);
	}

	public Message(final String message, final double weight){
		this.message = message;
		this.weight = weight;
	}

	public double getWeight(){
		return this.weight;
	}

	public String getMessage(){
		return this.message;
	}
}
