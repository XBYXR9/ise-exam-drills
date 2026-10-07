# Deployment diagram — hospital (draw in Apollon)

Nodes (3-D boxes), nothing extra:

| Node | Stereotype | Inside it |
|---|---|---|
| Smart Badge | `«device»` | artifact `Staff Identity Token` |
| Dispensing Cart | `«device»` | artifact `Cart UI`, component `Dispenser` |
| Transport Terminal | `«device»` | component `Dispatch Controller` |
| Central Pharmacy Server | `«machine»` | component `Pharmacy Manager` |
| Central Logistics Server | `«machine»` | component `Dispatch Manager` |

Communication paths (plain lines between nodes, no arrowhead, label = protocol):

- Smart Badge — Dispensing Cart: `NFC`
- Smart Badge — Transport Terminal: `NFC`
- Dispensing Cart — Central Pharmacy Server: `TLS`
- Transport Terminal — Central Logistics Server: `gRPC`

Interfaces (lollipop on the provider, socket on the requirer, named after the service):

- `Dispenser` (socket) requires → `Pharmacy Manager` (lollipop): **Prescription Authorization Service**
- `Dispatch Controller` (socket) requires → `Dispatch Manager` (lollipop): **Transport Scheduling Service**
- `Dispatch Controller` (socket) requires → `Dispatch Manager` (lollipop): **Orderly Availability Service**
