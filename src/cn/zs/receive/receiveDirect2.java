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

public class receiveDirect2 {
	private final static String EXCHANG_NAME = "MY_EXCHANG_NAME_DIRECT";
	private final static String TYPE = "direct";
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
		
		//创建队列,将他们都绑定到同一个exchange
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANG_NAME, "");
	    System.out.println("direct2:"+queueName);
	    //绑定固定类型的routingKey
        channel.queueBind(queueName, EXCHANG_NAME, "info");
        channel.queueBind(queueName, EXCHANG_NAME, "error");
        channel.queueBind(queueName, EXCHANG_NAME, "warning");

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		// 创建一个默认消费者，并在handleDelivery中回调处理消息内容
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" +envelope.getRoutingKey()+":"+ message + "'");
			}
		};
		// channel绑定队列、消费者，autoAck为true表示一旦收到消息则自动回复确认消息
		channel.basicConsume(queueName, true, consumer);
	}
}
