
import os

import psycopg
from psycopg.rows import dict_row


def get_connection():
    return psycopg.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=os.getenv("DB_PORT", "5432"),
        dbname=os.getenv("DB_NAME", "orderdb"),
        user=os.getenv("DB_USER", "orderuser"),
        password=os.getenv("DB_PASSWORD", "orderpass"),
        row_factory=dict_row,
    )
