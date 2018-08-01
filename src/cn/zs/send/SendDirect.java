package cn.zs.send;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

//一个生产者，多个消费者。
public class SendDirect {
	private final static String EXCHANG_NAME = "MY_EXCHANG_NAME_DIRECT";
	private final static String TYPE = "direct";
	private static final List<String> SEVERITIES = new ArrayList<String>();
    static {
        SEVERITIES.add("info");
        SEVERITIES.add("error");
        SEVERITIES.add("warning");
    }

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
				String severity = SEVERITIES.get(new Random().nextInt(3));//随机产生一个routingKey
				String message = "Hello World!" +i+"---"+severity;
				channel.basicPublish(EXCHANG_NAME, severity , null, message.getBytes());
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
