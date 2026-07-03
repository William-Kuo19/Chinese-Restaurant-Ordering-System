# Database

## Database Name

```
restaurant
```

## Tables

### member

Store member account information.

| Column | Description |
|---------|-------------|
| id | Primary Key |
| username | Login account |
| password | Login password |
| name | Member name |
| phone | Phone number |

---

### product

Store restaurant products.

| Column | Description |
|---------|-------------|
| id | Primary Key |
| name | Product name |
| category | Product category |
| price | Product price |
| description | Combo description (only used for combo meals) |

---

### orders

Store order information.

| Column | Description |
|---------|-------------|
| id | Primary Key |
| member_id | Member ID |
| total | Total amount |
| order_time | Order time |

---

### order_detail

Store ordered items.

| Column | Description |
|---------|-------------|
| id | Primary Key |
| order_id | Order ID |
| product_id | Product ID |
| quantity | Quantity |
| price | Unit price |

---

## SQL Script

```
sql/restaurant.sql
```