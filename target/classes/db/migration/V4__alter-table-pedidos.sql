ALTER TABLE pagamentos MODIFY COLUMN status ENUM('pendente', 'aprovado','cancelado') DEFAULT 'pendente';
