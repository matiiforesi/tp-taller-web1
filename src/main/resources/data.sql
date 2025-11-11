INSERT INTO Usuario(id, email, password, rol, activo, nombre, puntaje, monedas) VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true, null, 0, 0);

-- Dificultades
INSERT INTO Dificultad(id, nombre) VALUES (null, 'Easy');
INSERT INTO Dificultad(id, nombre) VALUES (null, 'Medium');
INSERT INTO Dificultad(id, nombre) VALUES (null, 'Hard');
-- Items
INSERT INTO Item (id,descripcion, tipoItem, precio) VALUES (null,'Duplicar puntaje', 'DUPLICAR_PUNTAJE', 150);
INSERT INTO Item (id,descripcion, tipoItem, precio) VALUES (null,'Quitar dos incorrectas', 'ELIMINAR_DOS_INCORRECTAS', 100);

-- Cuestionarios
INSERT INTO Cuestionario (id, nombre, descripcion, categoria, dificultad_id) VALUES
(1, 'Trivia Historia', 'Preguntas básicas sobre historia mundial', 'Historia', 1),
(2, 'Trivia Ciencia', 'Preguntas sobre descubrimientos y científicos famosos', 'Ciencia', 2),
(3, 'Trivia Cine', 'Preguntas sobre películas clásicas y modernas', 'Entretenimiento', 3);

-- Preguntas Cuestionario 1 (Historia)
INSERT INTO Preguntas (id, enunciado, categoria, dificultad_id, respuesta_correcta, respuesta_incorrecta_1, respuesta_incorrecta_2, respuesta_incorrecta_3, cuestionario_id) VALUES
(1, '¿En qué año comenzó la Primera Guerra Mundial?', 'Historia', 1, '1914', '1918', '1939', '1925', 1),
(2, '¿Quién fue el primer emperador romano?', 'Historia', 1, 'Augusto', 'Julio Cesar', 'Neron', 'Trajano', 1);

-- Preguntas Cuestionario 2 (Ciencia)
INSERT INTO Preguntas (id, enunciado, categoria, dificultad_id, respuesta_correcta, respuesta_incorrecta_1, respuesta_incorrecta_2, respuesta_incorrecta_3, cuestionario_id) VALUES
(3, '¿Cuál es el elemento químico con símbolo O?', 'Ciencia', 2, 'Oxigeno', 'Oro', 'Osmio', 'Ozono', 2),
(4, '¿Quién desarrolló la teoría de la relatividad?', 'Ciencia', 2, 'Albert Einstein', 'Isaac Newton', 'Galileo Galilei', 'Nikola Tesla', 2);

-- Preguntas Cuestionario 3 (Cine)
INSERT INTO Preguntas (id, enunciado, categoria, dificultad_id, respuesta_correcta, respuesta_incorrecta_1, respuesta_incorrecta_2, respuesta_incorrecta_3, cuestionario_id) VALUES
(5, '¿Quién dirigió la película “Titanic”?', 'Entretenimiento', 3, 'James Cameron', 'Steven Spielberg', 'Ridley Scott', 'Christopher Nolan', 3),
(6, '¿En qué año se estrenó “El Padrino”?', 'Entretenimiento', 3, '1972', '1980', '1969', '1975', 3);

