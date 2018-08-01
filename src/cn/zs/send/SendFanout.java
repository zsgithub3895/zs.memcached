package cn.zs.send;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

//一个生产者，多个消费者。
public class SendFanout {
	private final static String EXCHANG_NAME = "my_exchang_name";
	private final static String TYPE = "fanout";

	public static void main(String[] args) throws IOException, TimeoutException {
		// 创建一个连接，并且从连接处获取一个channel，为什么用channel参考"RabbitMQ--整体综述"
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("10.223.138.195");
		factory.setUsername("zhangsai");
		factory.setPassword("123456");
		factory.setPort(5672);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANG_NAME, TYPE);

		for (int i = 0; i < 10; i++) {
			try {
				new Thread().sleep(1000);
				String message = "Hello World!" + i;
				channel.basicPublish(EXCHANG_NAME, "", null, message.getBytes());
				System.out.println(" [x] Sent '" + message + "'");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 如同数据库连接一样，依次关闭连接
		channel.close();
		connection.close();
	}
}
