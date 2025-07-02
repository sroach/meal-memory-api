package gy.roach.health.mealmemoryapi.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "meals")
data class Meal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "image_path", nullable = false)
    val imagePath: String,

    @Column(name = "original_filename")
    val originalFilename: String?,

    @Column(name = "file_size")
    val fileSize: Long?,

    @Column(name = "timestamp")
    val timestamp: LocalDateTime?,

    @Column(name = "user_id")
    val userId: String?,

    @Column(name = "metadata", columnDefinition = "TEXT")
    val metadata: String?,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // No-arg constructor for JPA
    constructor() : this(
        id = 0,
        imagePath = "",
        originalFilename = null,
        fileSize = null,
        timestamp = null,
        userId = null,
        metadata = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}