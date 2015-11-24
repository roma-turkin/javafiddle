package ru.javafiddle.web.services;

import javax.ejb.EJB;
import javax.ws.rs.Path;

/**
 * Created by artyom on 22.11.15.
 */
@Path("/")
public class LibraryService {

    @EJB
    LibrariesBean librariesBean;


}
