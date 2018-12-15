package Web.Services;

import java.util.ArrayList;
import java.util.List;

import Web.Beans.SearchBean;
import Web.models.AlbumSearchModel;

public class AlbumSearchService {
    @Override
    public List<Object> search(AlbumSearchModel query) {
        if (query instanceof AlbumSearchModel)
        {
            AlbumSearchModel queryArtist = (AlbumSearchModel) query;

            List<Object> results = new ArrayList<Object>();
            

            for (AlbumSearchModel album: cars)
            {
                boolean condition1 = Compare.strings(queryCar.getCarModel(), car.getCarModel());
                boolean condition2 = Compare.strings(queryCar.getManufacturer(), car.getManufacturer());
                boolean condition3 = Compare.integers(queryCar.getYear(), car.getYear());
                boolean condition4 = Compare.integers(queryCar.getPrice(), car.getPrice());

                // Assume AND condition
                if (condition1 & condition2 & condition3 & condition4)
                {
                    results.add(car);
                }
            }

            return results;

        }
        return null;
}
