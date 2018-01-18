package ufo.service.implementation;

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
import org.springframework.beans.factory.annotation.Autowired;
import ufo.dto.UfoSighting;
import ufo.exception.ServiceException;
import ufo.service.UfoSightingService;

/**
 * UfoSightingServiceImpl contains operations for getting allSightings amd
 * search based on year and date
 */
public class UfoSightingServiceImpl implements UfoSightingService {
    
    private static final Logger logger       = Logger.getLogger(UfoSightingServiceImpl.class);

    @Autowired
    private UfoSighting ufoSighting;

    /**
     * Constants created to represent the indexes in the tsv file and file delimiter
     */
    private static final String FILE_DELIMITER = "\\t";
    private static final int DATESEEN_INDEX = 0;
    private static final int DATEREPORTED_INDEX = 1;
    private static final int PLACESEEN_INDEX = 2;
    private static final int SHAPE_INDEX = 3;
    private static final int DURATION_INDEX = 4;
    private static final int DESCRIPTION_INDEX = 5;
    private static final int YEAR_MONTH_VALID_LENGTH = 6;

    List<UfoSighting> allSightings = new ArrayList<UfoSighting>();
    
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
            while (StringUtils.isNotBlank(line) && StringUtils.isNotEmpty(line)) {
                
                ufoSighting = new UfoSighting();
                String[] lineIndex = line.split(FILE_DELIMITER);
                if (lineIndex.length < YEAR_MONTH_VALID_LENGTH) {
                    logger.info(String.format("Invalid data at line [%s]", line));
                    
                } else {
                    String dateSeen = lineIndex[DATESEEN_INDEX].trim();
                    String dateReported = lineIndex[DATEREPORTED_INDEX].trim();
                    String placeSeen = lineIndex[PLACESEEN_INDEX].trim();
                    String shape = lineIndex[SHAPE_INDEX].trim();
                    String duration = lineIndex[DURATION_INDEX].trim();
                    String description = lineIndex[DESCRIPTION_INDEX].trim();
                    ufoSighting = new UfoSighting(dateSeen, dateReported, placeSeen, shape, duration, description);
                    allSightings.add(ufoSighting);
                    
                }
                line = bufferedReader.readLine();

                /**
                 * Check for the NullPointer Exception
                 */
                if (line != null) {
                    line = line.trim();
                }
            }
            
            long end = System.currentTimeMillis();
            long diff = end - start;
            logger.info("Searched allSightings of size  " + allSightings.size() + " in time period of " + diff + " milliseconds.");
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
            logger.info("Sightings of " + year + " and " + month + " is : " + searchSightings.size());

        } catch (ServiceException serviceException) {
            logger.error("Service Exception is thrown from search() with error message: "
                    + serviceException.getMessage());
        }
        return searchSightings;
    }
}
