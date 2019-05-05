package edu.eci.arsw.cinema.util;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.eci.arsw.cinema.model.CinemaFunction;
import edu.eci.arsw.cinema.persistence.CinemaException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class RedisMethods {

    public static void saveToREDIS(String key, String data) {

        Jedis jedis = JedisUtil.getPool().getResource();

        // Operaciones
        jedis.watch(key);
        Transaction t1 = jedis.multi();
        t1.set(key, data);
        t1.exec();

        jedis.close();
    }

    public static String getFromREDIS(String key) {

        boolean intentar = true;
        String content = "";
        while (intentar) {
            // Inicializar jedis y obtener recursos
            Jedis jedis = JedisUtil.getPool().getResource();
            // Hacer watch de la llave
            jedis.watch(key);
            // Crear la transacción t
            Transaction t = jedis.multi();
            Response<String> data = t.get(key);
            List<Object> result = t.exec();
            if (result.size() > 0) {
                intentar = false;
                content = data.get();
                // Cerrar recurso jedis
                jedis.close();
            }
        }
        return content;
    }

    public static List<List<Boolean>> buyTicketRedis(String nameCinema, CinemaFunction function, int row, int col)
            throws CinemaException {
        List<List<Boolean>> seats = null;
        String key = nameCinema + function.getDate() + function.getMovie().getName();
        ObjectMapper mapper = new ObjectMapper();
        String seatsJson = getFromREDIS(key);
        try {
            seats = mapper.readValue(seatsJson, new TypeReference<List<List<Boolean>>>() {
            });
            seatsJson = mapper.writeValueAsString(seats);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!seats.get(row).get(col)) {
            seats.get(row).set(col, true);
        }
        saveToREDIS(key, seatsJson);
        return seats;
    }

    public static List<List<Boolean>> getSeatsRedis(String nameCinema, CinemaFunction function) {
        List<List<Boolean>> seats = null;
        String key = nameCinema + function.getDate() + function.getMovie().getName();
        ObjectMapper mapper = new ObjectMapper();
        String seatsJson = getFromREDIS(key);
        try {            
            seats = mapper.readValue(seatsJson, new TypeReference<List<List<Boolean>>>() {
            });
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seats;
    }

}