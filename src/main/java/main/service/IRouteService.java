package main.service;

import main.model.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.util.List;

public interface IRouteService {
    public List<Route> getRoutes();

    public Page<Route> getRoutes(Pageable pageable);

    public void addRoute(Route route);

    public Route getRouteById(Long id);

    public void updateRoute(Route route);

    public void deleteRouteById(Long id) throws MessagingException;
}
