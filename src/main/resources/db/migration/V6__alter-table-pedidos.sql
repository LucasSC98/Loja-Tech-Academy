ALTER TABLE pedidos MODIFY COLUMN status ENUM('pendente', 'enviado', 'entregue') DEFAULT 'pendente';
