package net.sasakonnect.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.SerializationUtils;

import net.sasakonnect.wallet.tools.redis.Queueable;

@Configuration
public class RedisConfig {

	@Bean
	@Primary
	<T extends Queueable> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setKeySerializer(new ObjectRedisSerializer<>());

		// Add more custom configurations as needed.

		return redisTemplate;
	}

	class ObjectRedisSerializer<T> implements RedisSerializer<T> {
		@Override
		public byte[] serialize(T object) throws SerializationException {
			if (object == null) {
				return null;
			}
			return SerializationUtils.serialize(object);
		}

		@Override
		public T deserialize(byte[] bytes) throws SerializationException {
			if (bytes == null) {
				return null;
			}
			return (T) SerializationUtils.deserialize(bytes);
		}
	}
}
