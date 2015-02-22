package ua.kiev.prog;

import java.util.ArrayList;
import java.util.List;

public class MessageList {
	
	private static final MessageList msgList = new MessageList();

	private final List<Message> list = new ArrayList<Message>();
	
	public static MessageList getInstance() {
		return msgList;
	}
	
	public synchronized void add(Message m) {
		list.add(m);
	}
	
	public synchronized List<Message> get() {
		List<Message> res = new ArrayList<Message>();
		res.addAll(list);
		
		return res;
	}
}
