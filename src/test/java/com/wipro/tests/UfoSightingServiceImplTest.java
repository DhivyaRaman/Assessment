package com.wipro.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ufo.dto.UfoSighting;
import ufo.exception.ServiceException;
import ufo.service.implementation.UfoSightingServiceImpl;

/**
* Test Class to test  getAllSightings() and search()
*/
public class UfoSightingServiceImplTest {
    
    UfoSightingServiceImpl ufoSightingServiceImpl = null;
    @Before 
    public void setUp(){
        ufoSightingServiceImpl = new UfoSightingServiceImpl();
        ufoSightingServiceImpl.getAllSightings();
    }

    @Test
    public void getAllSightingsTest() throws ServiceException, FileNotFoundException {
        List<UfoSighting> allSightings = ufoSightingServiceImpl.getAllSightings();
        assertEquals(61391, allSightings.size());
    }

    @Test
    public void InvalidSighting() throws ServiceException{
        List<UfoSighting> allSightings = ufoSightingServiceImpl.getAllSightings();
        assertNotNull(allSightings);
    }

    @Test
    public void searchTest() throws ServiceException {
         List<UfoSighting> allSightings = ufoSightingServiceImpl.search(1995, 10);
         assertEquals(107, allSightings.size());
    }

    @Test
    public void searchNoShapeAndDuration() throws ServiceException{
        List<UfoSighting> allSightings = ufoSightingServiceImpl.getAllSightings();
        UfoSighting FirstItem = allSightings.get(0);
        assertEquals("19951009", FirstItem.getDateSeen());
        assertEquals("19951009", FirstItem.getDateReported());
        assertEquals("Iowa City, IA", FirstItem.getPlaceSeen());
        assertEquals("", FirstItem.getShape());
        assertEquals("", FirstItem.getDuration());
        assertEquals("Man repts. witnessing &quot;flash, followed by a classic UFO, w/ a tailfin at back.&quot; Red color on top half of tailfin. Became triangular.", FirstItem.getDescription());
    }
}
