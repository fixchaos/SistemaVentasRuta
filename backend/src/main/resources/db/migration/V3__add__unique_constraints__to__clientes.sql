ALTER TABLE clientes
ADD CONSTRAINT uk_cliente_telefono UNIQUE (telefono);

ALTER TABLE clientes
ADD CONSTRAINT uk_cliente_direccion UNIQUE (direccion);