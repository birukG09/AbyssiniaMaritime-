use std::io::{self, Read};

fn haversine_nm(lat1: f64, lon1: f64, lat2: f64, lon2: f64) -> f64 {
    let r_km = 6371.0f64;
    let to_rad = |d: f64| d.to_radians();
    let dlat = to_rad(lat2 - lat1);
    let dlon = to_rad(lon2 - lon1);
    let a = ((dlat/2.0).sin().powi(2)) + to_rad(lat1).cos() * to_rad(lat2).cos() * ((dlon/2.0).sin().powi(2));
    let c = 2.0 * a.sqrt().atan2((1.0-a).sqrt());
    let km = r_km * c;
    km / 1.852 // km to nautical miles
}

fn main() {
    let mut input = String::new();
    io::stdin().read_to_string(&mut input).unwrap();
    // simple parse: {"origin_lat":..,"origin_lon":..,"dest_lat":..,"dest_lon":..,"avg_knots":..}
    let fetch = |key: &str| -> f64 {
        let pat = format!("\"{}\":", key);
        let start = input.find(&pat).unwrap_or(0) + pat.len();
        let tail = &input[start..];
        let number: String = tail.chars().take_while(|c| c.is_numeric() || *c=='.' || *c=='-').collect();
        number.parse::<f64>().unwrap_or(0.0)
    };

    let o_lat = fetch("origin_lat");
    let o_lon = fetch("origin_lon");
    let d_lat = fetch("dest_lat");
    let d_lon = fetch("dest_lon");
    let knots = fetch("avg_knots").max(1.0);

    let dist_nm = haversine_nm(o_lat, o_lon, d_lat, d_lon);
    let eta_hours = dist_nm / knots;
    println!("{{\"distance_nm\":{:.3},\"eta_hours\":{:.3}}}", dist_nm, eta_hours);
}