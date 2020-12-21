package root.application.infrastructure.exchange_gateway.stub;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvBarProvider
{
    public List<Bar> getBars()
    {
        List<Bar> bars = new ArrayList<>();
        InputStream stream = CsvBarProvider.class.getClassLoader().getResourceAsStream("data/ohlcvt-1m-1.csv");
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8), ',', '"', 0))
        {
            String[] line;
            while ((line = csvReader.readNext()) != null)
            {
                Bar bar = buildBar(line, Duration.ofMinutes(1));
                bars.add(bar);
            }
        }
        catch (IOException ioe)
        {
            log.error("Unable to load bars from CSV", ioe);
        }
        catch (NumberFormatException nfe)
        {
            log.error("Error while parsing value", nfe);
        }
        return bars;
    }

    private Bar buildBar(String[] line, Duration duration)
    {
        String open = line[0];
        String high = line[1];
        String low = line[2];
        String close = line[3];
        String volume = line[4];
        Instant barTime = Instant.ofEpochMilli(Long.parseLong(line[5]));
        ZonedDateTime zonedBarTime = ZonedDateTime.ofInstant(barTime, ZoneId.systemDefault());
        return new BaseBar(duration, zonedBarTime, open, high, low, close, volume);
    }
}
