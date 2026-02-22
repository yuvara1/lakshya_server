-- Create ENUM types
CREATE TYPE plan_type_enum AS ENUM ('FREE', 'PRO', 'ENTERPRISE');
CREATE TYPE user_role_enum AS ENUM ('ADMIN', 'MANAGER', 'EMPLOYEE');
CREATE TYPE project_status_enum AS ENUM ('ACTIVE', 'COMPLETED', 'ARCHIVED');
CREATE TYPE task_priority_enum AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');
CREATE TYPE task_status_enum AS ENUM ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE');
CREATE TYPE subscription_status_enum AS ENUM ('ACTIVE', 'CANCELLED', 'EXPIRED');

-- Tenant table
CREATE TABLE tenant (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        plan_type plan_type_enum NOT NULL DEFAULT 'FREE',
                        is_active BOOLEAN NOT NULL DEFAULT TRUE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,
                        deleted_at TIMESTAMP
);

-- App User table
CREATE TABLE app_user (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          role user_role_enum NOT NULL,
                          status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,
                          deleted_at TIMESTAMP
);

-- Project table
CREATE TABLE project (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         created_by UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
                         status project_status_enum NOT NULL DEFAULT 'ACTIVE',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP,
                         deleted_at TIMESTAMP
);

-- Task table
CREATE TABLE task (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
                      project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE,
                      assigned_to UUID REFERENCES app_user(id) ON DELETE SET NULL,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      priority task_priority_enum NOT NULL DEFAULT 'MEDIUM',
                      status task_status_enum NOT NULL DEFAULT 'TODO',
                      due_date DATE,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP,
                      deleted_at TIMESTAMP
);

-- Subscription table
CREATE TABLE subscription (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              tenant_id UUID NOT NULL UNIQUE REFERENCES tenant(id) ON DELETE CASCADE,
                              plan_type plan_type_enum NOT NULL,
                              start_date DATE NOT NULL,
                              end_date DATE,
                              status subscription_status_enum NOT NULL DEFAULT 'ACTIVE',
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP
);

-- Payment table
CREATE TABLE payment (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         tenant_id UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
                         amount DECIMAL(12, 2) NOT NULL,
                         payment_status VARCHAR(50) NOT NULL,
                         transaction_id VARCHAR(255) NOT NULL,
                         payment_provider VARCHAR(100),
                         payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better query performance
CREATE INDEX idx_app_user_tenant_id ON app_user(tenant_id);
CREATE INDEX idx_app_user_email_tenant ON app_user(email, tenant_id);
CREATE INDEX idx_project_tenant_id ON project(tenant_id);
CREATE INDEX idx_task_tenant_id ON task(tenant_id);
CREATE INDEX idx_task_project_id ON task(project_id);
CREATE INDEX idx_task_assigned_to ON task(assigned_to);
CREATE INDEX idx_payment_tenant_id ON payment(tenant_id);