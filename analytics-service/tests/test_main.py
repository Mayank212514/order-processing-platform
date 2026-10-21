
from fastapi.testclient import TestClient
from unittest.mock import MagicMock, patch

from main import app

client = TestClient(app)


def test_root_endpoint():
    response = client.get("/")

    assert response.status_code == 200
    assert response.json() == {
        "message": "Order Processing Analytics API"
    }


def test_health_endpoint():
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok"}

@patch("main.get_connection")
def test_analytics_summary(mock_get_connection):
    mock_conn = MagicMock()
    mock_cursor = MagicMock()

    mock_get_connection.return_value.__enter__.return_value = mock_conn
    mock_conn.cursor.return_value.__enter__.return_value = mock_cursor

    mock_cursor.fetchone.return_value = {
        "total_orders": 3,
        "total_revenue": 1250.50,
    }

    response = client.get("/analytics/summary")

    assert response.status_code == 200
    assert response.json() == {
        "total_orders": 3,
        "total_revenue": 1250.50,
    }


@patch("main.get_connection")
def test_analytics_inventory(mock_get_connection):
    mock_conn = MagicMock()
    mock_cursor = MagicMock()

    mock_get_connection.return_value.__enter__.return_value = mock_conn
    mock_conn.cursor.return_value.__enter__.return_value = mock_cursor

    mock_cursor.fetchone.return_value = {
        "total_products": 2,
        "total_units": 15,
        "inventory_value": 2500.00,
    }

    response = client.get("/analytics/inventory")

    assert response.status_code == 200
    assert response.json() == {
        "total_products": 2,
        "total_units": 15,
        "inventory_value": 2500.00,
    }


@patch("main.get_connection")
def test_analytics_products(mock_get_connection):
    mock_conn = MagicMock()
    mock_cursor = MagicMock()

    mock_get_connection.return_value.__enter__.return_value = mock_conn
    mock_conn.cursor.return_value.__enter__.return_value = mock_cursor

    mock_cursor.fetchall.return_value = [
        {"id": 1, "name": "Laptop", "price": 999.99, "stock": 10},
        {"id": 2, "name": "Mouse", "price": 25.00, "stock": 5},
    ]

    response = client.get("/analytics/products")

    assert response.status_code == 200
    assert response.json() == [
        {"id": 1, "name": "Laptop", "price": 999.99, "stock": 10},
        {"id": 2, "name": "Mouse", "price": 25.00, "stock": 5},
    ]
