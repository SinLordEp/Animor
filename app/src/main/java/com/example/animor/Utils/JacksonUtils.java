package com.example.animor.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class JacksonUtils {
    private static ObjectMapper mapper = null;

    public static <T> T readEntity(String json, TypeReference<T> typeReference){
        if(mapper == null){
            mapper = new ObjectMapper();
        }
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> List<T> readEntities(String json, TypeReference<List<T>> typeReference){
        if(mapper == null){
            mapper = new ObjectMapper();
        }
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public static <T> String entityToJson(T entity){
        if(mapper == null){
            mapper = new ObjectMapper();
        }
        try{
            return mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            //implement logger if needed
            throw new RuntimeException("Convert entity to json failed, please check getter setter of this model");
        }
    }
}
