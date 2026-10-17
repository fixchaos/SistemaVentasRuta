ALTER TABLE usuarios
    ALTER COLUMN nombre TYPE VARCHAR(160);

ALTER TABLE usuarios
    ADD COLUMN rut VARCHAR(20) NOT NULL;

ALTER TABLE usuarios
    ADD COLUMN email VARCHAR(160) NOT NULL;

ALTER TABLE usuarios
    DROP CONSTRAINT IF EXISTS usuarios_rol_check;

ALTER TABLE usuarios
    ADD CONSTRAINT ck_usuario_rol
        CHECK (rol IN ('ADMIN', 'USUARIO'));

ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuario_rut UNIQUE (rut);

ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuario_email UNIQUE (email);