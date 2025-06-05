package com.example.animor.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JacksonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }
    public static <T> T readEntity(String json, TypeReference<T> typeReference){
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Convert json to entity failed. Cause: ",e);
        }
    }

    public static <T> List<T> readEntities(String json, TypeReference<List<T>> typeReference){
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Convert json to entities failed. Cause: ",e);
        }
    }

    public static <T> String entityToJson(T entity){
        try{
            return mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            //implement logger if needed
            throw new RuntimeException("Convert entity to json failed, please check getter setter of this model");
        }
    }
}
