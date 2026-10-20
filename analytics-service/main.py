
from fastapi import FastAPI

from database import get_connection

app = FastAPI(title="Order Processing Analytics")


@app.get("/")
def root():
    return {"message": "Order Processing Analytics API"}


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/analytics/summary")
def analytics_summary():
    with get_connection() as conn:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT
                    COUNT(*) AS total_orders,
                    COALESCE(SUM(total_amount), 0) AS total_revenue
                FROM customer_orders
                """
            )
            result = cur.fetchone()

    return {
        "total_orders": result["total_orders"],
        "total_revenue": result["total_revenue"],
    }


@app.get("/analytics/products")
def product_inventory():
    with get_connection() as conn:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT id, name, price, stock
                FROM products
                ORDER BY id
                """
            )
            products = cur.fetchall()

    return products


@app.get("/analytics/inventory")
def inventory_summary():
    with get_connection() as conn:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT
                    COUNT(*) AS total_products,
                    COALESCE(SUM(stock), 0) AS total_units,
                    COALESCE(SUM(price * stock), 0) AS inventory_value
                FROM products
                """
            )
            result = cur.fetchone()

    return result
