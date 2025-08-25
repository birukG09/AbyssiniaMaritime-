import sys, json, re

data = sys.stdin.read().strip()
try:
    req = json.loads(data)
except Exception:
    def get(k):
        m = re.search(rf'"{k}":\s*([0-9.]+)', data)
        return float(m.group(1)) if m else 0.0
    req = {
        "distance_nm": get("distance_nm"),
        "cargo_tons": get("cargo_tons"),
        "fuel_price_per_ton": get("fuel_price_per_ton"),
        "port_fees_usd": get("port_fees_usd"),
        "base_rate_usd_per_nm": get("base_rate_usd_per_nm"),
    }

distance_nm = float(req.get("distance_nm", 0))
cargo_tons = float(req.get("cargo_tons", 0))
fuel_price = float(req.get("fuel_price_per_ton", 650))
port_fees = float(req.get("port_fees_usd", 0))
base_rate = float(req.get("base_rate_usd_per_nm", 2.5))

# Simplified fuel model: ~2.5 tons/hour at 16 knots => ~0.15625 tons per nm
tons_per_nm = 2.5 / 16.0
fuel_tons_est = distance_nm * tons_per_nm
fuel_cost = fuel_tons_est * fuel_price
distance_cost = distance_nm * base_rate

total = fuel_cost + distance_cost + port_fees

out = {
    "fuel_tons_est": round(fuel_tons_est, 3),
    "fuel_cost_usd": round(fuel_cost, 2),
    "distance_cost_usd": round(distance_cost, 2),
    "total_cost_usd": round(total, 2)
}
sys.stdout.write(json.dumps(out))