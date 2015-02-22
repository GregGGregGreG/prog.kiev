package ua.kiev.prog;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE, dd.MM.yy HH:mm:ss");
	@XmlElement
	public String date = sdf.format(new Date());
	@XmlElement
	public String from;
	@XmlElement
	public String to;
	@XmlElement
	public String text;
	@XmlElement
	public String chatRoom;

	@Override
	public String toString() {
		return new StringBuilder().append("[").append(date)
				.append(", ChatRoom: ").append(chatRoom).append(", From: ")
				.append(from).append(", To: ").append(to).append("] ")
				.append(text).toString();
	}

	public int send(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setDoOutput(true);

		OutputStream os = con.getOutputStream();
		this.writeToStream(os);
		os.flush();
		os.close();

		return con.getResponseCode();
	}

	public void writeToStream(OutputStream out) throws IOException, JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
		
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		
		try {
			marshaller.marshal(this, bs);
		} finally {
			bs.flush();
			bs.close();
		}

		byte[] packet = bs.toByteArray();

		DataOutputStream ds = new DataOutputStream(out);
		ds.writeInt(packet.length);
		ds.write(packet);
		ds.flush();
	}

	public static Message readFromStream(InputStream in) throws IOException,
			ClassNotFoundException, JAXBException {
		if (in.available() <= 0)
			return null;
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		

		DataInputStream ds = new DataInputStream(in);
		int len = ds.readInt();
		byte[] packet = new byte[len];
		ds.read(packet);

		ByteArrayInputStream bs = new ByteArrayInputStream(packet);

		try {
			Message msg = (Message) unmarshaller.unmarshal(bs);
			return msg;
		} finally {
			bs.close();
		}
	}
}