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

import android.util.Log;

import com.jackpf.blockchainsearch.Entity.ApiPath;

/**
 * Blockchain API service
 */
public class Blockchain
{
	/**
	 * Perform an api request
	 * 
	 * @param path
	 * @return JSONObject
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONObject request(ApiPath path) throws ClientProtocolException, IOException, ParseException
	{
		HttpClient client = new DefaultHttpClient();
		
		HttpUriRequest request = new HttpGet(path.getPath());
		HttpResponse response = client.execute(request);
		
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
		
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Invalid request: " + writer.toString());
		}
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(writer.toString());
		
		return jsonResponse;
	}
}
