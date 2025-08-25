package com.abyssinia;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Json {
    // Minimal JSON builder for simple key-values (no external libs)
    static String obj(Map<String, Object> m) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var e : m.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(e.getKey()).append("\":");
            Object v = e.getValue();
            if (v instanceof Number || v instanceof Boolean) sb.append(v.toString());
            else sb.append("\"").append(v.toString().replace("\"","\\\"")).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}

public class App {
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static String read(String prompt) throws IOException {
        System.out.print(prompt);
        return in.readLine();
    }

    static String runProcess(String[] cmd, String inputJson) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (OutputStream os = p.getOutputStream()) {
            if (inputJson != null) os.write((inputJson + "\n").getBytes(StandardCharsets.UTF_8));
        }
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        p.waitFor();
        return out;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("⚓ Abyssinia Maritime CLI v0.1");
        while (true) {
            System.out.println("\n1) Compute Route & ETA  2) Compute Cost  3) Demo Full Flow  0) Exit");
            String choice = read("> ");
            switch (choice) {
                case "1": computeRoute(); break;
                case "2": computeCost(); break;
                case "3": demoFlow(); break;
                case "0": System.out.println("Bye."); return;
                default: System.out.println("Invalid.");
            }
        }
    }

    static void computeRoute() throws Exception {
        double oLat = Double.parseDouble(read("Origin lat: "));
        double oLon = Double.parseDouble(read("Origin lon: "));
        double dLat = Double.parseDouble(read("Dest lat: "));
        double dLon = Double.parseDouble(read("Dest lon: "));
        double knots = Double.parseDouble(read("Avg speed (knots): "));

        Map<String,Object> req = new LinkedHashMap<>();
        req.put("origin_lat", oLat); req.put("origin_lon", oLon);
        req.put("dest_lat", dLat); req.put("dest_lon", dLon);
        req.put("avg_knots", knots);

        String json = Json.obj(req);
        String bin = System.getenv().getOrDefault("ROUTE_BIN", "bin/route_optimizer");
        String[] cmd = new String[]{bin};
        String out = runProcess(cmd, json);
        System.out.println("Route Engine -> " + out);
    }

    static void computeCost() throws Exception {
        double distanceNm = Double.parseDouble(read("Distance (nm): "));
        double cargoTons = Double.parseDouble(read("Cargo (tons): "));
        double fuelPrice = Double.parseDouble(read("Fuel price per ton (USD): "));
        double portFees = Double.parseDouble(read("Port fees (USD): "));
        double baseRate = Double.parseDouble(read("Base rate per nm (USD): "));

        Map<String,Object> req = new LinkedHashMap<>();
        req.put("distance_nm", distanceNm);
        req.put("cargo_tons", cargoTons);
        req.put("fuel_price_per_ton", fuelPrice);
        req.put("port_fees_usd", portFees);
        req.put("base_rate_usd_per_nm", baseRate);

        String json = Json.obj(req);
        String[] cmd = new String[]{"python3", "src/python/cost_engine.py"};
        String out = runProcess(cmd, json);
        System.out.println("Cost Engine -> " + out);
    }

    static void demoFlow() throws Exception {
        System.out.println("Demo: Djibouti Port -> Jebel Ali (Dubai)");
        double oLat = 11.6000, oLon = 43.1500;
        double dLat = 25.2700, dLon = 55.3075;
        double knots = 16.0;

        Map<String,Object> routeReq = new LinkedHashMap<>();
        routeReq.put("origin_lat", oLat); routeReq.put("origin_lon", oLon);
        routeReq.put("dest_lat", dLat); routeReq.put("dest_lon", dLon);
        routeReq.put("avg_knots", knots);

        String routeOut = runProcess(new String[]{System.getenv().getOrDefault("ROUTE_BIN", "bin/route_optimizer")}, Json.obj(routeReq));
        System.out.println("Route Engine -> " + routeOut);

        // Extract distance_nm from simple JSON (no parser; rough extraction)
        double distanceNm = Double.parseDouble(routeOut.replaceAll(".*\"distance_nm\":([0-9.]+).*", "$1"));
        Map<String,Object> costReq = new LinkedHashMap<>();
        costReq.put("distance_nm", distanceNm);
        costReq.put("cargo_tons", 20.0);
        costReq.put("fuel_price_per_ton", 650.0);
        costReq.put("port_fees_usd", 1500.0);
        costReq.put("base_rate_usd_per_nm", 2.5);

        String costOut = runProcess(new String[]{"python3", "src/python/cost_engine.py"}, Json.obj(costReq));
        System.out.println("Cost Engine -> " + costOut);

        // Optional pretty print
        try {
            String formatted = runProcess(new String[]{"node", "src/node/formatter.js"}, "{\"route\":"+routeOut+",\"cost\":"+costOut+"}");
            System.out.println(formatted);
        } catch (Exception e) {
            System.out.println("(Node formatter not found; skipping pretty output)");
        }
    }
}