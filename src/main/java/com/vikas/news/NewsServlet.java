package com.vikas.news;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class NewsServlet extends HttpServlet {

    private String apiKey;
    private String baseUrl;

    @Override
    public void init() throws ServletException {

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new ServletException("config.properties not found in resources/");
            }

            Properties props = new Properties();
            props.load(input);

            apiKey = props.getProperty("NEWS_API_KEY");
            baseUrl = props.getProperty("NEWS_URL");

        } catch (IOException e) {
            throw new ServletException("Error loading config.properties", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        URL url = new URL(baseUrl + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            json.append(line);
        }

        JSONObject jsonObj = new JSONObject(json.toString());

        String headline = jsonObj.getJSONArray("articles")
                                 .getJSONObject(0)
                                 .getString("title");

        req.setAttribute("headline", headline);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
