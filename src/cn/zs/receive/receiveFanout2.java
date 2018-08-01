package cn.zs.receive;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class receiveFanout2 {
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
		
		//创建2个队列,将他们都绑定到同一个exchange
        String queue_name = channel.queueDeclare().getQueue();
        channel.queueBind(queue_name, EXCHANG_NAME, "");
	    System.out.println("Fanout2:"+queue_name);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		// 创建一个默认消费者，并在handleDelivery中回调处理消息内容
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
			}
		};
		// channel绑定队列、消费者，autoAck为true表示一旦收到消息则自动回复确认消息
		channel.basicConsume(queue_name, true, consumer);
	}
}
