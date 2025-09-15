package com.gym.crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
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
    public JmsTemplate jmsTemplate(MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(pooledConnectionFactory());
        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setReceiveTimeout(jmsRequestTimeout);
        jmsTemplate.setDeliveryPersistent(true);

        return jmsTemplate;
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
