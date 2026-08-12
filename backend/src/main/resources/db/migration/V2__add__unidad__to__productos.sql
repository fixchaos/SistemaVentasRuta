ALTER TABLE productos
ADD COLUMN unidad VARCHAR(30);

UPDATE productos
SET unidad = 'unidad'
WHERE id = 1;

ALTER TABLE productos
ALTER COLUMN unidad SET NOT NULL;
