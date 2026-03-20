DROP TABLE IF EXISTS assessment_ai_log;
DROP TABLE IF EXISTS assessment_result;
DROP TABLE IF EXISTS assessment_answer;
DROP TABLE IF EXISTS assessment_session;
DROP TABLE IF EXISTS assessment_item_option;
DROP TABLE IF EXISTS assessment_item;
DROP TABLE IF EXISTS student_profile;
DROP TABLE IF EXISTS assessment_stage_rule;

CREATE TABLE assessment_stage_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stage_code VARCHAR(32) NOT NULL UNIQUE,
    stage_name VARCHAR(64) NOT NULL,
    start_difficulty DECIMAL(6,2) NOT NULL,
    min_difficulty DECIMAL(6,2) NOT NULL,
    max_difficulty DECIMAL(6,2) NOT NULL,
    route_question_count INT NOT NULL,
    core_question_count INT NOT NULL,
    anchor_question_count INT NOT NULL,
    stop_confidence DECIMAL(5,2) NOT NULL,
    result_mapping_json JSON NOT NULL,
    rule_version VARCHAR(32) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_stage_rule_stage_status (stage_code, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE assessment_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_code VARCHAR(64) NOT NULL UNIQUE,
    stage_code VARCHAR(32) NOT NULL,
    question_type VARCHAR(32) NOT NULL,
    ability_type VARCHAR(32) NOT NULL,
    word_text VARCHAR(128) NOT NULL,
    stem_text VARCHAR(500) NULL,
    stem_audio_url VARCHAR(500) NULL,
    stem_image_url VARCHAR(500) NULL,
    difficulty_score DECIMAL(6,2) NOT NULL,
    discrimination_score DECIMAL(6,2) NULL,
    is_anchor TINYINT NOT NULL DEFAULT 0,
    review_status VARCHAR(32) NOT NULL,
    bank_version VARCHAR(32) NOT NULL,
    content_source VARCHAR(32) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_assessment_item_stage_ability_difficulty_status (stage_code, ability_type, difficulty_score, status),
    KEY idx_assessment_item_code (item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE assessment_item_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT NOT NULL,
    option_key VARCHAR(8) NOT NULL,
    option_text VARCHAR(255) NULL,
    option_audio_url VARCHAR(500) NULL,
    option_image_url VARCHAR(500) NULL,
    is_correct TINYINT NOT NULL DEFAULT 0,
    sort_no INT NOT NULL,
    created_at DATETIME NOT NULL,
    KEY idx_item_option_item_sort (item_id, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE student_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_name VARCHAR(64) NOT NULL,
    grade_code VARCHAR(32) NOT NULL,
    birth_year INT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_student_profile_grade_code (grade_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE assessment_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_no VARCHAR(64) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    selected_stage_code VARCHAR(32) NOT NULL,
    start_difficulty DECIMAL(6,2) NOT NULL,
    current_difficulty DECIMAL(6,2) NOT NULL,
    rule_version VARCHAR(32) NOT NULL,
    bank_version VARCHAR(32) NOT NULL,
    ai_enabled TINYINT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    answered_count INT NOT NULL DEFAULT 0,
    correct_count INT NOT NULL DEFAULT 0,
    uncertain_count INT NOT NULL DEFAULT 0,
    started_at DATETIME NOT NULL,
    finished_at DATETIME NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_assessment_session_student_created (student_id, created_at),
    KEY idx_assessment_session_no (session_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE assessment_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    question_no INT NOT NULL,
    phase_type VARCHAR(32) NOT NULL,
    selected_option_id BIGINT NULL,
    answer_status VARCHAR(32) NOT NULL,
    is_correct TINYINT NOT NULL DEFAULT 0,
    response_time_ms INT NULL,
    difficulty_before DECIMAL(6,2) NOT NULL,
    difficulty_after DECIMAL(6,2) NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_assessment_answer_session_question (session_id, question_no),
    KEY idx_assessment_answer_session_item (session_id, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE assessment_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL UNIQUE,
    estimated_stage_code VARCHAR(32) NOT NULL,
    vocab_estimate_min INT NOT NULL,
    vocab_estimate_max INT NOT NULL,
    vocab_estimate_mid INT NOT NULL,
    reading_score DECIMAL(6,2) NOT NULL,
    listening_score DECIMAL(6,2) NOT NULL,
    context_score DECIMAL(6,2) NOT NULL,
    confidence_score DECIMAL(6,2) NOT NULL,
    summary_text VARCHAR(1000) NOT NULL,
    recommendation_text VARCHAR(1000) NOT NULL,
    basis_json JSON NOT NULL,
    created_at DATETIME NOT NULL,
    KEY idx_assessment_result_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE assessment_ai_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NULL,
    item_id BIGINT NULL,
    step_type VARCHAR(32) NOT NULL,
    model_name VARCHAR(64) NOT NULL,
    prompt_version VARCHAR(32) NOT NULL,
    input_snapshot JSON NOT NULL,
    output_snapshot JSON NOT NULL,
    decision_summary VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL,
    KEY idx_assessment_ai_log_session_step_created (session_id, step_type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
