package com.thanhpham.Kafka.utils;

public class Schema {
    public static String schemaStr = "{\n" +
            "  \"type\": \"record\",\n" +
            "  \"name\": \"User\",\n" +
            "  \"namespace\": \"com.example\",\n" +
            "  \"fields\": [\n" +
            "    {\"name\": \"id\", \"type\": \"string\"},\n" +
            "    {\"name\": \"age\", \"type\": \"int\"}\n" +
            "  ]\n" +
            "}";
}
