-- Script d'initialisation pour les données de test
-- Ce fichier doit être placé dans src/main/resources/data.sql

-- Supprimer les données existantes pour éviter les doublons (optionnel)
-- DELETE FROM volunteer_registrations;
-- DELETE FROM volunteer_shifts;
-- DELETE FROM volunteer_availabilities;
-- DELETE FROM reservations;
-- DELETE FROM time_slots;
-- DELETE FROM events;
-- DELETE FROM inventory;
-- DELETE FROM basket_types;
-- DELETE FROM notifications;
-- DELETE FROM users;

-- Insérer les types de paniers
INSERT IGNORE INTO basket_types (id, name, description, is_active, created_at) VALUES
(1, 'Panier Standard', 'Panier alimentaire de base avec des produits essentiels', true, NOW()),
(2, 'Panier Végétarien', 'Panier adapté aux régimes végétariens', true, NOW()),
(3, 'Panier Familial', 'Panier plus volumineux pour les familles', true, NOW()),
(4, 'Panier Express', 'Panier de dépannage rapide', true, NOW());

-- Insérer des produits d'inventaire pour les types de paniers
INSERT IGNORE INTO inventory (id, product_name, quantity, expiration_date, basket_type_id, created_at, updated_at) VALUES
(1, 'Pâtes', 50, '2025-12-31', 1, NOW(), NOW()),
(2, 'Riz', 40, '2025-11-30', 1, NOW(), NOW()),
(3, 'Huile d''olive', 20, '2025-10-15', 1, NOW(), NOW()),
(4, 'Légumes en conserve', 30, '2025-09-01', 1, NOW(), NOW()),
(5, 'Légumineuses', 35, '2025-12-15', 2, NOW(), NOW()),
(6, 'Quinoa', 25, '2025-11-20', 2, NOW(), NOW()),
(7, 'Lait végétal', 15, '2025-08-30', 2, NOW(), NOW()),
(8, 'Céréales pour petit-déjeuner', 60, '2025-10-10', 3, NOW(), NOW()),
(9, 'Compote de fruits', 45, '2025-09-15', 3, NOW(), NOW()),
(10, 'Barres énergétiques', 100, '2025-12-01', 4, NOW(), NOW());

-- Insérer des événements de test
INSERT IGNORE INTO events (id, name, description, location, event_date, status, created_by, created_at, updated_at) VALUES
(1, 'Distribution Alimentaire Hebdomadaire', 'Distribution hebdomadaire de paniers alimentaires pour les étudiants', 'Campus Principal - Salle A101', '2025-01-20 10:00:00', 'PUBLISHED', 3, NOW(), NOW()),
(2, 'Distribution Spéciale Examens', 'Distribution spéciale pendant la période d''examens', 'Campus Principal - Salle B203', '2025-01-25 14:00:00', 'PUBLISHED', 3, NOW(), NOW()),
(3, 'Événement de Fin de Mois', 'Distribution de fin de mois avec paniers supplémentaires', 'Campus Nord - Hall Central', '2025-01-30 09:00:00', 'DRAFT', 3, NOW(), NOW());

-- Insérer des créneaux horaires pour les événements
INSERT IGNORE INTO time_slots (id, event_id, start_time, end_time, max_capacity, available_spots, created_at, updated_at) VALUES
(1, 1, '2025-01-20 10:00:00', '2025-01-20 12:00:00', 50, 45, NOW(), NOW()),
(2, 1, '2025-01-20 14:00:00', '2025-01-20 16:00:00', 50, 50, NOW(), NOW()),
(3, 2, '2025-01-25 14:00:00', '2025-01-25 16:00:00', 30, 28, NOW(), NOW()),
(4, 2, '2025-01-25 16:30:00', '2025-01-25 18:30:00', 30, 30, NOW(), NOW()),
(5, 3, '2025-01-30 09:00:00', '2025-01-30 11:00:00', 40, 40, NOW(), NOW());

-- Insérer des postes bénévoles
INSERT IGNORE INTO volunteer_shifts (id, time_slot_id, role_type, required_volunteers, min_experience_level, description, start_time, end_time, created_at) VALUES
(1, 1, 'ACCUEIL', 2, 'BEGINNER', 'Accueil et orientation des étudiants', '09:30:00', '12:15:00', NOW()),
(2, 1, 'DISTRIBUTION', 3, 'INTERMEDIATE', 'Distribution des paniers alimentaires', '10:00:00', '12:00:00', NOW()),
(3, 1, 'RANGEMENT', 2, 'BEGINNER', 'Rangement et nettoyage après distribution', '11:45:00', '12:30:00', NOW()),
(4, 2, 'PREPARATION', 2, 'EXPERIENCED', 'Préparation des paniers avant distribution', '13:30:00', '14:00:00', NOW()),
(5, 2, 'DISTRIBUTION', 4, 'INTERMEDIATE', 'Distribution des paniers alimentaires', '14:00:00', '16:00:00', NOW());

-- Insérer quelques réservations de test (les utilisateurs user1 et user2 ont des réservations)
INSERT IGNORE INTO reservations (id, user_id, time_slot_id, basket_type_id, status, notes, created_at, updated_at) VALUES
(1, 1, 1, 1, 'COMFIRMED', 'Première réservation de test', NOW(), NOW()),
(2, 2, 1, 2, 'COMFIRMED', 'Réservation panier végétarien', NOW(), NOW()),
(3, 1, 3, 1, 'COMFIRMED', 'Réservation pour la distribution spéciale', NOW(), NOW());

-- Insérer des inscriptions de bénévolat (user admin est inscrit comme bénévole)
INSERT IGNORE INTO volunteer_registrations (id, volunteer_shift_id, user_id, status, is_team_leader, notes, created_at, updated_at) VALUES
(1, 1, 3, 'CONFIRMED', true, 'Responsable de l''accueil', NOW(), NOW()),
(2, 2, 3, 'CONFIRMED', false, 'Aide à la distribution', NOW(), NOW());

-- Insérer des disponibilités de bénévoles
INSERT IGNORE INTO volunteer_availabilities (id, user_id, day_of_week, start_time, end_time, is_active, created_at) VALUES
(1, 3, 1, '09:00:00', '17:00:00', true, NOW()), -- Lundi
(2, 3, 3, '14:00:00', '18:00:00', true, NOW()), -- Mercredi
(3, 3, 5, '10:00:00', '16:00:00', true, NOW()); -- Vendredi

-- Insérer quelques notifications de test
INSERT IGNORE INTO notifications (id, user_id, type, status, content, sent_at, read_at) VALUES
(1, 1, 'RESERVATION_CONFIRMATION', 'SENT', 'Votre réservation pour la distribution du 20 janvier a été confirmée.', NOW(), NULL),
(2, 2, 'RESERVATION_CONFIRMATION', 'SENT', 'Votre réservation pour un panier végétarien a été confirmée.', NOW(), NULL),
(3, 3, 'VOLUNTEER_CONFIRMATION', 'READ', 'Votre inscription comme bénévole pour l''accueil a été confirmée.', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(4, 1, 'RESERVATION_REMINDER', 'SENT', 'N''oubliez pas votre réservation demain à 10h00.', NOW(), NULL);
