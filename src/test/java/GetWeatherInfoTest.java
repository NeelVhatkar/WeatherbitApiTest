import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class GetWeatherInfoTest {
    Properties properties = new Properties();
    public String apiKey="";

    //Following method will be used to get the server credentials/details before staring of the test execution
    @BeforeSuite
    public void getServerDetails() throws IOException {
        Reporter.log("--Test case has Started getting Server Details--");
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\resources\\properties\\global.properties");
        properties.load(fis);
        apiKey= (String) properties.get("apiKey");
    }


    //Following method fetches the Zipcodes/citynames from the Input data sheet and passes to the test method
    @DataProvider(name = "getZipCodes")
    public Object[][] getZipCodes() throws IOException {
        String queryParameter = "CityZipCodes";
        Object[][] arrayObject = ExcelReader.ReadExcelData(queryParameter);
        return arrayObject;
    }

    //Following method fetches the Zipcodes/citynames from the Input data sheet and passes to the test method
    @DataProvider(name = "getLatLong")
    public Object[][] getLatLong() throws IOException {
        String queryParameter = "Lat&Lon";
        Object[][] arrayObject = ExcelReader.ReadExcelData(queryParameter);
        return arrayObject;
    }


    //Based on the input data passed from the data provider this method will trigger construct the rest calls and will
    // fetch the response and assert for the status code and city names
    @org.testng.annotations.Test(enabled = true,dataProvider = "getZipCodes",priority = 0)
    public void getTheWeatherInfoPostalCodesValidateCity(String cityName, String Zipcode) {
        given().
                pathParam("postalCode", Zipcode).pathParam("apiKey", apiKey).
                when().
                log().
                all().
                get("http://api.weatherbit.io/v2.0/current?postal_code={postalCode}&key={apiKey}")
                .then().assertThat().statusCode(200).and()
                .log().all()
                .body("data.city_name", hasItem(cityName));

    }

    @org.testng.annotations.Test(enabled = true ,dataProvider = "getLatLong",priority = 1)
    public void getTheWeatherInfoLatLong(String countryCode,String latitude, String longitude) {
        properties.get("baseUrlPostalCode");
        given().
                pathParam("latitude", latitude).pathParam("longitude",longitude).pathParam("apiKey", apiKey).
                when().
                log().
                all().
                get("http://api.weatherbit.io/v2.0/current?lat={latitude}&lon={longitude}&key={apiKey}")
                .then().assertThat().statusCode(200).and()
                .log().all()
                .body("data.country_code", hasItem(countryCode));

    }

}
