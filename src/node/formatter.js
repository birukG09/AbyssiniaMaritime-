// Reads {"route":{...},"cost":{...}} from stdin and prints a friendly summary
const fs = require('fs');
const input = fs.readFileSync(0, 'utf8').trim();
let data = {};
try { data = JSON.parse(input); } catch(e) { console.log("Invalid JSON"); process.exit(0); }
const r = data.route || {}, c = data.cost || {};
const num = x => typeof x === 'number' ? x.toFixed(2) : x;
console.log("=== Voyage Summary ===");
console.log(`Distance: ${num(r.distance_nm)} nm`);
console.log(`ETA: ${num(r.eta_hours)} hours`);
console.log("--- Costs ---");
console.log(`Fuel: $${num(c.fuel_cost_usd)} for ~${num(c.fuel_tons_est)} tons`);
console.log(`Distance: $${num(c.distance_cost_usd)}`);
console.log(`Port Fees: included`);
console.log(`Total: $${num(c.total_cost_usd)}`);