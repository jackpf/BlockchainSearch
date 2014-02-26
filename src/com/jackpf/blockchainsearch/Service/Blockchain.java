package com.jackpf.blockchainsearch.Service;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jackpf.blockchainsearch.Entity.ApiPath;

/**
 * Blockchain API service
 */
public class Blockchain
{
    /**
     * Raw http request
     * 
     * @param path
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String rawRequest(ApiPath path) throws ClientProtocolException, IOException
    {
        HttpClient client = new DefaultHttpClient();
        
        HttpUriRequest request = new HttpGet(path.getPath());
        HttpResponse response = client.execute(request);
        
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
        
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Invalid request: " + writer.toString());
        }
        
        return writer.toString();
    }
    
    /**
     * Perform an api request and parse the response json
     * 
     * @param path
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws ParseException
     */
    public JSONObject request(ApiPath path)  throws ClientProtocolException, IOException, ParseException
    {
        String response = rawRequest(path);
        
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) jsonParser.parse(response);
        
        return jsonResponse;
    }
}
