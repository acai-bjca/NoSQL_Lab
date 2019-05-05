/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import edu.eci.arsw.cinema.model.CinemaFunction;
import edu.eci.arsw.cinema.model.Movie;
import edu.eci.arsw.cinema.util.RedisMethods;

/**
 *
 * @author cristian
 */
@SpringBootApplication
@ComponentScan(basePackages = { "edu.eci.arsw.cinema" })
public class CinemaAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaAPIApplication.class, args);
        //RedisMethods.saveToREDIS("1", "Prueba save 1");
        //System.out.println(RedisMethods.getFromREDIS("1"));
        //System.out.println(RedisMethods.getSeatsRedis("cinemaY", new CinemaFunction(new Movie("SuperHeroes Movie", "Action"), "2018-12-18 17:00")));
    }
}
