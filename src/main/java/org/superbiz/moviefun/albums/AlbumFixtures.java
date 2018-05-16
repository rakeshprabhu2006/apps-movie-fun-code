package org.superbiz.moviefun.albums;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType.NUMBER;
import static java.util.Arrays.asList;
import static org.superbiz.moviefun.CsvUtils.readFromCsv;

@Component
public class AlbumFixtures {


    public List<Album> load() {
        return asList(
                new Album("Massive Attack", "Mezzanine", 1998, 9),
                new Album("Radiohead", "OK Computer", 1997, 8),
                new Album("Radiohead", "Kid A", 2000, 9)
        );
    }
}
