import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.Restaurants;
import data.SearchResponse;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;


public class ApiTestingTaskTest {

    private HttpGet request;

    // Test data
    private static final String VALID_TOKEN = "Token 9d5cae911bedd4bfa2831684896ffcfa716a3bce";
    private static final String INVALID_TOKEN = "Invalid Token";
    private static final String SOME_VALID_ADDRESS = "Rathausstrasse 2, 1010 Wien";
    private static final String QUERY_PARAM_ADDRESS = "address";
    private static final String HEADER_PARAM_AUTH = "Authorization";


    @Before
    public void setUp() throws Exception {

        URI uri = new URIBuilder().setPath(ApiTestingTask.BASE_URL)
                .addParameter(QUERY_PARAM_ADDRESS, SOME_VALID_ADDRESS).build();

        request = new HttpGet(uri.toString());
    }

    @Test
    public void searchAPI_whenInvalidToken_then_returnHttpStatus401() throws IOException {

        request.addHeader(HEADER_PARAM_AUTH, INVALID_TOKEN);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void searchAPI_whenValidToken_then_returnHttpStatus200() throws IOException {

        request.addHeader(HEADER_PARAM_AUTH, VALID_TOKEN);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void searchAPI_whenValidToken_then_returnValidContentType() throws IOException {

        request.addHeader("Authorization", VALID_TOKEN);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(response.getEntity().getContentType().getValue(), ContentType.APPLICATION_JSON.getMimeType());
    }

    @Test
    public void searchAPI_whenValidToken_then_returnValidPayload() throws IOException {

        request.addHeader("Authorization", VALID_TOKEN);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        SearchResponse searchResponse = retrieveResourceFromResponse(response, SearchResponse.class);
        Assert.assertEquals(searchResponse != null, true);
    }


    @Test
    public void searchAPI_whenValidTokenAndValidAddress_then_returnSomeRestaurant() throws IOException {

        request.addHeader("Authorization", VALID_TOKEN);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        SearchResponse searchResponse = retrieveResourceFromResponse(response, SearchResponse.class);
        Assert.assertNotEquals(searchResponse.getRestaurants().size(), 0);
    }

    @Test
    public void searchAPI_whenValidTokenAndValidAddress_then_returnSomeRestaurantWithStarRankGreaterThan3() throws IOException {

        request.addHeader("Authorization", VALID_TOKEN);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        SearchResponse searchResponse = retrieveResourceFromResponse(response, SearchResponse.class);

        boolean isHighRank = false;
        for (Restaurants restaurant : searchResponse.getRestaurants()) {

            if(restaurant.getStar_rank() > 3){
                isHighRank = true;
                break;
            }
        }

        Assert.assertTrue(isHighRank);
    }


    @Test
    public void searchAPI_whenValidTokenAndValidAddress_then_checkIfAnyRestaurantIsOpenNow() throws IOException {

        request.addHeader("Authorization", VALID_TOKEN);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        SearchResponse searchResponse = retrieveResourceFromResponse(response, SearchResponse.class);

        boolean isNowOpen = false;
        for (Restaurants restaurant : searchResponse.getRestaurants()) {

            if(restaurant.isNow_open()){
                isNowOpen = true;
                break;
            }
        }

        Assert.assertTrue(isNowOpen);
    }



    /**
     * Function to bind json to our data class
     * @param response
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz)
            throws IOException {

        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonFromResponse, clazz);
    }
}