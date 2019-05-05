package edu.eci.arsw.cinema.persistence.impl;

import edu.eci.arsw.cinema.filter.Filter;
import edu.eci.arsw.cinema.model.Cinema;
import edu.eci.arsw.cinema.model.CinemaFunction;
import edu.eci.arsw.cinema.model.Movie;
import edu.eci.arsw.cinema.persistence.CinemaException;
import edu.eci.arsw.cinema.persistence.CinemaPersistenceException;
import edu.eci.arsw.cinema.persistence.CinemaPersitence;
import edu.eci.arsw.cinema.util.RedisMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author cristian
 */

@Service
public class RedisCinemaPersistence implements CinemaPersitence {

    @Autowired
    private Filter filter = null;

    private final ConcurrentHashMap<String, Cinema> cinemas = new ConcurrentHashMap<>();

    public RedisCinemaPersistence() {
        // load stub data
        String functionDate1 = "2018-12-18 15:30";
        String functionDate2 = "2018-12-18 15:30";       
        List<CinemaFunction> functionsX = new ArrayList<>();
        List<CinemaFunction> functionsY = new ArrayList<>();
        Cinema c1 = new Cinema("cinemaX", functionsX);
        Cinema c2 = new Cinema("cinemaY", functionsY);
        CinemaFunction funct1 = new CinemaFunction(new Movie("SuperHeroes Movie", "Action"), functionDate1);
        CinemaFunction funct2 = new CinemaFunction(new Movie("The Night", "Horror"), functionDate1);
        CinemaFunction funct3 = new CinemaFunction(new Movie("Green Book", "Mystery"), functionDate1);
        CinemaFunction funct4 = new CinemaFunction(new Movie("Harry Potter", "Fantasy"), functionDate1);    
        
        cinemas.put("cinemaX", c1);
        cinemas.put("cinemaY", c2);        
    
        //LOAD DATA FROM REDIS
        funct1.setSeats(RedisMethods.getSeatsRedis("cinemaX",funct1));
        funct2.setSeats(RedisMethods.getSeatsRedis("cinemaX",funct2));
        funct3.setSeats(RedisMethods.getSeatsRedis("cinemaY",funct3));
        funct4.setSeats(RedisMethods.getSeatsRedis("cinemaY",funct4));        
        
        functionsX.add(funct1);
        functionsX.add(funct2);
        functionsY.add(funct3);
        functionsY.add(funct4);  
    }

    @Override
    public void buyTicket(int row, int col, String cinema, String date, String movieName) throws CinemaException {
        CinemaFunction function = getFunctionsbyCinemaDateName(cinema, date, movieName);        
        List<List<Boolean>> seats = RedisMethods.buyTicketRedis(cinema, function, row, col);
        function.setSeats(seats);
    }

    @Override
    public List<CinemaFunction> getFunctionsbyCinemaAndDate(String cinema, String date) throws CinemaException {
        try {
            Cinema cine = cinemas.get(cinema);
            List<CinemaFunction> funciones = new ArrayList<CinemaFunction>();
            for (CinemaFunction funcion : cine.getFunctions()) {
                if (funcion.getDate().equals(date)) {
                    funciones.add(funcion);
                }
            }

            return funciones;
        } catch (Exception e) {
            throw new CinemaException("Cinema not found");
        }

    }

    @Override
    public CinemaFunction getFunctionsbyCinemaDateName(String cinema, String date, String name) throws CinemaException {
        CinemaFunction cinemaFunction = null;
        try {
            Cinema cine = cinemas.get(cinema);

            for (CinemaFunction funcion : cine.getFunctions()) {
                if (funcion.getMovie().getName().equals(name)) {
                    cinemaFunction = funcion;
                }
            }
        } catch (Exception e) {
            throw new CinemaException("Cinema not found");
        }

        return cinemaFunction;

    }

    @Override
    public void saveCinema(Cinema c) throws CinemaPersistenceException {

        if (cinemas.containsKey(c.getName())) {

            throw new CinemaPersistenceException("The given cinema already exists: " + c.getName());
        } else {
            cinemas.put(c.getName(), c);
        }
    }

    @Override
    public Cinema getCinemaByName(String name) throws CinemaPersistenceException {
        if (cinemas.get(name) == null)
            throw new CinemaPersistenceException("cine no encontrado");
        return cinemas.get(name);
    }

    @Override
    public Map<String, Cinema> getCinemas() {
        return this.cinemas;
    }

    @Override
    public List<Movie> filterMovies(String cinema, String fecha, String filter) throws CinemaException {
        Cinema c = cinemas.get(cinema);
        List<Movie> movies = this.filter.filter(c, fecha, filter);
        return movies;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

}
