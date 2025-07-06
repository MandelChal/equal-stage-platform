# Repository Methods Documentation

This document provides a comprehensive list of all available methods for the repository interfaces in the Equal Stage Platform.

## Table of Contents
- [LecturerRepository](#lecturerrepository)
- [LectureRepository](#lecturerepository)

---

## LecturerRepository

**Interface:** `LecturerRepository extends JpaRepository<Lecturer, UUID>`

### JpaRepository Methods (Inherited)

#### Save Operations
```java
// Save a single entity
save(Lecturer entity)

// Save multiple entities
saveAll(Iterable<Lecturer> entities)

// Save and flush immediately
saveAndFlush(Lecturer entity)
```

#### Find/Query Operations
```java
// Find by primary key, returns Optional<Lecturer>
findById(UUID id)

// Find all entities
findAll()

// Find all entities by their IDs
findAllById(Iterable<UUID> ids)

// Find all entities with sorting
findAll(Sort sort)

// Find all entities with pagination
findAll(Pageable pageable)
```

#### Count & Exists Operations
```java
// Count total number of entities
count()

// Check if entity exists by ID
existsById(UUID id)
```

#### Delete Operations
```java
// Delete by ID
deleteById(UUID id)

// Delete a specific entity
delete(Lecturer entity)

// Delete multiple entities by IDs
deleteAllById(Iterable<UUID> ids)

// Delete multiple entities
deleteAll(Iterable<Lecturer> entities)

// Delete all entities
deleteAll()
```

#### Flush Operations
```java
// Flush pending changes to database
flush()
```

### Custom Implemented Methods

#### Status-based Queries
```java
// Find lecturers by their status
findByStatus(LecturerStatus status)
```

#### Contact Information Queries
```java
// Find lecturer by email (returns Optional<Lecturer>)
findByEmail(String email)

// Find lecturer by phone (returns Optional<Lecturer>)
findByPhone(String phone)
```

#### Name Search
```java
// Search lecturers by first name or last name (case-insensitive, partial match)
findByNameContaining(String name)
```

#### Custom JPQL Queries
```java
// Count total number of lecturers using JPQL
countLecturers()

// Find lecturers who have at least one lecture assigned
findLecturersWithLectures()
```

### Complete Method Summary for LecturerRepository

| Category | Method | Return Type | Description |
|----------|--------|-------------|-------------|
| **Save** | `save(Lecturer entity)` | `Lecturer` | Save a single entity |
| **Save** | `saveAll(Iterable<Lecturer> entities)` | `List<Lecturer>` | Save multiple entities |
| **Save** | `saveAndFlush(Lecturer entity)` | `Lecturer` | Save and flush immediately |
| **Find** | `findById(UUID id)` | `Optional<Lecturer>` | Find by primary key |
| **Find** | `findAll()` | `List<Lecturer>` | Find all entities |
| **Find** | `findAllById(Iterable<UUID> ids)` | `List<Lecturer>` | Find all entities by IDs |
| **Find** | `findAll(Sort sort)` | `List<Lecturer>` | Find all entities with sorting |
| **Find** | `findAll(Pageable pageable)` | `Page<Lecturer>` | Find all entities with pagination |
| **Count** | `count()` | `long` | Count total number of entities |
| **Exists** | `existsById(UUID id)` | `boolean` | Check if entity exists by ID |
| **Delete** | `deleteById(UUID id)` | `void` | Delete by ID |
| **Delete** | `delete(Lecturer entity)` | `void` | Delete a specific entity |
| **Delete** | `deleteAllById(Iterable<UUID> ids)` | `void` | Delete multiple entities by IDs |
| **Delete** | `deleteAll(Iterable<Lecturer> entities)` | `void` | Delete multiple entities |
| **Delete** | `deleteAll()` | `void` | Delete all entities |
| **Flush** | `flush()` | `void` | Flush pending changes |
| **Custom** | `findByStatus(LecturerStatus status)` | `List<Lecturer>` | Find by status |
| **Custom** | `findByEmail(String email)` | `Optional<Lecturer>` | Find by email |
| **Custom** | `findByPhone(String phone)` | `Optional<Lecturer>` | Find by phone |
| **Custom** | `findByNameContaining(String name)` | `List<Lecturer>` | Search by name |
| **Custom** | `countLecturers()` | `Long` | Count lecturers (JPQL) |
| **Custom** | `findLecturersWithLectures()` | `List<Lecturer>` | Find lecturers with lectures |

---

## LectureRepository

**Interface:** `LectureRepository extends JpaRepository<Lecture, Long>`

### JpaRepository Methods (Inherited)

#### Save Operations
```java
// Save a single entity
save(Lecture entity)

// Save multiple entities
saveAll(Iterable<Lecture> entities)

// Save and flush immediately
saveAndFlush(Lecture entity)
```

#### Find/Query Operations
```java
// Find by primary key, returns Optional<Lecture>
findById(Long id)

// Find all entities
findAll()

// Find all entities by their IDs
findAllById(Iterable<Long> ids)

// Find all entities with sorting
findAll(Sort sort)

// Find all entities with pagination
findAll(Pageable pageable)
```

#### Count & Exists Operations
```java
// Count total number of entities
count()

// Check if entity exists by ID
existsById(Long id)
```

#### Delete Operations
```java
// Delete by ID
deleteById(Long id)

// Delete a specific entity
delete(Lecture entity)

// Delete multiple entities by IDs
deleteAllById(Iterable<Long> ids)

// Delete multiple entities
deleteAll(Iterable<Lecture> entities)

// Delete all entities
deleteAll()
```

#### Flush Operations
```java
// Flush pending changes to database
flush()
```

### Custom Implemented Methods

#### Title-based Query
```java
// Find lecture by title (returns Optional<Lecture>)
findByTitle(String title)
```

### Complete Method Summary for LectureRepository

| Category | Method | Return Type | Description |
|----------|--------|-------------|-------------|
| **Save** | `save(Lecture entity)` | `Lecture` | Save a single entity |
| **Save** | `saveAll(Iterable<Lecture> entities)` | `List<Lecture>` | Save multiple entities |
| **Save** | `saveAndFlush(Lecture entity)` | `Lecture` | Save and flush immediately |
| **Find** | `findById(Long id)` | `Optional<Lecture>` | Find by primary key |
| **Find** | `findAll()` | `List<Lecture>` | Find all entities |
| **Find** | `findAllById(Iterable<Long> ids)` | `List<Lecture>` | Find all entities by IDs |
| **Find** | `findAll(Sort sort)` | `List<Lecture>` | Find all entities with sorting |
| **Find** | `findAll(Pageable pageable)` | `Page<Lecture>` | Find all entities with pagination |
| **Count** | `count()` | `long` | Count total number of entities |
| **Exists** | `existsById(Long id)` | `boolean` | Check if entity exists by ID |
| **Delete** | `deleteById(Long id)` | `void` | Delete by ID |
| **Delete** | `delete(Lecture entity)` | `void` | Delete a specific entity |
| **Delete** | `deleteAllById(Iterable<Long> ids)` | `void` | Delete multiple entities by IDs |
| **Delete** | `deleteAll(Iterable<Lecture> entities)` | `void` | Delete multiple entities |
| **Delete** | `deleteAll()` | `void` | Delete all entities |
| **Flush** | `flush()` | `void` | Flush pending changes |
| **Custom** | `findByTitle(String title)` | `Optional<Lecture>` | Find by title |

---

## Key Differences

| Aspect | LecturerRepository | LectureRepository |
|--------|-------------------|-------------------|
| **Primary Key Type** | `UUID` | `Long` |
| **Custom Methods** | 7 methods | 1 method |
| **JPQL Queries** | 2 custom queries | 0 custom queries |
| **Search Capabilities** | Email, phone, name, status | Title only |
| **Complexity** | High (multiple search options) | Low (basic functionality) |

## Notes

- **LecturerRepository** provides extensive search capabilities including status filtering, contact information lookup, and name-based search
- **LectureRepository** is currently minimal with only title-based search
- Both repositories inherit the full set of CRUD operations from JpaRepository
- The primary key difference (`UUID` vs `Long`) affects method signatures for ID-based operations
- Consider adding more custom methods to LectureRepository based on business requirements (e.g., status filtering, date-based queries, etc.)

## Potential Enhancements for LectureRepository

Consider adding these methods to enhance LectureRepository functionality:

```java
// Status-based queries
findByStatus(LectureStatus status)

// Online/Offline filtering
findByIsOnline(boolean isOnline)

// Date-based queries
findByStartTimeAfter(LocalDateTime startTime)
findByEndTimeBefore(LocalDateTime endTime)

// Lecturer-based queries
findByLecturersContaining(Lecturer lecturer)

// Price-based queries
findByPriceBetween(int minPrice, int maxPrice)

// Location-based queries
findByLocationContainingIgnoreCase(String location)
``` 