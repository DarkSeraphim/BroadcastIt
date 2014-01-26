package org.sensationcraft.broadcastit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageSelector {

	private List<Message> messages;
	private List<Double> weights;
	private final Random random = new Random();
	private final boolean useRandom;
	private final boolean weighted;
	private int step;

	public MessageSelector(final List<Message> messages, final boolean useRandom, final boolean weighted){
		this.messages = messages;
		this.useRandom = useRandom;
		this.weighted = weighted;
		if(weighted)
			this.calculateWeights();
	}

	public Message getMessage(){
		Message message = null;
		if(this.useRandom){
			if(this.weighted){
				final double weighted = this.random.nextDouble();
				for(int i = 0; i < this.weights.size(); i++)
					if(weighted < this.weights.get(i).doubleValue())
						message = this.messages.get(i);
			}else
				message = this.messages.get(this.random.nextInt(this.messages.size()));
		}else{
			message = this.messages.get(++this.step);
			this.step %= this.messages.size();
		}
		return message;
	}

	public void calculateWeights(){
		this.weights = new ArrayList<Double>(this.messages.size());
		double sum = 0.0D;
		for(final Message message:this.messages)
			sum += message.getWeight();
		for(int i = 0; i < this.messages.size(); i++){
			if(i == (this.messages.size()-1)){
				this.weights.set(i, 1.0D);
				return;
			}
			double weight = this.messages.get(i).getWeight()/sum;
			if(i != 0)
				weight += this.weights.get(i-1);
			this.weights.set(i, weight);
		}
	}

	public List<Message> getMessages() {
		return this.messages;
	}

	public void setMessages(final List<Message> messages) {
		this.messages = messages;
	}

}