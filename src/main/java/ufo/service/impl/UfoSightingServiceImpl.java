package ufo.service.impl;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ufo.dto.UfoSighting;
import ufo.exception.ServiceException;
import ufo.service.UfoSightingService;

/**
 * UfoSightingServiceImpl contains operations for getting allSightings amd
 * search based on year and date
 */
public class UfoSightingServiceImpl implements UfoSightingService {
    
    private static final Logger logger       = Logger.getLogger(UfoSightingServiceImpl.class);
    private UfoSighting         ufoSighting;
    List<UfoSighting>           allSightings = new ArrayList<UfoSighting>();
    
    /**
     * Method to get all the Sightings from the tsv file
     */
    public List<UfoSighting> getAllSightings() throws ServiceException {
        
        if (allSightings.size() > 0) {
            return allSightings;
        }
        fetchAllSightings();
        return allSightings;
    }
    
    public void fetchAllSightings() {
        try {
            Path path = Paths.get("src/main/resources/ufo_awesome.tsv");
            InputStream inputFileStream = new FileInputStream(String.valueOf(path));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputFileStream));
            
            String line = bufferedReader.readLine();
            long start = System.currentTimeMillis();
            while (line != null && StringUtils.isNotEmpty(line)) {
                
                ufoSighting = new UfoSighting();
                String[] lineIndex = line.split("\\t");
                if (lineIndex.length < 6) {
                    logger.warn(String.format("Invalid data", line));
                    
                } else {
                    String dateSeen = lineIndex[0];
                    String dateReported = lineIndex[1];
                    String placeSeen = lineIndex[2];
                    String shape = lineIndex[3];
                    String duration = lineIndex[4];
                    String description = lineIndex[5];
                    ufoSighting = new UfoSighting(dateSeen, dateReported, placeSeen, shape, duration, description);
                    allSightings.add(ufoSighting);
                    
                }
                line = bufferedReader.readLine();
                if (line != null) {
                    line = line.trim();
                }
            }
            
            long end = System.currentTimeMillis();
            long diff = end - start;
            logger.info("Size of allSightings : " + allSightings.size());
            bufferedReader.close();
        } catch (IOException ioException) {
            throw new ServiceException(ioException);
        } catch (Exception exception) {
            throw new ServiceException(exception);
        }
    }
    
    /**
     * Method to get the Sightings based on the year and month
     */
    public List<UfoSighting> search(int yearSeen, int monthSeen) throws ServiceException {
        String year = String.valueOf(yearSeen);
        String month = String.format("%02d", monthSeen);
        
        List<UfoSighting> searchSightings = new ArrayList<UfoSighting>();
        
        if (allSightings != null) {
            allSightings = getAllSightings();
        }
        try {
            
            for (UfoSighting ufoSighting : allSightings) {
                if (ufoSighting.getDateSeen().startsWith(year + month)) {
                    searchSightings.add(ufoSighting);
                }
            }
            System.out.println("Sightings of " + year + " and " + month + "is : " + searchSightings.size());
            ;
        } catch (ServiceException serviceException) {
            logger.error("Service Exception is thrown from search() with error message: "
                    + serviceException.getStackTrace());
        }
        return searchSightings;
    }
}
