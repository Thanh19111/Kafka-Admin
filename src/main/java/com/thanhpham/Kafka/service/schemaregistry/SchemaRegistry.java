package com.thanhpham.Kafka.service.schemaregistry;

import com.thanhpham.Kafka.dto.request.SchemaCreateRequest;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SchemaRegistry {
    private final SchemaRegistryClient client;

    public void createSchema(SchemaCreateRequest schema) throws RestClientException, IOException {
        Schema.Parser parser = new Schema.Parser();
        Schema avroSchema = parser.parse(schema.getSchema());
        AvroSchema confluentSchema = new AvroSchema(avroSchema);
        int schemaId = client.register(schema.getSubject(), confluentSchema);
        System.out.println("Registered schema ID = " + schemaId);
    }

    public void getSchemaBySubject(String subject) throws RestClientException, IOException {
        SchemaMetadata metadata = client.getLatestSchemaMetadata(subject);
        System.out.println(metadata.getSchema());
    }

    public void getAllSubject() throws RestClientException, IOException {
        System.out.println(client.getAllSubjects().toString());
    }

    public void checkCompatibility(){
        //boolean isCompatible = client.testCompatibility("user-value", new AvroSchema(newSchema));
    }

    public void updateCompatibility(){
        //client.updateCompatibility("user-value", "BACKWARD");
    }

    public void getVersionBySubject(){
        //int version = client.getVersion("user-value", new AvroSchema(avroSchema));
    }

    public void deleteSchemaBySubject(){
        //List<Integer> deletedVersions = client.deleteSubject("user-value");
    }
}
