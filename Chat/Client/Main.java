package ua.kiev.prog;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {

	static String chatroom = "common";

	public static void main(String[] args) {
		final String login;
		String tmp_login;

		try {
			final Scanner scanner = new Scanner(System.in);
			while (true) {
				System.out.println("Enter login: ");
				tmp_login = scanner.nextLine();
				System.out.println("Enter password: ");
				final String password = scanner.nextLine();
				URL url = new URL("http://127.0.0.1:8888/login?login="
						+ tmp_login + "&password=" + password);
				HttpURLConnection http = (HttpURLConnection) url
						.openConnection();
				if (http.getResponseCode() != 200) {
					try {
						throw new AuthorizationException(
								"Incorrect login or password!");
					} catch (AuthorizationException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Authorization OK");
					InputStream is = http.getInputStream();
					byte[] buf = new byte[is.available()];
					is.read(buf);
					is.close();
					System.out.println(new String(buf));
					break;
				}
			}
			login = tmp_login;

			Thread th = new Thread() {

				private int n;

				@Override
				public void run() {
					try {
						while (!isInterrupted()) {
							URL url = new URL("http://127.0.0.1:8888/get?from="
									+ n + "&login=" + login + "&chatroom="
									+ chatroom);
							HttpURLConnection http = (HttpURLConnection) url
									.openConnection();
							try {
								InputStream is = http.getInputStream();
								Message m = null;

								do {
									m = Message.readFromStream(is);
									if (m != null) {
										if (m.text != null) {
											System.out.println(m.toString());
										}
										n++;
									}
								} while (m != null);
							} finally {
								http.disconnect();
							}
						}
					} catch (Exception e) {
						return;
					}
				}
			};
			th.setDaemon(true);
			th.start();

			try {
				while (true) {
					String s = scanner.nextLine();
					if (s.isEmpty()) {
						URL status = new URL(
								"http://127.0.0.1:8888/status?login=" + login);
						HttpURLConnection httpStatus = (HttpURLConnection) status
								.openConnection();
						if (httpStatus.getResponseCode() == 200) {
							break;
						} else {
							System.out.println(httpStatus.getResponseCode());
							break;
						}
					}

					int del = s.indexOf(':');
					String to = "";
					String text = s;

					if (del >= 0) {
						to = s.substring(0, del);
						text = s.substring(del + 1);
					}

					Message m = new Message();
					m.text = text;
					m.from = login;
					m.to = to;
					if (m.to.equals("chatroom")) {
						chatroom = m.text;
						m.to = "";
						m.text = m.from + " enter in " + chatroom + " chatroom";
					}
					m.chatRoom = chatroom;

					int res = m.send("http://127.0.0.1:8888/add");
					if (res != 200) {
						System.out.println("HTTP error: " + res);
						break;
					}
				}
			} finally {
				th.interrupt();
				scanner.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}