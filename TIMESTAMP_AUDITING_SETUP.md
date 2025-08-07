# Auto Timestamp Auditing with Israel Time in Spring Boot

This document explains how automatic creation time and last updated time tracking has been implemented using Spring Boot JPA Auditing with Israel timezone.

## Overview

The solution automatically manages `createdAt` and `lastUpdatedAt` timestamps for all entities using:
- **JPA Auditing** with custom timezone handling
- **Base auditable entity** pattern for code reuse
- **Israel timezone** (Asia/Jerusalem) for all timestamps
- **Automatic updates** on entity persistence and modification

## Implementation Components

### 1. Base Auditable Entity (`BaseAuditableEntity.java`)

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = TimeUtils.nowInIsrael();
        createdAt = now;
        lastUpdatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = TimeUtils.nowInIsrael();
    }
}
```

**Key Features:**
- `@MappedSuperclass`: Allows inheritance without creating a table
- `@EntityListeners`: Enables JPA auditing callbacks
- `@PrePersist`: Triggers before entity creation
- `@PreUpdate`: Triggers before entity updates
- Uses `TimeUtils.nowInIsrael()` for consistent timezone handling

### 2. Custom DateTimeProvider (`IsraelTimeAuditorAware.java`)

```java
@Component
public class IsraelTimeAuditorAware implements DateTimeProvider {
    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(TimeUtils.nowInIsrael());
    }
}
```

**Purpose:**
- Provides Israel timezone for JPA auditing annotations
- Ensures `@CreatedDate` and `@LastModifiedDate` use Israel time

### 3. JPA Auditing Configuration (`JpaAuditingConfig.java`)

```java
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "israelTimeAuditorAware")
public class JpaAuditingConfig {
    @Bean
    public DateTimeProvider israelTimeAuditorAware() {
        return new IsraelTimeAuditorAware();
    }
}
```

**Purpose:**
- Enables JPA auditing application-wide
- Configures custom DateTimeProvider for Israel timezone

### 4. Updated Entity Classes

All entities now extend `BaseAuditableEntity`:

```java
@Entity
@EqualsAndHashCode(callSuper = false)
public class Lecturer extends BaseAuditableEntity {
    // No more manual timestamp fields or handling
    // Timestamps are inherited from BaseAuditableEntity
}

@Entity
@EqualsAndHashCode(callSuper = false)
public class User extends BaseAuditableEntity {
    // No more manual timestamp fields or handling
}

@Entity
@EqualsAndHashCode(callSuper = false)
public class Lecture extends BaseAuditableEntity {
    // No more manual timestamp fields or handling
}
```

## What Changed

### Before (Manual Timestamp Management)
```java
// Manual fields in each entity
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(name = "last_updated_at", nullable = false)
private LocalDateTime lastUpdatedAt;

// Manual timestamp setting in constructors
LocalDateTime now = TimeUtils.nowInIsrael();
this.createdAt = now;
this.lastUpdatedAt = now;

// Manual updates in methods
public void enrollLecture(Lecture lecture) {
    this.lastUpdatedAt = TimeUtils.nowInIsrael();
    // ... rest of method
}
```

### After (Automatic Auditing)
```java
// Entities simply extend BaseAuditableEntity
public class Lecturer extends BaseAuditableEntity {
    // Timestamps are automatically handled
}

// No manual timestamp code needed
public void enrollLecture(Lecture lecture) {
    // lastUpdatedAt automatically updated when entity is saved
    // ... business logic only
}
```

## Benefits

### 1. **Consistency**
- All timestamps use Israel timezone automatically
- No risk of mixed timezones or forgotten updates

### 2. **Maintainability**
- Centralized timestamp logic in one place
- Changes to timestamp behavior affect all entities
- Reduced code duplication

### 3. **Reliability**
- Impossible to forget updating timestamps
- JPA handles all persistence events automatically
- Thread-safe and transaction-aware

### 4. **Clean Code**
- Entity constructors focus on business logic only
- Methods don't need timestamp management code
- Clear separation of concerns

## How It Works

### On Entity Creation
1. Entity is created (e.g., `new User(...)`)
2. Entity is saved to repository
3. `@PrePersist` callback triggers `onCreate()`
4. Both `createdAt` and `lastUpdatedAt` set to Israel time

### On Entity Update
1. Entity is modified (e.g., `user.setEmail(...)`)
2. Entity is saved to repository
3. `@PreUpdate` callback triggers `onUpdate()`
4. Only `lastUpdatedAt` is updated to current Israel time
5. `createdAt` remains unchanged (marked as non-updatable)

### Timezone Handling
- All timestamps use `ZonedDateTime.now(ZoneId.of("Asia/Jerusalem"))`
- Automatically handles daylight saving time transitions
- Consistent with existing `TimeUtils.nowInIsrael()` method

## Usage Examples

### Creating Entities
```java
// Timestamps automatically set on save
User user = new User("email@example.com", "password");
userRepository.save(user);
// createdAt and lastUpdatedAt now contain Israel time
```

### Updating Entities
```java
// Timestamp automatically updated on save
user.setEmail("newemail@example.com");
userRepository.save(user);
// lastUpdatedAt now updated, createdAt unchanged
```

### Business Method Calls
```java
// Any entity modification triggers timestamp update
lecturer.enrollLecture(lecture);
lecturerRepository.save(lecturer);
// lastUpdatedAt automatically updated
```

## Database Schema

The database schema remains the same:
- `created_at` column: Stores creation timestamp
- `last_updated_at` column: Stores last modification timestamp

## Testing

To verify the implementation:
1. Create and save a new entity
2. Check that both timestamps are set to Israel time
3. Modify and save the entity
4. Verify that only `lastUpdatedAt` is updated
5. Confirm all timestamps use Israel timezone

This implementation provides robust, automatic timestamp management while maintaining consistency with Israel timezone requirements.