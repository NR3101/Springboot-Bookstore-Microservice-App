truncate table orders cascade;
alter sequence order_id_seq restart with 100;
alter sequence order_item_id_seq restart with 100;

insert into orders (id, order_number, username,
                    customer_name, customer_email, customer_phone,
                    delivery_address_line1, delivery_address_line2,
                    delivery_address_city, delivery_address_state,
                    delivery_address_zip_code, delivery_address_country,
                    status, comments)
values (100, 'order-123', 'neeraj',
        'John Doe', 'john.doe@example.com', '1234567890',
        '123 Main St', 'Apt 4B', 'Anytown', 'CA', '12345', 'USA',
        'NEW', 'Order created'),
       (101, 'order-456', 'neeraj',
        'Jane Smith', 'jane.smith@example.com', '0987654321',
        '456 Elm St', 'Suite 5C', 'Othertown', 'NY', '67890', 'USA',
        'NEW', 'Order created');

insert into order_items (order_id, code, name, price, quantity)
values (100, 'P100', 'Product A', 19.99, 2),
       (100, 'P104', 'Product B', 9.99, 1),
       (101, 'P102', 'Product C', 14.99, 3);