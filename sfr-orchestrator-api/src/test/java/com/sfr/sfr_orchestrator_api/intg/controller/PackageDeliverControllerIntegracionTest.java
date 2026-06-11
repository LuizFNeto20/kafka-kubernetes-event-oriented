package com.sfr.sfr_orchestrator_api.intg.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.sfr.sfr_orchestrator_api.application.dto.PackageDeliveryRequest;
import com.sfr.sfr_orchestrator_api.infrastructure.kafka.avro.RequestStartedEvent;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = { "package-delivery-topic" })
@TestPropertySource(properties = {
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.properties.schema.registry.url=mock://schema-registry",
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class PackageDeliverControllerIntegracionTest {

        @Autowired
        TestRestTemplate template;

        @Autowired
        EmbeddedKafkaBroker kafkaBroker;

        private Consumer<String, RequestStartedEvent> consumer;

        @BeforeEach
        void setUp() {
                var configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", kafkaBroker));

                configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

                configs.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

                configs.put("schema.registry.url", "mock://schema-registry");

                var avroDeserializer = new KafkaAvroDeserializer();
                avroDeserializer.configure(configs, false);

                consumer = new DefaultKafkaConsumerFactory<>(
                                configs,
                                new StringDeserializer(),
                                (Deserializer) avroDeserializer).createConsumer();

                kafkaBroker.consumeFromEmbeddedTopics(consumer, "package-delivery-topic");
        }

        @AfterEach
        void tearDown() {
                consumer.close();
        }

        @Test
        void shouldPostEventSuccesfully() {

                // GIVEN
                var httpHeaders = new HttpHeaders();
                httpHeaders.set("content-type", MediaType.APPLICATION_JSON_VALUE.toString());

                var requestBody = getValidRequestBody();

                var httpRequest = new HttpEntity<>(requestBody, httpHeaders);

                // WHEN
                ResponseEntity<Void> response = template.exchange(
                                "/api/delivery",
                                HttpMethod.POST,
                                httpRequest,
                                Void.class);

                // THEN
                assertEquals(
                                HttpStatus.CREATED,
                                response.getStatusCode());

                ConsumerRecord<String, RequestStartedEvent> singleRecord = KafkaTestUtils
                                .getSingleRecord(consumer, "package-delivery-topic");

                assertNotNull(singleRecord);
                assertNotNull(singleRecord.key());

                RequestStartedEvent eventValue = singleRecord.value();
                assertNotNull(eventValue);

                assertNotNull(eventValue.getOrderId(), "O OrderId não deveria ser nulo");

                assertEquals(requestBody.height(), eventValue.getHeight(), "A altura (height) diverge da requisição");
                assertEquals(requestBody.width(), eventValue.getWidth(), "A largura (width) diverge da requisição");
                assertEquals(requestBody.length(), eventValue.getLength(),
                                "O comprimento (length) diverge da requisição");
                assertEquals(requestBody.weight(), eventValue.getWeight(), "O peso (weight) diverge da requisição");

                assertEquals(
                                requestBody.originZipCode(),
                                eventValue.getOriginZipCode().toString(),
                                "O CEP de origem diverge da requisição");
                assertEquals(
                                requestBody.destinationZipCode(),
                                eventValue.getDestinationZipCode().toString(),
                                "O CEP de destino diverge da requisição");
        }

        private PackageDeliveryRequest getValidRequestBody() {
                var requestBody = new PackageDeliveryRequest(
                                15.5,
                                20.0,
                                30.0,
                                2.5,
                                "01001000",
                                "20001000");
                return requestBody;
        }
}
