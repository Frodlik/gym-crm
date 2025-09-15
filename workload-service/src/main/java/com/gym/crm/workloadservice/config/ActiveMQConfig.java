package com.gym.crm.workloadservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class ActiveMQConfig {
    @Value("${jms.request-timeout-ms:5000}")
    private long jmsRequestTimeout;

    @Value("${spring.activemq.broker-url:tcp://localhost:61616}")
    private String brokerUrl;

    @Value("${spring.activemq.user:admin}")
    private String username;

    @Value("${spring.activemq.password:admin}")
    private String password;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        connectionFactory.setTrustAllPackages(true);

        return connectionFactory;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory());
        pooledConnectionFactory.setMaxConnections(10);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(500);

        return pooledConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(pooledConnectionFactory());
        factory.setMessageConverter(messageConverter);
        factory.setConcurrency("1-3");
        factory.setReceiveTimeout(jmsRequestTimeout);

        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        return converter;
    }
}
